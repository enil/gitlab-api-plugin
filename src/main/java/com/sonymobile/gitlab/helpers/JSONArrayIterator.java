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

package com.sonymobile.gitlab.helpers;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator for {@link JSONArray} arrays of {@link JSONObject}s.
 *
 * Note: the behaviour is undefined if the array is changed during iteration as there is no way to detect or prevent
 * that during iteration.
 *
 * @author Emil Nilsson
 * @see Iterator
 * @see Iterable
 */
public class JSONArrayIterator implements Iterator<JSONObject>, Iterable<JSONObject> {
    /** The JSON array to iterate. */
    private final JSONArray array;

    /** The current position in the array. */
    private int position = 0;

    /**
     * Creates an iterator for a JSON array.
     *
     * @param array the JSON array
     */
    public JSONArrayIterator(JSONArray array) {
        this.array = array;
    }

    /**
     * Creates a new instance of an iterator for a JSON array.
     *
     * @param array the JSON array
     * @return an iterator instance
     */
    public static JSONArrayIterator iterator(JSONArray array) {
        return new JSONArrayIterator(array);
    }

    @Override
    public synchronized boolean hasNext() {
        return position < array.length();
    }

    @Override
    public synchronized JSONObject next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return array.getJSONObject(position++);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("JSONArrayIterator#remove() is not supported");
    }

    @Override
    public Iterator<JSONObject> iterator() {
        return this;
    }
}