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

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Unit tests for {@link GitLabUser}.
 *
 * @author Emil Nilsson
 */
public class GitLabUserTest {
    /** The session object to test against. */
    private GitLabUser user;
    /** The JSON object to create the user from. */
    private static final JSONObject jsonObject = new JSONObject();

    // set values for the JSON object
    static {
        jsonObject
                .put("id",              1)
                .put("username",        "username")
                .put("email",           "user@example.com")
                .put("name",            "User Name")
                .put("private_token",   "token")
                .put("state",           "active");
    }

    /**
     * Sets up the session object with reasonable values.
     */
    @Before
    public void setUp() {
        user = new GitLabUser(jsonObject);
    }

    /**
     * Tests whether the correct user ID is set.
     */
    @Test
    public void testId() {
        assertThat(1, is(user.getId()));
    }

    /**
     * Tests whether the correct username is set.
     */
    @Test
    public void testUsername() {
        assertThat("username", is(user.getUsername()));
    }

    /**
     * Tests whether the correct email address is set.
     */
    @Test
    public void testEmail() {
        assertThat("user@example.com", is(user.getEmail()));
    }

    /**
     * Tests whether the correct name is set.
     */
    @Test
    public void testName() {
        assertThat("User Name", is(user.getName()));
    }

    /**
     * Tests whether the correct block status is set.
     */
    @Test
    public void testIsBlocked() {
        assertThat(false, is(user.isBlocked()));
    }

    /**
     * Tests that creating a user fails if needed keys are missing.
     *
     * The construct should throw {@link IllegalArgumentException} when keys are missing.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testMissingKeys() {
        // use empty JSON object
        new GitLabSession(new JSONObject());
    }
}
