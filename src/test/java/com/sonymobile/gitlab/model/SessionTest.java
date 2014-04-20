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

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static com.sonymobile.gitlab.helpers.FileHelpers.loadJsonObjectFromFile;
import static java.util.Calendar.MILLISECOND;
import static java.util.Calendar.NOVEMBER;
import static org.apache.commons.lang.time.DateUtils.UTC_TIME_ZONE;
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

    /**
     * Loads session objects from a JSON files.
     *
     * @throws IOException if reading of a JSON file failed
     */
    @Before
    public void setUp() throws IOException {
        normalSession = new GitLabSessionInfo(loadJsonObjectFromFile("api/v3/session"));

    }

    @Test
    public void getId() {
        assertThat(1, is(normalSession.getId()));
    }

    @Test
    public void getUsername() {
        assertThat("username", is(normalSession.getUsername()));
    }

    @Test
    public void getEmail() {
        assertThat("user@example.com", is(normalSession.getEmail()));
    }

    @Test
    public void getName() {
        assertThat("User Name", is(normalSession.getName()));
    }

    @Test
    public void getCreatedAtDate() {
        // create the date 2010-11-12 13:14:15
        Calendar calendar = new GregorianCalendar(UTC_TIME_ZONE);
        calendar.set(2010, NOVEMBER, 12, 13, 14, 15);
        calendar.clear(MILLISECOND);
        Date expectedDate = calendar.getTime();

        assertThat(expectedDate, is(normalSession.getCreatedAtDate()));
    }

    @Test
    public void isActive() {
        assertThat(normalSession.isActive(), is(true));
    }

    @Test
    public void isAdmin() {
        assertThat(normalSession.isAdmin(), is(false));
    }

    @Test
    public void getPrivateToken() {
        assertThat("0123456789abcdef", is(normalSession.getPrivateToken()));
    }

    /**
     * Converts the session info to a String, which should be the username of the user
     */
    @Test
    public void convertToString() {
        assertThat(normalSession, hasToString("username"));
    }
}
