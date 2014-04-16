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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static com.sonymobile.gitlab.helpers.FileHelpers.loadJsonObjectFromFile;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests getting attributes from a {@link GitLabSession}.
 *
 * @author Emil Nilsson
 */
public class SessionTest {
    /** The session object to test against. */
    private GitLabSession session;

    /** A rule for catching expected exceptions. */
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Loads the session object from a JSON file.
     *
     * @throws java.io.IOException if reading of the JSON file failed
     */
    @Before
    public void setUp() throws IOException {
        session = new GitLabSession(loadJsonObjectFromFile("api/v3/session/withValidCredentials.json"));
    }

    @Test
    public void getId() {
        assertThat(1, is(session.getId()));
    }

    @Test
    public void getUsername() {
        assertThat("username", is(session.getUsername()));
    }

    @Test
    public void getEmail() {
        assertThat("user@example.com", is(session.getEmail()));
    }

    @Test
    public void getName() {
        assertThat("User Name", is(session.getName()));
    }

    @Test
    public void getPrivateToken() {
        assertThat("0123456789abcdef", is(session.getPrivateToken()));
    }

    @Test
    public void isBlocked() {
        assertThat(false, is(session.isBlocked()));
    }

    /**
     * Attempts to create a session with missing keys.
     */
    @Test
    public void createSessionWithMissingKeys() throws Exception {
        // constructor should throw an exception
        thrown.expect(IllegalArgumentException.class);
        // use empty JSON object
        new GitLabSession(new JSONObject());
    }
}
