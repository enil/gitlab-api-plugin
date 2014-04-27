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

import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.sonymobile.gitlab.helpers.JSONArrayIterator.iterator;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.reflect.ConstructorUtils.invokeConstructor;

/**
 * A utility class for loading data from JSON files.
 *
 * @author Emil Nilsson
 */
public class JsonFileLoader {
    /**
     * The directory under resources to load the files from.
     *
     * Uses the same directory as WireMock for convenience.
     */
    private static final String FILES_DIRECTORY_PATH = "/__files/";

    /** The file suffix for JSON files. */
    private static final String JSON_FILE_SUFFIX = ".json";

    /** The relative file path (excluding file suffix and variant name). */
    private final String filePath;

    /** The file variant name (inserted before the file suffix). */
    private String variant;

    /** The index in an array in the JSON file to load from. */
    private Integer index;

    /**
     * Creates a JSON file loader.
     *
     * @param filePath the file path (excluding file suffix and variant name)
     */
    private JsonFileLoader(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Initializes a new JSON file loader.
     *
     * @param filePath the file path
     * @return a JSON file loader
     */
    public static JsonFileLoader jsonFile(String filePath) {
        return new JsonFileLoader(filePath);
    }

    /**
     * Specifies which file variant to use.
     *
     * The variant name will be appended before the file suffix prepended with a underscore sign.
     *
     * @param variant the variant name
     * @return this object for chaining
     */
    public JsonFileLoader withVariant(String variant) {
        this.variant = variant;
        return this;
    }

    /**
     * Specifies the index in the array to load the object from.
     *
     * @param index the index in the array
     * @return this object for chaining
     */
    public JsonFileLoader fromIndex(int index) {
        this.index = index;
        return this;
    }

    /**
     * Creates an loader for loading the JSON file as an object of a specific type.
     *
     * @param type the class for the type
     * @param <T>  the type
     * @return a loader for the type
     */
    public <T> ObjectLoader<T> withType(Class<T> type) {
        final ObjectLoader<T> loader = new ObjectLoader<T>();
        loader.type = type;

        return loader;
    }

    /**
     * Loads the JSON file as a JSON object.
     *
     * @return a JSON object from the file
     * @throws IOException if the file could not be loaded
     */
    public JSONObject loadAsObject() throws IOException {
        String fileContent = loadFileContent();

        if (index != null) {
            return new JSONArray(fileContent).getJSONObject(index);
        } else {
            return new JSONObject(fileContent);
        }
    }

    /**
     * Loads the JSON file as a JSON array.
     *
     * @return a JSON array from the file
     * @throws IOException if the file could not be loaded
     */
    public JSONArray loadAsArray() throws IOException {
        String fileContent = loadFileContent();

        if (index != null) {
            return new JSONArray(fileContent).getJSONArray(index);
        } else {
            return new JSONArray(fileContent);
        }
    }

    /**
     * Gets the absolute file path for the JSON file.
     *
     * @return the absolute file path
     * @throws IOException if the file could not be loaded
     */
    private String getAbsoluteResourceFilePath() throws IOException {
        // add variant before file suffix if specified
        String suffix = (isBlank(variant) ? "" : "_" + variant) + JSON_FILE_SUFFIX;
        String relativePath = FILES_DIRECTORY_PATH + filePath + suffix;

        URL url = JsonFileLoader.class.getResource(relativePath);
        if (url == null) {
            throw new FileNotFoundException("The file " + relativePath + " doesn't exist in the resources directory");
        }
        return url.getFile();
    }

    /**
     * Loads the content of the JSON file into a string.
     *
     * @return the content of the file
     * @throws IOException if the file could not be loaded
     */
    private String loadFileContent() throws IOException {
        return readFileToString(new File(getAbsoluteResourceFilePath()));
    }

    /**
     * Class for loading the JSON file as a object of a specific type.
     *
     * @param <T> the type of the object
     */
    public class ObjectLoader<T> {
        /** The class of the type. */
        private Class<T> type;

        /** The constructor parameters. */
        private Object[] constructorParameters;

        /**
         * @see JsonFileLoader#withVariant(String)
         */
        public ObjectLoader<T> withVariant(String variant) {
            JsonFileLoader.this.withVariant(variant);
            return this;
        }

        /**
         * @see JsonFileLoader#fromIndex(int)
         */
        public ObjectLoader<T> fromIndex(int index) {
            JsonFileLoader.this.fromIndex(index);
            return this;
        }

        /**
         * Sets the constructor parameters.
         *
         * @return this object for chaining
         */
        public ObjectLoader<T> andParameters(Object... constructorParameters) {
            this.constructorParameters = constructorParameters;
            return this;
        }

        /**
         * Loads the JSON file into an object of the object type.
         *
         * The type must have a constructor taking at least a single {@link JSONObject} and is followed by the
         * specified constructor parameters (if any).
         *
         * @return the object
         * @throws IOException               if the file could not be loaded
         * @throws InvocationTargetException if the constructor threw an exception
         */
        public T loadAsObject()
                throws IOException, InvocationTargetException {
            // load the JSON object and pass to the constructor for the type
            return jsonObjectToObject(JsonFileLoader.this.loadAsObject(), type, constructorParameters);
        }

        /**
         * Loads the JSON file as a list of elements of the object type.
         *
         * @return the list of objects
         * @throws IOException               if the file could not be loaded
         * @throws InvocationTargetException if the constructor threw an exception
         * @see #loadAsObject()
         */
        public List<T> loadAsArray()
                throws IOException, InvocationTargetException {
            JSONArray array = JsonFileLoader.this.loadAsArray();

            // load each element in the array as the object type
            List<T> elements = new ArrayList<T>(array.length());
            for (JSONObject jsonObject : iterator(array)) {
                elements.add(jsonObjectToObject(jsonObject, type, constructorParameters));
            }

            return elements;
        }

        /**
         * Create an object of the object type from a JSON object.
         *
         * The type must have a constructor taking a single {@link JSONObject} followed by the parameters passed to the
         * method.
         *
         * @param jsonObject the JSON object
         * @param type       the class of the returning object
         * @param parameters parameters for the constructor
         * @param <T>        the type of the object
         * @return an object of the specified type
         * @throws IOException               if the file could not be loaded
         * @throws InvocationTargetException if the constructor threw an exception
         */
        private <T> T jsonObjectToObject(JSONObject jsonObject, Class<T> type, Object... parameters)
                throws IOException, InvocationTargetException {
            // inserts the JSON object as the first constructor parameter
            Object[] constructorParameters = ArrayUtils.add(parameters, 0, jsonObject);

            try {
                return invokeConstructor(type, constructorParameters);
            } catch (InvocationTargetException e) {
                // constructor threw an exception when invoked
                throw e;
            } catch (Exception e) {
                // couldn't find a matching constructor
                throw new IllegalArgumentException("No suitable constructor found for " + type);
            }
        }
    }
}
