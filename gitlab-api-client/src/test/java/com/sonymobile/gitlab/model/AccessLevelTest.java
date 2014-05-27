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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests getting values from {@link GitLabAccessLevel} with {@link GitLabAccessLevel#accessLevelForId(int)}.
 *
 * @author Emil Nilsson
 */
public class AccessLevelTest {
    /** A rule for catching expected exceptions. */
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void getNoneAccessLevel() {
        assertThat(GitLabAccessLevel.accessLevelForId(0), is(GitLabAccessLevel.NONE));
    }

    @Test
    public void getGuestAccessLevel() {
        assertThat(GitLabAccessLevel.accessLevelForId(10), is(GitLabAccessLevel.GUEST));
    }

    @Test
    public void getReporterAccessLevel() {
        assertThat(GitLabAccessLevel.accessLevelForId(20), is(GitLabAccessLevel.REPORTER));
    }

    @Test
    public void getDeveloperAccessLevel() {
        assertThat(GitLabAccessLevel.accessLevelForId(30), is(GitLabAccessLevel.DEVELOPER));
    }

    @Test
    public void getMasterAccessLevel() {
        assertThat(GitLabAccessLevel.accessLevelForId(40), is(GitLabAccessLevel.MASTER));
    }

    @Test
    public void getOwnerAccessLevel() {
        assertThat(GitLabAccessLevel.accessLevelForId(50), is(GitLabAccessLevel.OWNER));
    }

    @Test
    public void getInvalidAccessLevel() {
        // throw an exception if the ID isn't 10, 20, 30, 40 or 50
        thrown.expect(IllegalArgumentException.class);
        GitLabAccessLevel.accessLevelForId(11);
    }
}
