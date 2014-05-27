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

import org.apache.http.HttpException;
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
    private static final List<Pattern> HOSTNAME_PATTERNS = new ArrayList<Pattern>() {
        {
            add(Pattern.compile("127\\.0\\.0\\.1"));
            add(Pattern.compile("domain"));
            add(Pattern.compile("www\\.domain"));
            add(Pattern.compile(".*\\.domain"));
        }
    };

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
        HttpRoute firstRoute = determinRoute("notmatching");
        HttpRoute secondRoute = determinRoute("notmatching");

        assertThat("Should use proxy", firstRoute.getProxyHost().getHostName(), is("proxy"));
        assertThat("Should use same route again", secondRoute.getProxyHost().getHostName(), is("proxy"));
    }

    /**
     * Tests {@link PatternProxyRoutePlanner#determineRoute(HttpHost, HttpRequest, HttpContext)} with a host matching
     * one of the excluded hostnames.
     */
    @Test
    public void withMatch() throws Exception {
        HttpRoute firstRoute = determinRoute("domain");
        HttpRoute secondRoute = determinRoute("domain");

        assertThat("Should bypass proxy", firstRoute.getProxyHost(), is(nullValue()));
        assertThat(firstRoute.getTargetHost().getHostName(), is("domain"));

        assertThat("Should bypass proxy the again", secondRoute.getProxyHost(), is(nullValue()));
        assertThat("Should use same route the again", secondRoute.getTargetHost().getHostName(), is("domain"));
    }

    /**
     * Tests {@link PatternProxyRoutePlanner#determineRoute(HttpHost, HttpRequest, HttpContext)} with a host matching
     * one of the excluded hostnames on a wildcard pattern.
     */
    @Test
    public void withWildCardMatch() throws Exception {
        HttpRoute firstRoute = determinRoute("subdomain.domain");
        HttpRoute secondRoute = determinRoute("subdomain.domain");

        assertThat("Should bypass proxy", firstRoute.getProxyHost(), is(nullValue()));
        assertThat("Should match wildcard", firstRoute.getTargetHost().getHostName(), is("subdomain.domain"));
        assertThat("Should bypass proxy again", secondRoute.getProxyHost(), is(nullValue()));
        assertThat("Should match wildcard again", secondRoute.getTargetHost().getHostName(), is("subdomain.domain"));
    }

    /**
     * Call {@link PatternProxyRoutePlanner#determineRoute(HttpHost, HttpRequest, HttpContext)} with a specified
     * hostname and a standard request and context.
     *
     * @param hostname the hostname
     * @return the returned route
     * @throws HttpException if the method threw an HTTP exception
     */
    private HttpRoute determinRoute(String hostname) throws HttpException {
        return routerPlanner.determineRoute(new HttpHost(hostname), createRequest(hostname), new BasicHttpContext());
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
