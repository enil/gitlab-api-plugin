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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.apache.commons.lang.time.DateUtils.UTC_TIME_ZONE;

/**
 * The most basic information about a GitLab user.
 *
 * @author Emil Nilsson
 */
public abstract class GitLabUserInfo {
    /** A date formatter for parsing the date of creation. */
    private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    static {
        // interpret all dates as UTC dates
        DATE_FORMATTER.setTimeZone(UTC_TIME_ZONE);
    }

    /** The user ID. */
    private final int id;

    /** The username. */
    private final String username;

    /** The email address. */
    private final String email;

    /** The name. */
    private final String name;

    /** The date of creation. */
    private final Date createdAt;

    /**
     * Creates a user info object from a JSON object.
     *
     * @param jsonObject the JSON object
     */
    public GitLabUserInfo(JSONObject jsonObject) {
        try {
            id = jsonObject.getInt("id");
            username = jsonObject.getString("username");
            email = jsonObject.getString("email");
            name = jsonObject.getString("name");
            createdAt = DATE_FORMATTER.parse(jsonObject.getString("created_at"));
        } catch (JSONException e) {
            // failed to retrieve a value
            throw new IllegalArgumentException("Malformed JSON object", e);
        } catch (ParseException e) {
            // failed to parse the date of creation
            throw new IllegalArgumentException("Malformed date");
        }
    }


    /**
     * Gets the user ID.
     *
     * @return a user ID
     */
    public final int getId() {
        return id;
    }

    /**
     * Gets the username.
     *
     * @return a username
     */
    public final String getUsername() {
        return username;
    }

    /**
     * Gets the email address.
     *
     * @return an email address
     */
    public final String getEmail() {
        return email;
    }

    /**
     * Gets the name.
     *
     * @return a name
     */
    public final String getName() {
        return name;
    }

    /**
     * Gets the date of the creation of the user account.
     *
     * @return a date
     */
    public final Date getCreatedAtDate() {
        return createdAt;
    }

    /**
     * Checks whether the user account is active.
     *
     * @return true if active
     */
    public abstract boolean isActive();

    /**
     * Checks whether the user is blocked.
     *
     * @return true if blocked
     */
    public final boolean isBlocked() {
        return !isActive();
    }
}
