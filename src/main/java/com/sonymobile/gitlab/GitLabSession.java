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

package com.sonymobile.gitlab;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A GitLab session.
 *
 * @author Emil Nilsson
 */
public class GitLabSession {
    /** The private token. */
    private final String privateToken;
    /** The user. */
    private final GitLabUser user;

    /**
     * Creates a session from a JSON object.
     *
     * @param jsonObject the JSON object
     */
    public GitLabSession(JSONObject jsonObject) {
        try {
            privateToken = jsonObject.getString("private_token");
            // create a user object using the same JSON object
            user = new GitLabUser(jsonObject);
        } catch (JSONException e) {
            // failed to retrieve a value
            throw new IllegalArgumentException("Malformed JSON object", e);
        }
    }

    /**
     * Returns the user ID.
     *
     * @return a user ID
     */
    public int getId() {
        return user.getId();
    }

    /**
     * Returns the username of the user.
     *
     * @return a username
     */
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * Returns the email address of the user.
     *
     * @return an email address
     */
    public String getEmail() {
        return user.getEmail();
    }

    /**
     * Returns the name of the user.
     *
     * @return a name
     */
    public String getName() {
        return user.getName();
    }

    /**
     * Returns whether the user is blocked.
     *
     * @return true if the user is blocked
     */
    public boolean isBlocked() {
        return user.isBlocked();
    }

    /**
     * Returns the private token for the user.
     *
     * @return a private token
     */
    public String getPrivateToken() {
        return privateToken;
    }

    /**
     * Returns the user of the session.
     *
     * @return a user object for the user
     */
    public GitLabUser getUser() {
        return user;
    }
}
