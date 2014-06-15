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

import com.sonymobile.gitlab.helpers.JsonFileLoader;
import org.junit.Before;
import org.junit.Test;

import static com.sonymobile.gitlab.helpers.DateHelpers.utcDate;
import static com.sonymobile.gitlab.helpers.JsonFileLoader.jsonFile;
import static java.util.Calendar.NOVEMBER;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasToString;
import static org.junit.Assert.assertThat;

/**
 * Tests getting attributes from a {@link GitLabSessionInfo} object.
 *
 * @author Emil Nilsson
 */
public class SessionTest {
    /** A session for a normal user. */
    private GitLabSessionInfo normalSession;
    
    /** A session for a blocked user. */
    private GitLabSessionInfo blockedSession;

    /** A session for an admin user. */
    private GitLabSessionInfo adminSession;

    /**
     * Loads session objects from a JSON files.
     */
    @Before
    public void setUp() throws Exception {
        JsonFileLoader.ObjectLoader<GitLabSessionInfo> sessionFile = jsonFile("api/v3/session")
                .withType(GitLabSessionInfo.class);

        normalSession = sessionFile.loadAsObject();
        blockedSession = sessionFile.withVariant("blocked").loadAsObject();
        adminSession = sessionFile.withVariant("admin").loadAsObject();
    }

    @Test
    public void getId() {
        assertThat(normalSession.getId(), is(1));
    }

    @Test
    public void getUsername() {
        assertThat(normalSession.getUsername(), is("username"));
    }

    @Test
    public void getEmail() {
        assertThat(normalSession.getEmail(), is("user@example.com"));
    }

    @Test
    public void getName() {
        assertThat(normalSession.getName(), is("User Name"));
    }

    @Test
    public void getCreatedAtDate() {
        assertThat(normalSession.getCreatedAtDate(), is(utcDate(2010, NOVEMBER, 12, 13, 14, 15)));
    }

    @Test
    public void isActive() {
        assertThat(normalSession.isActive(), is(true));
        assertThat(blockedSession.isActive(), is(false));
    }

    @Test
    public void isAdmin() {
        assertThat(normalSession.isAdmin(), is(false));
        assertThat(adminSession.isAdmin(), is(true));
    }

    @Test
    public void getPrivateToken() {
        assertThat(normalSession.getPrivateToken(), is("0123456789abcdef"));
    }

    /**
     * Converts the session info to a String, which should be the username of the user
     */
    @Test
    public void convertToString() {
        assertThat(normalSession, hasToString("username"));
    }
}
