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

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.sonymobile.gitlab.exceptions.ApiConnectionFailureException;
import com.sonymobile.gitlab.exceptions.AuthenticationFailedException;
import com.sonymobile.gitlab.exceptions.NotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * A GitLab API client connecting as another user.
 *
 * @author Emil Nilsson
 */
/* package */ final class ImpersonatingGitLabApiClient extends GitLabApiClient {
    /** The user ID of the impersonated user. */
    private final int userId;

    /**
     * Creates a GitLab API client impersonating a user
     *
     * @param userId            the user ID of the user to impersonate
     * @param host              the URL of the host server (excluding the path)
     * @param privateToken      the private token used to authenticate the connection
     * @param proxyHost         the proxy host name
     * @param proxyPort         the proxy port
     * @param proxyUser         the proxy user
     * @param proxyPassword     the proxy password
     * @param excludedHostnames the excluded hosts
     */
    /* package */ ImpersonatingGitLabApiClient(int userId,
                                        String host, String privateToken,
                                        String proxyHost, int proxyPort,
                                        String proxyUser, String proxyPassword,
                                        List<Pattern> excludedHostnames) {
        super(host, privateToken, proxyHost, proxyPort, proxyUser, proxyPassword, excludedHostnames);
        this.userId = userId;
    }

    /**
     * Returns the user ID of the impersonated user.
     *
     * @return the user ID
     */
    public int getUserId() {
        return userId;
    }

    @Override
    protected HttpResponse<JsonNode> get(String path, Map<String, Object> fields, boolean includePrivateToken)
            throws ApiConnectionFailureException, AuthenticationFailedException, NotFoundException {
        fields = (fields == null) ? new HashMap<String, Object>(1) : fields;
        // impersonate the user
        fields.put("sudo", getUserId());
        return super.get(path, fields, includePrivateToken);
    }

    @Override
    protected HttpResponse<JsonNode> post(String path, Map<String, Object> fields, boolean includePrivateToken)
            throws ApiConnectionFailureException, AuthenticationFailedException, NotFoundException {
        fields = (fields == null) ? new HashMap<String, Object>(1) : fields;
        // impersonate the user
        fields.put("sudo", getUserId());
        return super.post(path, fields, includePrivateToken);
    }
}
