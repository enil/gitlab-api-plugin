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

package com.sonymobile.gitlab.helpers;

import org.json.JSONObject;

/**
 * Mock data used for testing the API.
 *
 * @author Emil Nilsson
 */
public final class MockData {
    /** An example user ID. */
    public static final int USER_ID = 1;
    /** An example username. */
    public static final String USER_USERNAME = "username";
    /** An example name. */
    public static final String USER_EMAIL = "user@example.com";
    /** An example email . */
    public static final String USER_NAME = "User Name";
    /** An example private token. */
    public static final String PRIVATE_TOKEN = "0123456789abcdef";

    /** A valid JSON object from /session. */
    public static final JSONObject VALID_SESSION = new JSONObject();
    static {
        VALID_SESSION
                .put("id",              USER_ID)
                .put("username",        USER_USERNAME)
                .put("email",           USER_EMAIL)
                .put("name",            USER_NAME)
                .put("private_token",   PRIVATE_TOKEN)
                .put("blocked",         false);
    }

    /** A valid JSON object from /user/:id or /user. */
    public static final JSONObject VALID_USER = new JSONObject();
    static {
        VALID_USER
                .put("id",              USER_ID)
                .put("username",        USER_USERNAME)
                .put("email",           USER_EMAIL)
                .put("name",            USER_NAME)
                .put("private_token",   PRIVATE_TOKEN)
                .put("state",           "active");
    }
}
