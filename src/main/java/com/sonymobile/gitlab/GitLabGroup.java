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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A GitLab group.
 *
 * @author Emil Nilsson
 */
public class GitLabGroup {
    /** The group ID. */
    private final int id;

    /** The full group name. */
    private final String name;

    /** The group path. */
    private final String path;

    /**
     * Creates a user from a JSON object.
     *
     * @param jsonObject the JSON object
     */
    public GitLabGroup(JSONObject jsonObject) {
        try {
            id = jsonObject.getInt("id");
            name = jsonObject.getString("name");
            path = jsonObject.getString("path");
        } catch (JSONException e) {
            // failed to retrieve a value
            throw new IllegalArgumentException("Malformed JSON object", e);
        }
    }

    /**
     * Returns the group ID.
     *
     * @return a group ID
     */
    public int getId() {// todo: implement
        return id;
    }

    /**
     * Returns the full group name.
     *
     * @return a group name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the path of the group.
     *
     * @return a path
     */
    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return getName();
    }
}
