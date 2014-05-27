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

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import static java.lang.Integer.parseInt;

/**
 * Parent for all test classes testing the GitLab API client.
 *
 * @author Emil Nilsson
 */
public abstract class AbstractClientTest {
    /** The port to run WireMock on. */
    private static final int WIREMOCK_PORT = parseInt(System.getProperty("com.sonymobile.gitlab.api.wiremock.port",
            "6789"));

    /** The URL of the GitLab API WireMock is faking. */
    protected static final String SERVER_URL = "http://localhost:" + WIREMOCK_PORT;

    /** The private token of the GitLab API WireMock is faking. */
    protected static final String PRIVATE_TOKEN = "0123456789abcdef";

    /** A rule for setting up a mock server for every test. */
    @Rule
    public WireMockRule serverRule = new WireMockRule(WIREMOCK_PORT);

    /** A rule for catching expected exceptions. */
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /** The GitLab API client to test against. */
    protected GitLabApiClient client;

    /**
     * Set up the GitLab API client.
     */
    @Before
    public void setUp() {
        client = new GitLabApiClient(SERVER_URL, PRIVATE_TOKEN);
    }
}
