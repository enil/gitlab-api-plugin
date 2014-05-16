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
import com.sonymobile.gitlab.exceptions.UserNotFoundException;
import com.sonymobile.gitlab.model.GitLabSessionInfo;
import com.sonymobile.gitlab.model.GitLabUserInfo;
import org.junit.Before;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests impersonating users with {@link ImpersonatingGitLabApiClient}.
 *
 * @author Emil Nilsson
 */
public class UserImpersonationTest extends AbstractClientTest {
    /** The API client created with {@link GitLabApiClient#asUser(int)}. */
    private GitLabApiClient newClient;

    @Before
    public void setUp() {
        // set up client for WireMock
        super.setUp();

        newClient = client.asUser(1);
    }

    /**
     * Checks that the correct user ID is set for the new client.
     */
    @Test
    public void isUserIdSet() {
        GitLabApiClient client = new GitLabApiClient("http://gitlab.example.org", "0123456789abcdef");
        GitLabApiClient newClient = client.asUser(1);

        assertThat(newClient, is(instanceOf(ImpersonatingGitLabApiClient.class)));
        ImpersonatingGitLabApiClient impersonatingClient = (ImpersonatingGitLabApiClient)newClient;

        assertThat(impersonatingClient.getUserId(), is(1));
    }

    /**
     * Checks that the fields are set correctly in the new client.
     */
    @Test
    public void areFieldsSet() {
        GitLabApiClient client = new GitLabApiClient(
                "http://gitlab.example.org",
                "0123456789abcdef",
                "http://proxy",
                1234);
        GitLabApiClient newClient = client.asUser(1);

        assertThat(newClient.getHost(), is("http://gitlab.example.org"));
        assertThat(newClient.getPrivateToken(), is("0123456789abcdef"));
        assertThat(newClient.getProxyHost(), is("http://proxy"));
        assertThat(newClient.getProxyPort(), is(1234));
    }

    /**
     * Tests making a GET request impersonating a user.
     */
    @Test
    public void makeGetRequest() throws Exception {
        // stub for expected request to get the current user
        stubFor(get(urlEqualTo("/api/v3/user?sudo=1&private_token=" + PRIVATE_TOKEN))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBodyFile("api/v3/user.json")));

        GitLabUserInfo user = newClient.getCurrentUser();
        assertThat(1, is(user.getId()));
    }

    /**
     * Tests making a GET request impersonating a user which doesn't exist.
     */
    @Test
    public void makeGetRequestForMissingUser() throws Exception {
        // stub for expected request to get the current user
        stubFor(get(urlEqualTo("/api/v3/user?sudo=1&private_token=" + PRIVATE_TOKEN))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withBodyFile("api/v3/user_missing.json")));

        thrown.expect(UserNotFoundException.class);

        newClient.getCurrentUser();
    }

    /**
     * Tests making a POST request impersonating a user.
     */
    @Test
    public void makePostRequest() throws Exception {
        // stub for expected request to get a session
        stubFor(post(urlEqualTo("/api/v3/session"))
                .withRequestBody(containing("sudo=1"))
                .withRequestBody(containing("login=username"))
                .withRequestBody(containing("password=password"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withBodyFile("/api/v3/session.json")));

        // get a session from the API and make sure it succeeds
        GitLabSessionInfo session = newClient.getSession("username", "password");

        assertThat(session.getId(), is(1));
    }

    /**
     * Tests making a POST request impersonating a user which doesn't exist.
     */
    @Test
    public void makePostRequestForMissingUser() throws Exception {
        // stub for expected request to get a session
        stubFor(post(urlEqualTo("/api/v3/session"))
                .withRequestBody(containing("sudo=1"))
                .withRequestBody(containing("login=username"))
                .withRequestBody(containing("password=password"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withBodyFile("api/v3/user_missing.json")));

        thrown.expect(UserNotFoundException.class);

        newClient.getSession("username", "password");
    }
}
