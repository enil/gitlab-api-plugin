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
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.protocol.HttpContext;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import static java.util.Collections.unmodifiableList;

/**
 * A route planner for choosing whether to use a proxy or not based on hostname patterns.
 *
 * @author Emil Nilsson
 */
public class PatternProxyRoutePlanner extends DefaultProxyRoutePlanner {
    /** Patterns for matching hostnames to exclude from the proxy. */
    private final List<Pattern> excludedHostnames;

    /** A map mapping already matched hosts to a boolean indicating whether the host is excluded. */
    private final HashMap<String, Boolean> matchedExcludedHostnames = new HashMap<String, Boolean>();

    /**
     * Creates a route planner using a list of hostnames to exclude from the proxy.
     *
     * @param proxy             the proxy
     * @param excludedHostnames the excluded hostnames
     */
    public PatternProxyRoutePlanner(HttpHost proxy, List<Pattern> excludedHostnames) {
        super(proxy);
        this.excludedHostnames = excludedHostnames;
    }

    /**
     * Returns a list of the excluded hostnames
     *
     * @return the excluded hostnames
     */
    public List<Pattern> getExcludedHostnames() {
        return unmodifiableList(excludedHostnames);
    }

    /**
     * Chooses a proxy for a host, bypassing the proxy for excluded hostnames.
     *
     * @param target  the host
     * @param request the request
     * @param context the HTTP context
     * @return the proxy or null if the proxy isn't used
     * @throws HttpException if an HTTP exception occurred
     */
    @Override
    protected HttpHost determineProxy(HttpHost target, HttpRequest request, HttpContext context) throws HttpException {
        final String hostname = target.getHostName();

        // check if the hostname has been matched before
        if (matchedExcludedHostnames.containsKey(hostname)) {
            if (matchedExcludedHostnames.get(hostname)) {
                // bypass proxy
                return null;
            }
        } else {
            for (final Pattern hostnamePattern : excludedHostnames) {
                if (hostnamePattern.matcher(hostname).matches()) {
                    // exclude from proxy in the future
                    matchedExcludedHostnames.put(hostname, true);
                    // bypass proxy
                    return null;
                }
            }

            // use proxy in the future
            matchedExcludedHostnames.put(hostname, false);
        }

        //use proxy
        return super.determineProxy(target, request, context);
    }
}
