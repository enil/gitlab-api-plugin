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

package com.sonymobile.gitlab.helpers;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.io.FileUtils.readFileToString;

/**
 * Created by emil on 4/13/14.
 */
public class FileHelpers {
    /**
     * Loads a JSON object from a file in test resource.
     * 
     * The path must be relative to the test/resources/__files directory used by WireMock.
     *
     * @param relativePath a path relative to the files directory
     * @return a JSON object
     * @throws IOException if the file cannot be loaded
     */
    public static JSONObject loadJsonObjectFromFile(String relativePath) throws IOException {
        /* fixme: handle NPE gracefully */
        return new JSONObject(readFileToString(new File(getAbsoluteResourceFilePath(relativePath))));
    }

    /**
     * Loads a JSON array from a file in test resource.
     *
     * The path must be relative to the test/resources/__files directory used by WireMock.
     *
     * @param relativePath a path relative to the files directory
     * @return a JSON array
     * @throws IOException if the file cannot be loaded
     */
    public static JSONArray loadJsonArrayFromFile(String relativePath) throws IOException {
        /* fixme: handle NPE gracefully */
        return new JSONArray(readFileToString(new File(getAbsoluteResourceFilePath(relativePath))));
    }

    /**
     * Gets the absolute file path for a test resource file.
     *
     * The path must be relative to the test/resources/__files directory used by WireMock.

     * @param relativePath a path relative to the files directory
     * @return the absolute file path
     */
    private static String getAbsoluteResourceFilePath(String relativePath) throws IOException {
        return FileHelpers.class.getResource("/__files/" + relativePath).getFile();
    }
}
