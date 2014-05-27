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

package com.sonymobile.gitlab.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Complete information about a GitLab user.
 *
 * @author Emil Nilsson
 */
public abstract class GitLabUserInfo extends BasicGitLabUserInfo {
    /** Whether the user is an administrator. */
    private final boolean isAdmin;

    /**
     * Creates a user info object from a JSON object.
     *
     * @param jsonObject a JSON object to derive the information from
     */
    protected GitLabUserInfo(JSONObject jsonObject) {
        super(jsonObject);
        try {
            isAdmin = jsonObject.getBoolean("is_admin");
        } catch (JSONException e) {
            // failed to retrieve a value
            throw new IllegalArgumentException("Malformed JSON object", e);
        }
    }

    /**
     * Checks whether the user is an administrator.
     *
     * @return true if administrator
     */
    public final boolean isAdmin() {
        return isAdmin;
    }
}
