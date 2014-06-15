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
import com.sonymobile.gitlab.model.GitLabUserInfo;
import org.junit.Test;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

/**
 * Tests getting users with the GitLab API client.
 *
 * @author Emil Nilsson
 */
public class ClientUserTest extends AbstractClientTest {
    /**
     * Gets all users.
     */
    @Test
    public void getUsers() throws Exception {
        // stub for expected request to get the all users
        stubFor(get(urlEqualTo("/api/v3/users?private_token=" + PRIVATE_TOKEN))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBodyFile("api/v3/users.json")));

        // get all users
        List<GitLabUserInfo> users = client.getUsers();

        assertThat(users, hasSize(3));

        GitLabUserInfo user = users.get(0);
        assertThat(user.getId(), is(1));
        assertThat(user.getUsername(), is("username"));
        assertThat(user.getEmail(), is("user@example.com"));
        assertThat(user.getName(), is("User Name"));
        assertThat(user.isBlocked(), is(false));
    }

    /**
     * Attempts to get all users with an invalid token.
     */
    @Test
    public void getUsersWithInvalidPrivateToken() throws Exception {
        // stub for expected request to get the all users
        stubFor(get(urlEqualTo("/api/v3/users?private_token=" + PRIVATE_TOKEN))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withBodyFile("/401.json")));

        // authentication should fail
        thrown.expect(AuthenticationFailedException.class);

        // try to get all users from the API and expect it to throw and exception
        client.getUsers();
    }

    /**
     * Gets a user.
     */
    @Test
    public void getUser() throws Exception {
        // stub for expected request to get the user
        stubFor(get(urlEqualTo("/api/v3/users/1?private_token=" + PRIVATE_TOKEN))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBodyFile("api/v3/users/1.json")));

        GitLabUserInfo user = client.getUser(1);
        assertThat(user.getId(), is(1));
        assertThat(user.getUsername(), is("username"));
        assertThat(user.getEmail(), is("user@example.com"));
        assertThat(user.getName(), is("User Name"));
        assertThat(user.isBlocked(), is(false));
    }

    /**
     * Attempts to get a user that doesn't exist.
     */
    @Test
    public void getNonexistentUser() throws Exception {
        // stub for expected request to get the user
        stubFor(get(urlEqualTo("/api/v3/users/1?private_token=" + PRIVATE_TOKEN))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withBodyFile("/404.json")));

        // should not find user
        thrown.expect(UserNotFoundException.class);

        client.getUser(1);
    }

    /**
     * Attempts to get a user with an invalid token.
     */
    @Test
    public void getUserWithInvalidPrivateToken() throws Exception {
        // stub for expected request to get the user
        stubFor(get(urlEqualTo("/api/v3/users/1?private_token=" + PRIVATE_TOKEN))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withBodyFile("/401.json")));

        // authentication should fail
        thrown.expect(AuthenticationFailedException.class);

        // try to get the user from the API and expect it to throw and exception
        client.getUser(1);
    }

    /**
     * Gets the authenticated user.
     */
    @Test
    public void getCurrentUser() throws Exception {
        // stub for expected request to get the current user
        stubFor(get(urlEqualTo("/api/v3/user?private_token=" + PRIVATE_TOKEN))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBodyFile("api/v3/user.json")));

        GitLabUserInfo user = client.getCurrentUser();
        assertThat(user.getId(), is(1));
        assertThat(user.getUsername(), is("username"));
        assertThat(user.getEmail(), is("user@example.com"));
        assertThat(user.getName(), is("User Name"));
        assertThat(user.isBlocked(), is(false));
    }

    /**
     * Attempts to get the authenticated user with invalid token.
     */
    @Test
    public void getCurrentUserWithInvalidPrivateToken() throws Exception {
        // stub for expected request to get the current user
        stubFor(get(urlEqualTo("/api/v3/user?private_token=" + PRIVATE_TOKEN))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withBodyFile("/401.json")));

        // authentication should fail
        thrown.expect(AuthenticationFailedException.class);

        // try to get the current user from the API and expect it to throw and exception
        client.getCurrentUser();
    }
}
