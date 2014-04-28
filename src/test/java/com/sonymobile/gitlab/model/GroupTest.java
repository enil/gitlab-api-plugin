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

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static com.sonymobile.gitlab.helpers.JsonFileLoader.jsonFile;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasToString;
import static org.junit.Assert.assertThat;

/**
 * Tests getting attributes from a {@link GitLabGroupInfo} object.
 *
 * @author Emil Nilsson
 */
public class GroupTest {
    /** The group object to test against. */
    private GitLabGroupInfo group;

    /** A rule for catching expected exceptions. */
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Loads the group object from a JSON file.
     */
    @Before
    public void setUp() throws Exception {
        group = jsonFile("api/v3/groups/1")
                .withType(GitLabGroupInfo.class)
                .loadAsObject();
    }

    @Test
    public void getId() {
        assertThat(1, is(group.getId()));
    }

    @Test
    public void getName() {
        assertThat("Group Name", is(group.getName()));
    }

    @Test
    public void getPath() {
        assertThat("groupname", is(group.getPath()));
    }

    /**
     * Attempts to create a group with missing keys.
     */
    @Test
    public void createGroupWithMissingKeys() throws Exception {
        // constructor should throw an exception
        thrown.expect(IllegalArgumentException.class);
        // use empty JSON object
        new GitLabGroupInfo(new JSONObject());
    }

    /**
     * Converts the group to a String, which should be the group name
     */
    @Test
    public void convertToString() {
        assertThat(group, hasToString("Group Name"));
    }
}
