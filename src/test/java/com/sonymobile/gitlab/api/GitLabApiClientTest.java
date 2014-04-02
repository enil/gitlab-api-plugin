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

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sonymobile.gitlab.GitLabSession;
import com.sonymobile.gitlab.GitLabUser;
import com.sonymobile.gitlab.exceptions.ApiConnectionFailureException;
import com.sonymobile.gitlab.exceptions.AuthenticationFailedException;
import com.sonymobile.gitlab.helpers.MockData;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Unit tests for {@link GitLabApiClient}.
 *
 * @author Emil Nilsson
 */
public class GitLabApiClientTest {
    /** A rule for setting up a mock server for every test. */
    @Rule
    public WireMockRule serverRule = new WireMockRule(9090);

    /** The GitLab API client to test against. */
    private GitLabApiClient client;

    /**
     * Set up the GitLab API client.
     */
    @Before
    public void setUp() {
        client = new GitLabApiClient("http://localhost:9090", MockData.PRIVATE_TOKEN);
    }

    /**
     * Tests getting a session with valid credentials.
     *
     * Uses {@link GitLabApiClient#getSession(String, String)} to get a session.
     *
     * @throws ApiConnectionFailureException if the connection failed
     * @throws AuthenticationFailedException if the authentication failed
     */
    @Test
    public void openSessionWithValidCredentials()
            throws ApiConnectionFailureException, AuthenticationFailedException {
        // stub for expected request to get a session
        stubFor(post(urlEqualTo("/api/v3/session"))
                .withRequestBody(equalTo("login=username&password=password"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withBody(MockData.VALID_SESSION.toString())));

        // get a session from the API and make sure it succeeds
        GitLabSession session = client.getSession("username", "password");

        // check that the values of the session are correct
        assertThat(MockData.USER_ID, is(session.getId()));
        assertThat(MockData.USER_USERNAME, is(session.getUsername()));
        assertThat(MockData.USER_EMAIL, is(session.getEmail()));
        assertThat(MockData.USER_NAME, is(session.getName()));
        assertThat(MockData.PRIVATE_TOKEN, is(session.getPrivateToken()));
        assertThat(false, is(session.isBlocked()));
    }

    /**
     * Tests attempting to get a session with invalid credentials.
     *
     * Uses {@link GitLabApiClient#getSession(String, String)} to get a session.
     */
    @Test(expected=AuthenticationFailedException.class)
    public void openSessionWithInvalidCredentials()
            throws AuthenticationFailedException, ApiConnectionFailureException {
        // stub for expected request to get a session
        stubFor(post(urlEqualTo("/api/v3/session"))
                .withRequestBody(equalTo("login=username&password=invalidpassword"))
                .willReturn(aResponse()
                .withStatus(401)));

        // try to get a session from the API and expect it to throw and exception
        client.getSession("username", "invalidpassword");
    }

    /**
     * Tests getting the authenticated user with a valid token.
     *
     * Uses {@link GitLabApiClient#getCurrentUser()} to get the user.
     *
     * @throws ApiConnectionFailureException if the connection failed
     * @throws AuthenticationFailedException if the authentication failed
     */
    @Test
    public void getCurrentUserWithValidPrivateToken()
            throws AuthenticationFailedException, ApiConnectionFailureException {
        // stub for expected request to get the current user
        stubFor(get(urlEqualTo("/api/v3/user?private_token=" + MockData.PRIVATE_TOKEN))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(MockData.VALID_USER.toString())));

        // get the current user
        GitLabUser user = client.getCurrentUser();

        // check that the values of the user are correct
        assertThat(MockData.USER_ID, is(user.getId()));
        assertThat(MockData.USER_USERNAME, is(user.getUsername()));
        assertThat(MockData.USER_EMAIL, is(user.getEmail()));
        assertThat(MockData.USER_NAME, is(user.getName()));
        assertThat(false, is(user.isBlocked()));
    }

    /**
     * Tests attempting to get the authenticated user with invalid token.
     *
     * Uses {@link GitLabApiClient#getCurrentUser()} to get the user.
     *
     * @throws ApiConnectionFailureException if the connection failed
     * @throws AuthenticationFailedException if the authentication failed     */
    @Test(expected=AuthenticationFailedException.class)
    public void getCurrentUserWithInvalidPrivateToken()
            throws AuthenticationFailedException, ApiConnectionFailureException {
        // stub for expected request to get the current user
        stubFor(get(urlEqualTo("/api/v3/user?private_token=" + MockData.PRIVATE_TOKEN))
                .willReturn(aResponse()
                        .withStatus(401)));

        // try to get the current user from the API and expect it to throw and exception
        client.getCurrentUser();
    }
}
