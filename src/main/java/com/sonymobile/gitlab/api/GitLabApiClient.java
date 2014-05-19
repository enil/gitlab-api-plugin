/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Andreas Alanko, Emil Nilsson, Sony Mobile Communications AB.
 * All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.sonymobile.gitlab.api;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import com.mashape.unirest.request.body.MultipartBody;
import com.sonymobile.gitlab.exceptions.ApiConnectionFailureException;
import com.sonymobile.gitlab.exceptions.AuthenticationFailedException;
import com.sonymobile.gitlab.exceptions.GitLabApiException;
import com.sonymobile.gitlab.exceptions.GroupNotFoundException;
import com.sonymobile.gitlab.exceptions.NotFoundException;
import com.sonymobile.gitlab.exceptions.UserNotFoundException;
import com.sonymobile.gitlab.http.PatternProxyRoutePlanner;
import com.sonymobile.gitlab.model.FullGitLabUserInfo;
import com.sonymobile.gitlab.model.GitLabGroupInfo;
import com.sonymobile.gitlab.model.GitLabGroupMemberInfo;
import com.sonymobile.gitlab.model.GitLabSessionInfo;
import com.sonymobile.gitlab.model.GitLabUserInfo;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.sonymobile.gitlab.helpers.JSONArrayIterator.iterator;
import static java.util.Collections.unmodifiableList;

/**
 * A client for communicating with a GitLab API.
 *
 * @author Emil Nilsson
 */
public class GitLabApiClient {
    /** HTTP status code 200 OK. */
    private static final int HTTP_200_OK = 200;

    /** HTTP status code 201 Created. */
    private static final int HTTP_201_CREATED = 201;

    /** HTTP status code 404 Not Found. */
    private static final int HTTP_404_NOT_FOUND = 404;

    /** The URL of the host server excluding the path. */
    private final String host;

    /** The private token used to authenticate the connection. */
    private final String privateToken;

    /** The proxy host name (or null if proxy is not used). */
    private final String proxyHost;

    /** The proxy port. */
    private final int proxyPort;

    /** The proxy user (or null if proxy credentials are not used). */
    private final String proxyUser;

    /** The proxy password (or null if proxy credentials are not used). */
    private final String proxyPassword;

    /** The hosts excluded from the proxy (or null if no exclusions are used). */
    private final List<Pattern> excludedHostnames;

    /**
     * The
     *
     * /** Creates a GitLab API client.
     *
     * @param host         the URL of the host server (excluding the path)
     * @param privateToken the private token used to authenticate the connection
     */
    public GitLabApiClient(String host, String privateToken) {
        // initialize without a proxy
        this(host, privateToken, null, 0);
    }

    /**
     * Creates a GitLab API client connecting using a proxy server.
     *
     * @param host         the URL of the host server (excluding the path)
     * @param privateToken the private token used to authenticate the connection
     * @param proxyHost    the proxy host name
     * @param proxyPort    the proxy port
     */
    public GitLabApiClient(String host, String privateToken, String proxyHost, int proxyPort) {
        // initialize without proxy credentials
        this(host, privateToken, proxyHost, proxyPort, null, null);
    }

    /**
     * Creates a GitLab API client connecting using a proxy server with credentials.
     *
     * @param host          the URL of the host server (excluding the path)
     * @param privateToken  the private token used to authenticate the connection
     * @param proxyHost     the proxy host name
     * @param proxyPort     the proxy port
     * @param proxyUser     the proxy user
     * @param proxyPassword the proxy password
     */
    public GitLabApiClient(String host, String privateToken,
                           String proxyHost, int proxyPort,
                           String proxyUser, String proxyPassword) {
        this(host, privateToken, proxyHost, proxyPort, proxyUser, proxyPassword, null);
    }

    /**
     * Creates a GitLab API client connecting using a proxy server with credentials and a list of hosts excluded from
     * the proxy.
     *
     * @param host              the URL of the host server (excluding the path)
     * @param privateToken      the private token used to authenticate the connection
     * @param proxyHost         the proxy host name
     * @param proxyPort         the proxy port
     * @param proxyUser         the proxy user
     * @param proxyPassword     the proxy password
     * @param excludedHostnames the excluded hosts
     */
    public GitLabApiClient(String host, String privateToken,
                           String proxyHost, int proxyPort,
                           String proxyUser, String proxyPassword,
                           List<Pattern> excludedHostnames) {
        this.host = host;
        this.privateToken = privateToken;
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        this.proxyUser = proxyUser;
        this.proxyPassword = proxyPassword;
        this.excludedHostnames = excludedHostnames;

        // create the HTTP client used by Unirest
        initializeHttpClient();
    }

    /**
     * Opens a session with the API using user credentials.
     *
     * This may be used to create a GitLab API client without a private token using only the username and password.
     *
     * @param host     the URL of the host server (excluding the path)
     * @param login    the username of the user
     * @param password the password of the user
     * @return a GitLab API client
     * @throws GitLabApiException if the request failed
     */
    public static final GitLabApiClient openSession(String host, String login, String password)
            throws GitLabApiException {
        // open session without setting a proxy
        return openSession(host, login, password, null, 0);
    }

    /**
     * Opens a session with the API using user credentials connecting using a proxy server.
     *
     * This may be used to create a GitLab API client without a private token using only the username and password.
     *
     * @param host      the URL of the host server (excluding the path)
     * @param login     the username of the user
     * @param password  the password of the user
     * @param proxyHost the proxy host name
     * @param proxyPort the proxy port
     * @return a GitLab API client
     * @throws GitLabApiException if the request failed
     */
    public static final GitLabApiClient openSession(String host,
                                                    String login, String password,
                                                    String proxyHost, int proxyPort)
            throws GitLabApiException {
        // connect to API and create a session with the user credentials
        final GitLabSessionInfo session = new GitLabApiClient(host, null, proxyHost, proxyPort).getSession(login,
                password);
        // use token from session to create a client
        return new GitLabApiClient(host, session.getPrivateToken(), proxyHost, proxyPort);
    }

    /**
     * Tests if a connection can be established with the given parameters.
     *
     * @param host              the URL of the host server (excluding the path)
     * @param privateToken      the private token used to authenticate the connection
     * @param proxyHost         the proxy host name
     * @param proxyPort         the proxy port
     * @param proxyUser         the proxy user
     * @param proxyPassword     the proxy password
     * @param excludedHostnames the excluded hosts
     * @throws GitLabApiException if the request failed
     */
    public static final void testConnection(String host, String privateToken,
                                            String proxyHost, int proxyPort,
                                            String proxyUser, String proxyPassword,
                                            List<Pattern> excludedHostnames)
            throws GitLabApiException {
        new GitLabApiClient(
                host,
                privateToken,
                proxyHost,
                proxyPort,
                proxyUser,
                proxyPassword,
                excludedHostnames).getCurrentUser();
    }

    /**
     * Fetches a session to the API using user credentials.
     *
     * @param login    the username of the user
     * @param password the password of the user
     * @return a session object
     * @throws GitLabApiException if the request failed
     */
    public final GitLabSessionInfo getSession(String login, String password)
            throws GitLabApiException {
        final Map<String, Object> fields = new HashMap<String, Object>();
        fields.put("login", login);
        fields.put("password", password);

        // create a session object with the response
        return new GitLabSessionInfo(post("/session", fields, false).getBody().getObject());
    }

    /**
     * Fetches the group the authenticated user can see.
     *
     * Admin users can see all groups, others can only see groups they are members of.
     *
     * @return a list of groups
     * @throws GitLabApiException if the request failed
     */
    public final List<GitLabGroupInfo> getGroups()
            throws GitLabApiException {
        // get the json array with the groups from the response
        JSONArray jsonArray = get("/groups", null).getBody().getArray();

        // convert all objects in the json array to groups
        ArrayList<GitLabGroupInfo> groups = new ArrayList<GitLabGroupInfo>(jsonArray.length());
        for (final JSONObject jsonObject : iterator(jsonArray)) {
            groups.add(new GitLabGroupInfo(jsonObject));
        }

        return groups;
    }

    /**
     * Returns the group with a specific group ID.
     *
     * @param groupId a group ID
     * @return the group
     * @throws GitLabApiException if the request failed or if the group is missing
     */
    public final GitLabGroupInfo getGroup(int groupId)
            throws GitLabApiException {
        try {
            // create a group object with the response
            return new GitLabGroupInfo(get("/groups/" + groupId, null).getBody().getObject());
        } catch (NotFoundException e) {
            throw new GroupNotFoundException("A group with group ID " + groupId + " does not exist");
        }
    }

    /**
     * Fetches the members of a group.
     *
     * @param groupId an ID of a group
     * @return the members of the group
     * @throws GitLabApiException if the request failed or if the group is missing
     */
    public final List<GitLabGroupMemberInfo> getGroupMembers(int groupId)
            throws GitLabApiException {
        JSONArray jsonArray;
        try {
            // get the json array with the group members from the response
            jsonArray = get("/groups/" + groupId + "/members", null).getBody().getArray();
        } catch (NotFoundException e) {
            throw new GroupNotFoundException("A group with group ID " + groupId + " does not exist");
        }

        // convert all objects in the json array to groups
        ArrayList<GitLabGroupMemberInfo> members = new ArrayList<GitLabGroupMemberInfo>(jsonArray.length());
        for (final JSONObject jsonObject : iterator(jsonArray)) {
            members.add(new GitLabGroupMemberInfo(jsonObject, groupId));
        }

        return members;
    }

    /**
     * Fetches all users from the system.
     *
     * @return a list of all users
     * @throws GitLabApiException if the request failed
     */
    public final List<GitLabUserInfo> getUsers()
            throws GitLabApiException {
        // get the json array with the users from the response
        JSONArray jsonArray = get("/users", null).getBody().getArray();

        // convert all objects in the json array to users
        ArrayList<GitLabUserInfo> users = new ArrayList<GitLabUserInfo>(jsonArray.length());
        for (final JSONObject jsonObject : iterator(jsonArray)) {
            users.add(new FullGitLabUserInfo(jsonObject));
        }

        return users;
    }

    /**
     * Returns the user the API is authenticated with.
     *
     * The authenticated user is the owner of the private token.
     *
     * @return the authenticated user
     * @throws GitLabApiException if the request failed
     */
    public final GitLabUserInfo getCurrentUser()
            throws GitLabApiException {
        // create a user object with the response
        return new FullGitLabUserInfo(get("/user", null).getBody().getObject());
    }

    /**
     * Returns the user with a specific user ID.
     *
     * @param userId a user ID
     * @return the user
     * @throws GitLabApiException if the request failed
     */
    public final GitLabUserInfo getUser(int userId)
            throws GitLabApiException {
        try {
            // create a user object with the response
            return new FullGitLabUserInfo(get("/users/" + userId, null).getBody().getObject());
        } catch (NotFoundException e) {
            throw new UserNotFoundException("A user with group ID " + userId + " does not exist");
        }
    }

    /**
     * Returns the URL of the host server.
     *
     * @return the URL
     */
    public final String getHost() {
        return host;
    }

    /**
     * Returns the private token.
     *
     * @return the private token
     */
    public final String getPrivateToken() {
        return privateToken;
    }

    /**
     * Returns the URL of the proxy.
     *
     * @return the URL or null if not set
     */
    public final String getProxyHost() {
        return proxyHost;
    }

    /**
     * Returns the port of the proxy.
     *
     * @return the port number
     */
    public final int getProxyPort() {
        return proxyPort;
    }

    /**
     * Returns the proxy user.
     *
     * @return the user
     */
    public final String getProxyUser() {
        return proxyUser;
    }

    /**
     * Returns the proxy password.
     *
     * @return the password
     */
    public final String getProxyPassword() {
        return proxyPassword;
    }

    /**
     * Returns the excluded hosts.
     *
     * @return a list of hostname patterns
     */
    public final List<Pattern> getExcludedHostnames() {
        return unmodifiableList(excludedHostnames);
    }

    /**
     * Returns the URL of the API.
     *
     * @return an URL
     */
    private final String getApiUrl() {
        return host + "/api/v3";
    }

    /**
     * Returns a client impersonating another user.
     *
     * The returned client will have the same access as the impersonated user.
     * Impersonating users is only possible when authenticated with the private token of an administrator user.
     *
     * @param userId the user ID of the impersonated user
     * @return a API client for the impersonated user
     */
    public final GitLabApiClient asUser(int userId) {
        return new ImpersonatingGitLabApiClient(
                userId,
                host, privateToken,
                proxyHost, proxyPort,
                proxyUser, proxyPassword,
                excludedHostnames);
    }

    /**
     * Creates the HTTP client.
     *
     * If a proxy is specified this will be used for the client.
     */
    protected void initializeHttpClient() {
        // use proxy settings etc from system properties
        final HttpClientBuilder builder = HttpClientBuilder.create().useSystemProperties();
        // override proxy settings if the proxy host is set
        if (proxyHost != null) {
            HttpHost proxy = new HttpHost(proxyHost, proxyPort);

            builder.setProxy(proxy);

            // use proxy credentials if proxy user is set
            if (proxyUser != null) {
                CredentialsProvider proxyCredentialsProvider = new BasicCredentialsProvider();
                Credentials proxyCredentials = new UsernamePasswordCredentials(proxyUser, proxyPassword);
                proxyCredentialsProvider.setCredentials(new AuthScope(proxyHost, proxyPort), proxyCredentials);

                builder.setDefaultCredentialsProvider(proxyCredentialsProvider);
            }

            // exclude hosts if excluded hosts lists is set
            if (excludedHostnames != null) {
                builder.setRoutePlanner(new PatternProxyRoutePlanner(proxy, excludedHostnames));
            }
        }

        Unirest.setHttpClient(builder.build());
    }

    /**
     * Makes a GET request to the API with the private token.
     *
     * @param path   the path relative to the API
     * @param fields the fields for the request (can be null)
     * @return an HTTP response containing a JSON body
     * @throws GitLabApiException if the request failed
     */
    protected final HttpResponse<JsonNode> get(String path, Map<String, Object> fields)
            throws GitLabApiException {
        // include private token in request
        return get(path, fields, true);
    }

    /**
     * Makes a GET request to the API.
     *
     * @param path                the path relative to the API
     * @param fields              the fields for the request (can be null)
     * @param includePrivateToken if the private token should be added to the fields
     * @return an HTTP response containing a JSON body
     * @throws GitLabApiException if the request failed
     */
    protected HttpResponse<JsonNode> get(String path, Map<String, Object> fields, boolean includePrivateToken)
            throws GitLabApiException {
        final GetRequest request = Unirest.get(getApiUrl() + path);

        request.fields(fields);
        if (includePrivateToken) {
            request.field("private_token", privateToken);
        }

        try {
            // make request
            return processGetResponse(request.asJson());
        } catch (UnirestException e) {
            throw new ApiConnectionFailureException("Could not connect to API", e);
        }
    }

    /**
     * Processes a GET response.
     *
     * Used by {@link #get(String, Map, boolean)} to handle the HTTP response.
     *
     * @param response the HTTP response object
     * @return a response object if the request succeeded
     * @throws GitLabApiException if the request failed
     */
    protected HttpResponse<JsonNode> processGetResponse(HttpResponse<JsonNode> response)
            throws GitLabApiException {
        // check if the request was successful
        switch (response.getCode()) {
            case HTTP_200_OK:
                // request succeeded
                return response;
            case HTTP_404_NOT_FOUND:
                throw new NotFoundException("Resource not found");
            default:
                throw new AuthenticationFailedException("Invalid private token");
        }
    }

    /**
     * Make a POST request to the API with the private token.
     *
     * @param path   the path relative to the API
     * @param fields the fields for the request
     * @return an HTTP response containing a JSON body
     * @throws ApiConnectionFailureException if a connection to the API could not be found
     * @throws AuthenticationFailedException if the private token is incorrect
     * @throws NotFoundException             if the resource wasn't found
     */
    protected final HttpResponse<JsonNode> post(String path, Map<String, Object> fields)
            throws GitLabApiException {
        // include private token in request
        return post(path, fields, true);
    }

    /**
     * Makes a POST request to the API.
     *
     * @param path                the path relative to the API
     * @param fields              the fields for the request
     * @param includePrivateToken if the private token should be added to the fields
     * @return an HTTP response containing a JSON body
     * @throws GitLabApiException if the request failed
     */
    protected HttpResponse<JsonNode> post(String path, Map<String, Object> fields, boolean includePrivateToken)
            throws GitLabApiException {
        HttpRequestWithBody request = Unirest.post(getApiUrl() + path);

        final MultipartBody body = request.fields(fields);
        if (includePrivateToken) {
            body.field("private_token", privateToken);
        }

        try {
            // make request
            return processPostResponse(request.asJson());
        } catch (UnirestException e) {
            throw new ApiConnectionFailureException("Could not connect to API", e);
        }
    }

    /**
     * Processes a POST response.
     *
     * Used by {@link #post(String, Map, boolean)} to handle the HTTP response.
     *
     * @param response the HTTP response object
     * @return a response object if the request succeeded
     * @throws GitLabApiException if the request failed
     */
    protected HttpResponse<JsonNode> processPostResponse(HttpResponse<JsonNode> response)
            throws GitLabApiException {
        // check if the request was successful
        switch (response.getCode()) {
            case HTTP_201_CREATED:
                // request succeeded
                return response;
            case HTTP_404_NOT_FOUND:
                throw new NotFoundException("Resource not found");
            default:
                throw new AuthenticationFailedException("Invalid private token");
        }
    }
}
