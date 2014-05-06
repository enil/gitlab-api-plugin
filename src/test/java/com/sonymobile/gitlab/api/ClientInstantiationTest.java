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

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests creating instances of a GitLab API client.
 *
 * @author Emil Nilsson
 */
public class ClientInstantiationTest extends AbstractClientTest {
    /**
     * Tests creating a new instance with a proxy.
     */
    @Test
    public void createInstanceWithProxy() {
        GitLabApiClient newClient = new GitLabApiClient(
                "http://gitlab.example.org",
                "0123456789abcdef",
                "http://proxy",
                1234);

        assertThat("http://gitlab.example.org", is(newClient.getHost()));
        assertThat("0123456789abcdef", is(newClient.getPrivateToken()));
        assertThat("http://proxy", is(newClient.getProxyHost()));
        assertThat(1234, is(newClient.getProxyPort()));
    }

    /**
     * Tests creating a new instance with a proxy with proxy credentials.
     */
    @Test
    public void createInstanceWithProxyCredentials() {
        GitLabApiClient newClient = new GitLabApiClient(
                "http://gitlab.example.org",
                "0123456789abcdef",
                "http://proxy",
                1234,
                "proxyuser",
                "proxypassword");

        assertThat("http://gitlab.example.org", is(newClient.getHost()));
        assertThat("0123456789abcdef", is(newClient.getPrivateToken()));
        assertThat("http://proxy", is(newClient.getProxyHost()));
        assertThat(1234, is(newClient.getProxyPort()));
        assertThat("proxyuser", is(newClient.getProxyUser()));
        assertThat("proxypassword", is(newClient.getProxyPassword()));
    }

    /**
     * Tests creating a new instance without a proxy.
     */
    @Test
    public void createInstanceWithoutProxy() {
        GitLabApiClient newClient = new GitLabApiClient(
                "http://gitlab.example.org",
                "0123456789abcdef");

        assertThat("http://gitlab.example.org", is(newClient.getHost()));
        assertThat("0123456789abcdef", is(newClient.getPrivateToken()));
        assertThat(null, is(newClient.getProxyHost()));
    }

    /**
     * Tests creating a new instance by assuming the identity of another user.
     */
    @Test
    public void createInstanceByImpersonatingUser() {
        GitLabApiClient oldClient = new GitLabApiClient(
                "http://gitlab.example.org",
                "0123456789abcdef",
                "http://proxy",
                1234);

        // assume the identity of another user
        GitLabApiClient newClient = oldClient.impersonate("9876543210abcdef");

        assertThat("http://gitlab.example.org", is(newClient.getHost()));
        assertThat("9876543210abcdef", is(newClient.getPrivateToken()));
        assertThat("http://proxy", is(newClient.getProxyHost()));
        assertThat(1234, is(newClient.getProxyPort()));
    }
}
