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

import com.sonymobile.gitlab.exceptions.AuthenticationFailedException;
import com.sonymobile.gitlab.model.GitLabSessionInfo;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests getting sessions with the GitLab API client.
 *
 * @author Emil Nilsson
 */
public class ClientSessionTest extends AbstractClientTest {
    /**
     * Gets a session with valid credentials.
     */
    @Test
    public void openSessionWithValidCredentials() throws Exception {
        // stub for expected request to get a session
        stubFor(post(urlEqualTo("/api/v3/session"))
                .withRequestBody(containing("login=username"))
                .withRequestBody(containing("password=password"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withBodyFile("/api/v3/session.json")));

        // get a session from the API and make sure it succeeds
        GitLabSessionInfo session = client.getSession("username", "password");

        // check that the values of the session are correct
        assertThat(1, is(session.getId()));
        assertThat("username", is(session.getUsername()));
        assertThat("user@example.com", is(session.getEmail()));
        assertThat("User Name", is(session.getName()));
        assertThat(PRIVATE_TOKEN, is(session.getPrivateToken()));
        assertThat(false, is(session.isBlocked()));
    }

    /**
     * Gets a session for a blocked user.
     */
    @Test
    public void openSessionForBlockedUser() throws Exception {
        // stub for expected request to get a session
        stubFor(post(urlEqualTo("/api/v3/session"))
                .withRequestBody(containing("login=username"))
                .withRequestBody(containing("password=password"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withBodyFile("/api/v3/session_blocked.json")));

        // get a session from the API and make sure it succeeds
        GitLabSessionInfo session = client.getSession("username", "password");

        assertThat(true, is(session.isBlocked()));
    }

    /**
     * Attempts to get a session with invalid credentials.
     */
    @Test
    public void openSessionWithInvalidCredentials() throws Exception {
        // stub for expected request to get a session
        stubFor(post(urlEqualTo("/api/v3/session"))
                .withRequestBody(containing("login=username"))
                .withRequestBody(containing("password=invalidpassword"))
                .willReturn(aResponse()
                        .withStatus(401)));
        // authentication should fail
        thrown.expect(AuthenticationFailedException.class);

        // try to get a session from the API and expect it to throw and exception
        client.getSession("username", "invalidpassword");
    }
}
