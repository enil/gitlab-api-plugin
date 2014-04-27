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

/**
 * Access levels to a GitLab group for a group member.
 *
 * @author Emil Nilsson
 */
public enum GitLabAccessLevel {
    /** Not a member in a group. */
    NONE       ("None",         0),
    /** A guest in a group. */
    GUEST       ("Guest",       10),
    /** A reporter in a group. */
    REPORTER    ("Reporter",    20),
    /** A developer in a group. */
    DEVELOPER   ("Developer",   30),
    /** A master of a group. */
    MASTER      ("Master",      40),
    /** An owner of a group. */
    OWNER       ("Owner",       50);

    /** The name. */
    private final String name;

    /** The unique ID. */
    private final int id;

    /**
     * Creates an access level with a name and unique id
     *
     * @param name the name
     * @param id   the unique ID
     */
    private GitLabAccessLevel(String name, int id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Returns the access level for an ID
     *
     * @param id the unique ID
     */
    public static GitLabAccessLevel accessLevelForId(int id) {
        for (GitLabAccessLevel accessLevel : GitLabAccessLevel.values()) {
            if (accessLevel.id == id) {
                return accessLevel;
            }
        }
        throw new IllegalArgumentException("Invalid access level ID");
    }
}
