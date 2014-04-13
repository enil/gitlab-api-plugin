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
import com.sonymobile.gitlab.GitLabGroup;
import com.sonymobile.gitlab.GitLabSession;
import com.sonymobile.gitlab.GitLabUser;
import com.sonymobile.gitlab.exceptions.ApiConnectionFailureException;
import com.sonymobile.gitlab.exceptions.AuthenticationFailedException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static java.lang.Integer.parseInt;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

/**
 * Unit tests for {@link GitLabApiClient}.
 *
 * @author Emil Nilsson
 */
public class GitLabApiClientTest {
    /** The port to run WireMock on. */
    private static final int WIREMOCK_PORT = parseInt(System.getProperty("com.sonymobile.gitlab.api.wiremock.port",
            "6789"));

    /** The private token. */
    private static final String PRIVATE_TOKEN = "0123456789abcdef";

    /** A rule for setting up a mock server for every test. */
    @Rule public WireMockRule serverRule = new WireMockRule(WIREMOCK_PORT);

    /** A rule for catching expected exceptions. */
    @Rule public ExpectedException thrown = ExpectedException.none();

    /** The GitLab API client to test against. */
    private GitLabApiClient client;

    /**
     * Set up the GitLab API client.
     */
    @Before
    public void setUp() {
        client = new GitLabApiClient("http://localhost:" + WIREMOCK_PORT, PRIVATE_TOKEN);
    }

    /**
     * Tests getting a session with valid credentials.
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
                        .withBodyFile("/api/v3/session/withValidCredentials.json")));

        // get a session from the API and make sure it succeeds
        GitLabSession session = client.getSession("username", "password");

        // check that the values of the session are correct
        assertThat(1,                   is(session.getId()));
        assertThat("username",          is(session.getUsername()));
        assertThat("user@example.com",  is(session.getEmail()));
        assertThat("User Name",         is(session.getName()));
        assertThat(PRIVATE_TOKEN,       is(session.getPrivateToken()));
        assertThat(false,               is(session.isBlocked()));
    }

    /**
     * Tests attempting to get a session with invalid credentials.
     *
     * @throws ApiConnectionFailureException if the connection failed
     * @throws AuthenticationFailedException if the authentication failed
     */
    @Test
    public void openSessionWithInvalidCredentials()
            throws AuthenticationFailedException, ApiConnectionFailureException {
        // stub for expected request to get a session
        stubFor(post(urlEqualTo("/api/v3/session"))
                .withRequestBody(equalTo("login=username&password=invalidpassword"))
                .willReturn(aResponse()
                        .withStatus(401)));
        // authentication should fail
        thrown.expect(AuthenticationFailedException.class);

        // try to get a session from the API and expect it to throw and exception
        client.getSession("username", "invalidpassword");
    }

    /**
     * Tests getting all groups for the authenticated user with ha valid token.
     *
     * @throws ApiConnectionFailureException if the connection failed
     * @throws AuthenticationFailedException if the authentication failed
     */
    @Test
    public void getGroupsWithValidPrivateToken()
            throws AuthenticationFailedException, ApiConnectionFailureException {
        // stub for expected request to get all groups
        stubFor(get(urlEqualTo("/api/v3/groups?private_token=" + PRIVATE_TOKEN))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBodyFile("/api/v3/groups/all.json")));
        // get the groups
        List<GitLabGroup> groups = client.getGroups();

        assertThat(groups, hasSize(1));
        // pick out the first (and only) group

        GitLabGroup group = groups.get(0);
        assertThat(2, is(group.getId()));
        assertThat("Group Name", is(group.getName()));
        assertThat("groupname", is(group.getPath()));
    }

    /**
     * Tests attempting to get groups for the authenticated user with invalid token.
     *
     * @throws ApiConnectionFailureException if the connection failed
     * @throws AuthenticationFailedException if the authentication failed
     */
    @Test
    public void getGroupsWithInvalidPrivateToken()
            throws AuthenticationFailedException, ApiConnectionFailureException {
        // stub for expected request to get all groups
        stubFor(get(urlEqualTo("/api/v3/groups?private_token=" + PRIVATE_TOKEN))
                .willReturn(aResponse()
                        .withStatus(401)));

        // authentication should fail
        thrown.expect(AuthenticationFailedException.class);

        // try to get the groups from the API and expect it to throw and exception
        client.getGroups();
    }

    /**
     * Tests getting the authenticated user with a valid token.
     *
     * @throws ApiConnectionFailureException if the connection failed
     * @throws AuthenticationFailedException if the authentication failed
     */
    @Test
    public void getCurrentUserWithValidPrivateToken()
            throws AuthenticationFailedException, ApiConnectionFailureException {
        // stub for expected request to get the current user
        stubFor(get(urlEqualTo("/api/v3/user?private_token=" + PRIVATE_TOKEN))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBodyFile("api/v3/users/withValidPrivateToken.json")));

        // get the current user
        GitLabUser user = client.getCurrentUser();

        // check that the values of the user are correct
        assertThat(1,                   is(user.getId()));
        assertThat("username",          is(user.getUsername()));
        assertThat("user@example.com",  is(user.getEmail()));
        assertThat("User Name",         is(user.getName()));
        assertThat(false,               is(user.isBlocked()));
    }

    /**
     * Tests attempting to get the authenticated user with invalid token.
     *
     * @throws ApiConnectionFailureException if the connection failed
     * @throws AuthenticationFailedException if the authentication failed
     */
    @Test
    public void getCurrentUserWithInvalidPrivateToken()
            throws AuthenticationFailedException, ApiConnectionFailureException {
        // stub for expected request to get the current user
        stubFor(get(urlEqualTo("/api/v3/user?private_token=" + PRIVATE_TOKEN))
                .willReturn(aResponse()
                        .withStatus(401)));
        // authentication should fail
        thrown.expect(AuthenticationFailedException.class);

        // try to get the current user from the API and expect it to throw and exception
        client.getCurrentUser();
    }
}
