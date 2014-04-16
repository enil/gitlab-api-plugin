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

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static com.sonymobile.gitlab.helpers.FileHelpers.loadJsonObjectFromFile;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasToString;
import static org.junit.Assert.assertThat;

/**
 * Tests getting attributes from a {@link GitLabUser}.
 *
 * @author Emil Nilsson
 */
public class UserTest {
    /** The session object to test against. */
    private GitLabUser user;

    /** A rule for catching expected exceptions. */
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Loads the user object from a JSON file.
     *
     * @throws java.io.IOException if reading of the JSON file failed
     */
    @Before
    public void setUp() throws IOException {
        user = new GitLabUser(loadJsonObjectFromFile("api/v3/users/withValidPrivateToken.json"));
    }

    @Test
    public void getId() {
        assertThat(1, is(user.getId()));
    }

    @Test
    public void getUsername() {
        assertThat("username", is(user.getUsername()));
    }

    @Test
    public void getEmail() {
        assertThat("user@example.com", is(user.getEmail()));
    }

    @Test
    public void getName() {
        assertThat("User Name", is(user.getName()));
    }

    @Test
    public void isBlocked() {
        assertThat(false, is(user.isBlocked()));
    }

    /**
     * Attempts to create a user with missing keys.
     */
    @Test
    public void createUserWithMissingKeys() throws Exception {
        // constructor should throw an exception
        thrown.expect(IllegalArgumentException.class);
        // use empty JSON object
        new GitLabUser(new JSONObject());
    }

    /**
     * Converts the group to a String, which should be the user name
     */
    @Test
    public void convertToString() {
        assertThat(user, hasToString("User Name"));
    }
}
