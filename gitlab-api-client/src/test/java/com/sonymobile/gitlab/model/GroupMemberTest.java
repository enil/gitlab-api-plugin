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
 * Tests getting attributes from a {@link GitLabGroupMemberInfo} object.
 *
 * @author Emil Nilsson
 */
public class GroupMemberTest {
    /** A normal member. */
    private GitLabGroupMemberInfo normalMember;

    /** A blocked member. */
    private GitLabGroupMemberInfo blockedMember;

    /** An admin member. */
    private GitLabGroupMemberInfo adminMember;

    /**
     * Loads the group member objects from a JSON file.
     */
    @Before
    public void setUp() throws Exception {
        JsonFileLoader.ObjectLoader<GitLabGroupMemberInfo> membersFile = jsonFile("api/v3/groups/1/members")
                .withType(GitLabGroupMemberInfo.class)
                .andParameters(1); // group ID is 1

        normalMember = membersFile.fromIndex(0).loadAsObject();
        blockedMember = membersFile.fromIndex(1).loadAsObject();
        adminMember = membersFile.fromIndex(2).loadAsObject();
    }

    @Test
    public void getId() {
        assertThat(1, is(normalMember.getId()));
    }

    @Test
    public void getUsername() {
        assertThat(normalMember.getUsername(), is("username"));
    }

    @Test
    public void getEmail() {
        assertThat(normalMember.getEmail(), is("user@example.com"));
    }

    @Test
    public void getName() {
        assertThat(normalMember.getName(), is("User Name"));
    }

    @Test
    public void getAccessLevel() {
        assertThat(normalMember.getAccessLevel(), is(GitLabAccessLevel.DEVELOPER));
        assertThat(adminMember.getAccessLevel(), is(GitLabAccessLevel.OWNER));
    }

    @Test
    public void getCreatedAtDate() {
        assertThat(normalMember.getCreatedAtDate(), is(utcDate(2010, NOVEMBER, 12, 13, 14, 15)));
    }

    @Test
    public void isActive() {
        assertThat(normalMember.isActive(), is(true));
        assertThat(blockedMember.isActive(), is(false));
    }

    @Test
    public void getGroupId() {
        assertThat(normalMember.getGroupId(), is(1));
    }

    /**
     * Converts the group member info to a String, which should be the username of the user
     */
    @Test
    public void convertToString() {
        assertThat(normalMember, hasToString("username"));
    }
}
