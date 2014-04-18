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
import com.sonymobile.gitlab.model.DetailedGitLabUserInfo;
import com.sonymobile.gitlab.model.FullGitLabUserInfo;
import com.sonymobile.gitlab.model.GitLabGroupInfo;
import com.sonymobile.gitlab.model.GitLabSessionInfo;
import org.apache.http.HttpHost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /** The URL of the host server excluding the path. */
    private final String host;

    /** The private token used to authenticate the connection. */
    private final String privateToken;

    /** The used proxy host (or null if proxy is not used). */
    private final String proxyHost;

    /** The used proxy port. */
    private final int proxyPort;

    /**
     * Creates a GitLab API client.
     *
     * @param host         the URL of the host server (excluding the path)
     * @param privateToken the private token used to authenticate the connection
     */
    public GitLabApiClient(final String host, final String privateToken) {
        // initialize without a proxy
        this(host, privateToken, null, 0);
    }

    /**
     * Creates a GitLab API client connecting using a proxy server.
     *
     * @param host         the URL of the host server (excluding the path)
     * @param privateToken the private token used to authenticate the connection
     * @param proxyHost    the used proxy host
     * @param proxyPort    the used proxy port
     */
    public GitLabApiClient(final String host, final String privateToken, final String proxyHost, final int proxyPort) {
        this.host = host;
        this.privateToken = privateToken;
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;

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
     * @throws ApiConnectionFailureException if the connection with the API failed
     * @throws AuthenticationFailedException if the authentication failed because of bad user credentials
     */
    public static GitLabApiClient openSession(final String host, final String login, final String password)
            throws ApiConnectionFailureException, AuthenticationFailedException {
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
     * @param proxyHost the used proxy host
     * @param proxyPort the used proxy port
     * @return a GitLab API client
     * @throws ApiConnectionFailureException if the connection with the API failed
     * @throws AuthenticationFailedException if the authentication failed because of bad user credentials
     */
    public static GitLabApiClient openSession(final String host, final String login, final String password,
                                              final String proxyHost, final int proxyPort)
            throws ApiConnectionFailureException, AuthenticationFailedException {
        // connect to API and create a session with the user credentials
        final GitLabSessionInfo session = new GitLabApiClient(host, null, proxyHost, proxyPort).getSession(login,
                password);
        // use token from session to create a client
        return new GitLabApiClient(host, session.getPrivateToken(), proxyHost, proxyPort);
    }

    /**
     * Fetches a session to the API using user credentials.
     *
     * @param login    the username of the user
     * @param password the password of the user
     * @return a session object
     * @throws ApiConnectionFailureException if the connection with the API failed
     * @throws AuthenticationFailedException if the authentication failed because of bad user credentials
     */
    public GitLabSessionInfo getSession(final String login, final String password)
            throws ApiConnectionFailureException, AuthenticationFailedException {
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
     * @throws ApiConnectionFailureException if the connection with the API failed
     * @throws AuthenticationFailedException if the authentication failed because of bad user credentials
     */
    public List<GitLabGroupInfo> getGroups()
            throws ApiConnectionFailureException, AuthenticationFailedException {
        // get the json array with the groups from the response
        JSONArray jsonArray = get("/groups", null).getBody().getArray();

        // convert all objects in the json array to groups
        ArrayList<GitLabGroupInfo> groups = new ArrayList<GitLabGroupInfo>(jsonArray.length());
        for (int index = 0; index < jsonArray.length(); index++) {
            groups.add(new GitLabGroupInfo(jsonArray.getJSONObject(index)));
        }

        return groups;
    }

    /**
     * Returns the user the API is authenticated with.
     *
     * The authenticated user is the owner of the private token.
     *
     * @return the authenticated user
     * @throws ApiConnectionFailureException if the connection with the API failed
     * @throws AuthenticationFailedException if the authentication failed because of bad user credentials
     */
    public DetailedGitLabUserInfo getCurrentUser()
            throws ApiConnectionFailureException, AuthenticationFailedException {
        // create a user object with the response
        return new FullGitLabUserInfo(get("/user", null).getBody().getObject());
    }

    /**
     * Returns the URL of the host server.
     *
     * @return an URL
     */
    public String getHost() {
        return host;
    }

    /**
     * Returns the private token.
     *
     * @return a private token
     */
    public String getPrivateToken() {
        return privateToken;
    }

    /**
     * Returns the URL of the proxy.
     *
     * @return a URL or null if not set
     */
    public String getProxyHost() {
        return proxyHost;
    }

    /**
     * Returns the port of the proxy.
     *
     * @return a port number
     */
    public int getProxyPort() {
        return proxyPort;
    }

    /**
     * Returns the URL of the API.
     *
     * @return an URL
     */
    private String getApiUrl() {
        return host + "/api/v3";
    }

    /**
     * Tests if a connection can be established with the given parameters.
     *
     * @param host         the GitLab host URL
     * @param privateToken the GitLab private token
     * @param proxyHost    the http proxy host
     * @param proxyPort    the http proxy port
     * @throws ApiConnectionFailureException if a connection to the API could not be found
     * @throws AuthenticationFailedException if the private token is incorrect
     */
    public static void testConnection(String host, String privateToken, String proxyHost, int proxyPort)
            throws ApiConnectionFailureException, AuthenticationFailedException {
        new GitLabApiClient(host, privateToken, proxyHost, proxyPort).getCurrentUser();
    }

    /**
     * Creates a new client assuming the identity of another user.
     *
     * @param privateToken a private token for the other user
     * @return a new API client
     */
    public GitLabApiClient impersonate(String privateToken) {
        return new GitLabApiClient(host, privateToken, proxyHost, proxyPort);
    }

    /**
     * Create the HTTP client.
     *
     * If a proxy is specified this will be used for the client.
     */
    private void initializeHttpClient() {
        // use proxy settings etc from system properties
        final HttpClientBuilder builder = HttpClientBuilder.create().useSystemProperties();
        // override proxy settings if the proxy host is set
        if (proxyHost != null) {
            builder.setProxy(new HttpHost(proxyHost, proxyPort));
        }

        Unirest.setHttpClient(builder.build());
    }

    /**
     * Makes a GET request to the API with the private token.
     *
     * @param path   the path relative to the API
     * @param fields the fields for the request (can be null)
     * @return an HTTP response containing a JSON body
     * @throws ApiConnectionFailureException if a connection to the API could not be found
     * @throws AuthenticationFailedException if the private token is incorrect
     */
    private HttpResponse<JsonNode> get(String path, Map<String, Object> fields)
            throws ApiConnectionFailureException, AuthenticationFailedException {
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
     * @throws ApiConnectionFailureException if a connection to the API could not be found
     * @throws AuthenticationFailedException if the private token is incorrect
     */
    private HttpResponse<JsonNode> get(String path, Map<String, Object> fields, boolean includePrivateToken)
            throws ApiConnectionFailureException, AuthenticationFailedException {
        final GetRequest request = Unirest.get(getApiUrl() + path);

        request.fields(fields);
        if (includePrivateToken) {
            request.field("private_token", privateToken);
        }

        final HttpResponse<JsonNode> response;
        try {
            response = request.asJson();
        } catch (UnirestException e) {
            throw new ApiConnectionFailureException("Could not connect to API", e);
        }

        // check if the request was successful
        if (response.getCode() != HTTP_200_OK) {
            throw new AuthenticationFailedException("Invalid private token");
        }

        return response;
    }

    /**
     * Make a POST request to the API with the private token.
     *
     * @param path   the path relative to the API
     * @param fields the fields for the request
     * @return an HTTP response containing a JSON body
     * @throws ApiConnectionFailureException if a connection to the API could not be found
     * @throws AuthenticationFailedException if the private token is incorrect
     */
    private HttpResponse<JsonNode> post(String path, Map<String, Object> fields)
            throws ApiConnectionFailureException, AuthenticationFailedException {
        // include private token in request
        return post(path, fields, true);
    }

    /**
     * Make a POST request to the API.
     *
     * @param path                the path relative to the API
     * @param fields              the fields for the request
     * @param includePrivateToken if the private token should be added to the fields
     * @return an HTTP response containing a JSON body
     * @throws ApiConnectionFailureException if a connection to the API could not be found
     * @throws AuthenticationFailedException if the private token is incorrect
     */
    private HttpResponse<JsonNode> post(String path, Map<String, Object> fields, boolean includePrivateToken)
            throws ApiConnectionFailureException, AuthenticationFailedException {
        HttpRequestWithBody request = Unirest.post(getApiUrl() + path);

        final MultipartBody body = request.fields(fields);
        if (includePrivateToken) {
            body.field("private_token", privateToken);
        }

        final HttpResponse<JsonNode> response;
        try {
            response = request.asJson();
        } catch (UnirestException e) {
            throw new ApiConnectionFailureException("Could not connect to API", e);
        }

        // check if the request was successful
        if (response.getCode() != HTTP_201_CREATED) {
            throw new AuthenticationFailedException("Invalid private token");
        }

        return response;
    }
}
