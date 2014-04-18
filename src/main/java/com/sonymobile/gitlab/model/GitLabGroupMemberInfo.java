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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Information of a member in a GitLab group.
 *
 * A member is basically a user but with additional information about the group and the group membership.
 */
public final class GitLabGroupMemberInfo extends BasicGitLabUserInfo {
    /** The ID of the group. */
    private final int groupId;

    /** The access level of the group member. */
    private final GitLabAccessLevel accessLevel;

    /** Whether the group member is active. */
    private final boolean isActive;

    /**
     * Creates group membership information with JSON data.
     *
     * @param jsonObject a JSON object to derive the information from
     * @param groupId the ID of the group
     */
    public GitLabGroupMemberInfo(JSONObject jsonObject, int groupId) {
        super(jsonObject);
        try {
            accessLevel = GitLabAccessLevel.accessLevelForId(jsonObject.getInt("access_level"));
            isActive = jsonObject.getString("state").equals("active");
        } catch (JSONException e) {
            // failed to retrieve a value
            throw new IllegalArgumentException("Malformed JSON object", e);
        }

        this.groupId = groupId;
    }

    /**
     * Gets the access level for the member in this group
     *
     * @return an access level
     */
    public GitLabAccessLevel getAccessLevel() {
        return accessLevel;
    }

    /**
     * Gets the ID of the group
     *
     * @return a group ID
     */
    public int getGroupId() {
        return groupId;
    }

    /**
     * Checks whether the user account is active
     *
     * @return true if active
     */
    @Override
    public boolean isActive() {
        return isActive;
    }
}
