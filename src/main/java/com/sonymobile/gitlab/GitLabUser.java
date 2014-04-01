/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Sony Mobile Communications AB. All rights reserved.
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
 * A GitLab user.
 *
 * @author Emil Nilsson
 */
public class GitLabUser {
    /** The user ID. */
    private final int id;
    /** The username. */
    private final String username;
    /** The email address. */
    private final String email;
    /** The name. */
    private final String name;
    /** Whether the user is blocked. */
    private final boolean isBlocked;

    /**
     * Creates a user from a JSON object.
     *
     * A user object can either be created from a session JSON object or a user JSON object.
     *
     * @param jsonObject the JSON object
     */
    public GitLabUser(JSONObject jsonObject) {
        try {
            id = jsonObject.getInt("id");
            username = jsonObject.getString("username");
            email = jsonObject.getString("email");
            name = jsonObject.getString("name");
            // sessions objects use key "blocked", user objects use "state"
            if (jsonObject.has("blocked")) {
                isBlocked = jsonObject.getBoolean("blocked");
            } else {
                isBlocked = jsonObject.getString("state").equals("blocked");
            }
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
        return id;
    }

    /**
     * Returns the username of the user.
     *
     * @return a username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the email address of the user.
     *
     * @return an email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the name of the user.
     *
     * @return a name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns whether the user is blocked.
     *
     * @return true if the user is blocked
     */
    public boolean isBlocked() {
        return isBlocked;
    }

    @Override
    public String toString() {
        return getName();
    }
}
