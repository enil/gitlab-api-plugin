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
import com.sonymobile.gitlab.exceptions.ApiConnectionFailureException;
import com.sonymobile.gitlab.exceptions.AuthenticationFailedException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
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
    /** A body message for a valid session. */
    private static final String validSessionResponseBody;

    // creates a body message for a valid session
    static {
        validSessionResponseBody = new JSONObject()
                .put("id",              1)
                .put("username",        "username")
                .put("email",           "user@example.com")
                .put("name",            "User Name")
                .put("private_token",   "token")
                .put("blocked",         false).toString();
    }

    /**
     * Set up the GitLab API client.
     */
    @Before
    public void setUp() {
        client = new GitLabApiClient("http://localhost:9090", "token");
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
    public void testGettingValidSession() throws ApiConnectionFailureException, AuthenticationFailedException {
        // stub the request to get a session that is expected
        stubFor(post(urlEqualTo("/api/v3/session"))
                .withRequestBody(equalTo("login=username&password=password"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withBody(validSessionResponseBody.toString())));

        // get a session from the API and make sure it succeeds
        GitLabSession session = client.getSession("username", "password");

        // check that the values of the session are correct
        assertThat(1, is(session.getId()));
        assertThat("username", is(session.getUsername()));
        assertThat("user@example.com", is(session.getEmail()));
        assertThat("User Name", is(session.getName()));
        assertThat("token", is(session.getPrivateToken()));
        assertThat(false, is(session.isBlocked()));
    }

    /**
     * Tests attempting to get a session with invalid credentials.
     *
     * Uses {@link GitLabApiClient#getSession(String, String)} to get a session.
     */
    @Test(expected=AuthenticationFailedException.class)
    public void testGettingInvalidSession() throws AuthenticationFailedException, ApiConnectionFailureException {
        // stub for request to get an error code
        stubFor(post(urlEqualTo("/api/v3/session"))
                .withRequestBody(equalTo("login=username&password=invalidpassword"))
                .willReturn(aResponse()
                .withStatus(401)));

        // try to get a session from the API and expect it to throw and exception
        client.getSession("username", "invalidpassword");
    }
}