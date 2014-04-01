/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Sony Mobile Communications AB. All rights reserved.
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
import com.sonymobile.gitlab.GitLabSession;
import com.sonymobile.gitlab.exceptions.ApiConnectionFailureException;
import com.sonymobile.gitlab.exceptions.AuthenticationFailedException;
import org.apache.http.HttpHost;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * A client for communicating with a GitLab API.
 *
 * @author Emil Nilsson
 */
public class GitLabApiClient {
    /** HTTP status code 201 Created. */
    private static final int HTTP_201_CREATED = 201;

    /** The URL of the host server excluding the path. */
    private final String host;
    /** The private token used to authenticate the connection. */
    private final String privateToken;
    /** The used proxy host (or null if proxy is not used) */
    private final String proxyHost;
    /** The used proxy port */
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
        final GitLabSession session = new GitLabApiClient(host, null, proxyHost, proxyPort).getSession(login, password);
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
    public GitLabSession getSession(final String login, final String password)
            throws ApiConnectionFailureException, AuthenticationFailedException {
        final HttpResponse<JsonNode> response;
        try {
            // send request to API
            response = Unirest.post(getApiUrl() + "/session")
                    .field("login", login)
                    .field("password", password)
                    .asJson();
        } catch (UnirestException e) {
            throw new ApiConnectionFailureException("Could not connect to API", e);
        }

        // check if the request was successful
        if (response.getCode() != HTTP_201_CREATED) {
            throw new AuthenticationFailedException("Invalid user credentials");
        }

        // create a session object with the response
        return new GitLabSession(response.getBody().getObject());
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
     * Returns the URL of the API.
     *
     * @return an URL
     */
    private String getApiUrl() {
        return host + "/api/v3";
    }
}
