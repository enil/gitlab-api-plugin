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

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.hasToString;
import static org.junit.Assert.assertThat;

/**
 * Tests creating instances of a {@link GitLabApiClient}.
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

        assertThat(newClient.getHost(), is("http://gitlab.example.org"));
        assertThat(newClient.getPrivateToken(), is("0123456789abcdef"));
        assertThat(newClient.getProxyHost(), is("http://proxy"));
        assertThat(newClient.getProxyPort(), is(1234));
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

        assertThat(newClient.getHost(), is("http://gitlab.example.org"));
        assertThat(newClient.getPrivateToken(), is("0123456789abcdef"));
        assertThat(newClient.getProxyHost(), is("http://proxy"));
        assertThat(newClient.getProxyPort(), is(1234));
        assertThat(newClient.getProxyUser(), is("proxyuser"));
        assertThat(newClient.getProxyPassword(), is("proxypassword"));
    }

    /**
     * Tests creating a new instance with a proxy with proxy credentials and a list of excluded hosts.
     */
    @Test
    public void createInstanceWithProxyCredentialsAndExcludedHosts() {
        List<Pattern> excludedHosts = Collections.singletonList(Pattern.compile("localhost"));

        GitLabApiClient newClient = new GitLabApiClient(
                "http://gitlab.example.org",
                "0123456789abcdef",
                "http://proxy",
                1234,
                "proxyuser",
                "proxypassword",
                excludedHosts);

        assertThat(newClient.getHost(), is("http://gitlab.example.org"));
        assertThat(newClient.getPrivateToken(), is("0123456789abcdef"));
        assertThat(newClient.getProxyHost(), is("http://proxy"));
        assertThat(newClient.getProxyPort(), is(1234));
        assertThat(newClient.getProxyUser(), is("proxyuser"));
        assertThat(newClient.getProxyPassword(), is("proxypassword"));
        assertThat(newClient.getExcludedHostnames(), hasToString("[localhost]"));
    }

    /**
     * Tests creating a new instance without a proxy.
     */
    @Test
    public void createInstanceWithoutProxy() {
        GitLabApiClient newClient = new GitLabApiClient(
                "http://gitlab.example.org",
                "0123456789abcdef");

        assertThat(newClient.getHost(), is("http://gitlab.example.org"));
        assertThat(newClient.getPrivateToken(), is("0123456789abcdef"));
        assertThat(newClient.getProxyHost(), is(nullValue()));
    }
}
