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

import com.sonymobile.gitlab.GitLabGroup;
import com.sonymobile.gitlab.exceptions.AuthenticationFailedException;
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
 * Tests getting groups with the GitLab API client.
 *
 * @author Emil Nilsson
 */
public class ClientGroupTest extends AbstractClientTest {
    /**
     * Gets all groups for the authenticated user with ha valid token.
     */
    @Test
    public void getGroupsWithValidPrivateToken() throws Exception {
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
        assertThat(2,               is(group.getId()));
        assertThat("Group Name",    is(group.getName()));
        assertThat("groupname",     is(group.getPath()));
    }

    /**
     * Attempts to get groups for the authenticated user with invalid token.
     */
    @Test
    public void getGroupsWithInvalidPrivateToken() throws Exception {
        // stub for expected request to get all groups
        stubFor(get(urlEqualTo("/api/v3/groups?private_token=" + PRIVATE_TOKEN))
                .willReturn(aResponse()
                        .withStatus(401)));

        // authentication should fail
        thrown.expect(AuthenticationFailedException.class);

        // try to get the groups from the API and expect it to throw and exception
        client.getGroups();
    }
}
