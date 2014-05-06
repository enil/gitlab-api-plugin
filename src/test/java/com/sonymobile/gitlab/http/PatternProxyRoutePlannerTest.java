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

package com.sonymobile.gitlab.http;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link PatternProxyRoutePlanner}.
 *
 * @author Emil Nilsson
 */
public class PatternProxyRoutePlannerTest {
    /** Hostname patterns to exclude. */
    private static final List<Pattern> HOSTNAME_PATTERNS = new ArrayList<Pattern>();

    static {
        HOSTNAME_PATTERNS.add(Pattern.compile("127\\.0\\.0\\.1"));
        HOSTNAME_PATTERNS.add(Pattern.compile("domain\\.com"));
        HOSTNAME_PATTERNS.add(Pattern.compile("www\\.domain\\.com"));
        HOSTNAME_PATTERNS.add(Pattern.compile(".*\\.domain\\.com"));
    }

    /** The host of the proxy. */
    private static final HttpHost PROXY_HOST = new HttpHost("proxy");

    /** The router planner. */
    private PatternProxyRoutePlanner routerPlanner;


    @Before
    public void setUp() {
        // create a route planner for the proxy host and the hostname patterns to exclude
        routerPlanner = new PatternProxyRoutePlanner(PROXY_HOST, HOSTNAME_PATTERNS);
    }

    /**
     * Tests {@link PatternProxyRoutePlanner#determineRoute(HttpHost, HttpRequest, HttpContext)} with a host not
     * matching any of the excluded hostnames.
     */
    @Test
    public void withNoMatch() throws Exception {
        HttpRoute route = routerPlanner.determineRoute(
                new HttpHost("notmatching"),
                createRequest("notmatching"),
                new BasicHttpContext());

        assertThat("Should use proxy", route.getProxyHost().getHostName(), is("proxy"));
    }

    /**
     * Tests {@link PatternProxyRoutePlanner#determineRoute(HttpHost, HttpRequest, HttpContext)} with a host matching
     * one of the excluded hostnames.
     */
    @Test
    public void withMatch() throws Exception {
        HttpRoute route = routerPlanner.determineRoute(
                new HttpHost("domain.com"),
                createRequest("domain.com"),
                new BasicHttpContext());

        assertThat("Should bypass proxy", route.getProxyHost(), is(nullValue()));
        assertThat(route.getTargetHost().getHostName(), is("domain.com"));
    }

    /**
     * Tests {@link PatternProxyRoutePlanner#determineRoute(HttpHost, HttpRequest, HttpContext)} with a host matching
     * one of the excluded hostnames on a wildcard pattern.
     */
    @Test
    public void withWildCardMatch() throws Exception {
        HttpRoute route = routerPlanner.determineRoute(
                new HttpHost("subdomain.domain.com"),
                createRequest("subdomain.domain.com"),
                new BasicHttpContext());

        assertThat("Should bypass proxy", route.getProxyHost(), is(nullValue()));
        assertThat("Should match wildcard", route.getTargetHost().getHostName(), is("subdomain.domain.com"));
    }

    /**
     * Create a fake HTTP request for a hostname.
     *
     * @param hostname the hostname
     * @return a HTTP request
     */
    private HttpRequest createRequest(String hostname) {
        return new BasicHttpRequest("GET", String.format("http://%s/", hostname));
    }
}
