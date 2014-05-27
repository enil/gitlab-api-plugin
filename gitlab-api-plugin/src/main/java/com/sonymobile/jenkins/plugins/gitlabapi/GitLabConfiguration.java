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

package com.sonymobile.jenkins.plugins.gitlabapi;

import com.sonymobile.gitlab.api.GitLabApiClient;
import com.sonymobile.gitlab.exceptions.AuthenticationFailedException;
import com.sonymobile.gitlab.exceptions.GitLabApiException;
import com.sonymobile.jenkins.plugins.gitlabapi.exception.GitLabConfigurationException;
import hudson.Extension;
import hudson.ExtensionList;
import hudson.ProxyConfiguration;
import hudson.util.FormValidation;
import hudson.util.Secret;
import jenkins.model.GlobalConfiguration;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.util.List;
import java.util.regex.Pattern;

import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.StringUtils.trimToNull;

/**
 * Configuration page for GitLab server URI and the a private token.
 *
 * @author Andreas Alanko
 */
@Extension
public class GitLabConfiguration extends GlobalConfiguration {
    /** The GitLab server URL. */
    private String serverUrl;

    /** The private GitLab token. */
    private Secret privateToken;

    /** The proxy configuration */
    private transient ProxyConfiguration proxyConfiguration;

    /** The GitLab API client. */
    private transient GitLabApiClient client;

    /**
     * Creates a GitLab configuration object.
     *
     * Any previous settings are loaded.
     */
    public GitLabConfiguration() {
        proxyConfiguration = Jenkins.getInstance().proxy;
        load();
    }

    /**
     * Sets the GitLab server URL.
     *
     * @param serverUrl the server URL
     */
    public void setServerUrl(String serverUrl) {
        invalidateClient();
        this.serverUrl = serverUrl;
    }

    /**
     * Sets the private GitLab API token.
     *
     * @param privateToken the private token
     */
    public void setPrivateToken(String privateToken) {
        invalidateClient();
        this.privateToken = Secret.fromString(privateToken);
    }

    /**
     * Gets the GitLab server URL.
     *
     * @return the server URL
     */
    public String getServerUrl() {
        return serverUrl;
    }

    /**
     * Gets the private GitLab API token.
     *
     * @return the private token
     */
    public String getPrivateToken() {
        return Secret.toString(privateToken);
    }

    /**
     * Gets the configured proxy host.
     *
     * @return the proxy host or null if not set
     */
    public String getProxyHost() {
        return (proxyConfiguration != null) ? trimToNull(proxyConfiguration.name) : null;
    }

    /**
     * Gets the configured proxy port.
     *
     * @return the proxy port
     */
    public int getProxyPort() {
        return (proxyConfiguration != null) ? proxyConfiguration.port : 0;
    }

    /**
     * Gets the configured proxy username.
     *
     * @return the proxy username
     */
    public String getProxyUsername() {
        return (proxyConfiguration != null) ? trimToNull(proxyConfiguration.getUserName()) : null;
    }

    /**
     * Gets the configured proxy password.
     *
     * @return the proxy password
     */
    public String getProxyPassword() {
        return (proxyConfiguration != null) ? trimToNull(proxyConfiguration.getPassword()) : null;
    }

    /**
     * Gets the hosts configured to be excluded from the proxy.
     *
     * @return a list of patterns for hostnames
     */
    public List<Pattern> getNoProxyHostPatterns() {
        return (proxyConfiguration != null) ? proxyConfiguration.getNoProxyHostPatterns() : null;
    }

    /**
     * Invalidates the client, forcing a new one to be created next time someone tries to reach the old one.
     */
    private void invalidateClient() {
        client = null;
    }

    /**
     * Checks if the global proxy configuration has changed and in that case fetches the new values.
     */
    private void fetchProxyConfiguration() {
        // check if the proxy configuration has changed
        if (proxyConfiguration != Jenkins.getInstance().proxy) {
            proxyConfiguration = Jenkins.getInstance().proxy;
            invalidateClient();
        }
    }

    /**
     * Gets the API client using the configured settings.
     *
     * @return an GitLab API
     * @throws GitLabApiException if the client couldn't be created with the given values
     */
    private GitLabApiClient getClient() throws GitLabApiException {
        fetchProxyConfiguration();
        if (client == null) {
            testApiConnection(
                    getServerUrl(),
                    getPrivateToken(),
                    getProxyHost(),
                    getProxyPort(),
                    getProxyUsername(),
                    getProxyPassword(),
                    getNoProxyHostPatterns());
            return new GitLabApiClient(getServerUrl(),
                    getPrivateToken(),
                    getProxyHost(),
                    getProxyPort());
        }
        return client;
    }

    /**
     * Returns a API client using the configured settings.
     *
     * Null if the configured values are incorrect.
     *
     * @return an GitLab API or null if wrong values are configured
     */
    public static GitLabApiClient getApiClient() {
        GitLabConfiguration config = getInstance();

        try {
            if (config != null) {
                return config.getClient();
            }
        } catch (GitLabApiException e) {
            // fixme: use logger
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * Checks whether the required fields are set for API.
     *
     * This will check that serverUrl and privateToken has been set. Note: this method performs no sanity check on the
     * values, use {@link #testApiConnection(String, String, String, int, String, String, List<Pattern>)} to see whether
     * the server accepts the given values.
     *
     * @return true if the required fields are set
     */
    public static boolean isApiConfigured() {
        GitLabConfiguration config = getInstance();

        return config != null && isNotBlank(config.getServerUrl()) && isNotBlank(config.getPrivateToken());
    }

    /**
     * Tests if its possible to create a connection to the given GitLab host with the given parameters.
     *
     * @param serverUrl         the server url
     * @param privateToken      the private token
     * @param proxyHost         the proxy host
     * @param proxyPort         the proxy port
     * @param proxyUser         the proxy user
     * @param proxyPassword     the proxy password
     * @param excludedHostnames the excluded hosts
     * @throws GitLabApiException if a connection could not be established
     */
    private void testApiConnection(String serverUrl, String privateToken,
                                   String proxyHost, int proxyPort,
                                   String proxyUser, String proxyPassword,
                                   List<Pattern> excludedHostnames)
            throws GitLabApiException {

        if (StringUtils.isBlank(serverUrl)) {
            throw new GitLabConfigurationException("Server URL is not set");
        }

        if (StringUtils.isBlank(privateToken)) {
            throw new GitLabConfigurationException("Private token is not set");
        }

        GitLabApiClient.testConnection(
                serverUrl,
                privateToken,
                trimToNull(proxyHost),
                proxyPort,
                proxyUser,
                proxyPassword,
                excludedHostnames);
    }

    /**
     * Tests if its possible to create a connection to the given GitLab host with the given parameters.
     *
     * @param serverUrl    the GitLab host URL
     * @param privateToken the GitLab private token
     * @return a FormValidation object containing information about the test connection
     */
    public FormValidation doTestConnection(
            @QueryParameter("serverUrl") final String serverUrl,
            @QueryParameter("privateToken") final String privateToken) {

        try {
            fetchProxyConfiguration();
            testApiConnection(
                    serverUrl,
                    privateToken,
                    getProxyHost(),
                    getProxyPort(),
                    getProxyUsername(),
                    getProxyPassword(),
                    getNoProxyHostPatterns());
            return FormValidation.ok("Success: Connection established");
        } catch (AuthenticationFailedException e) {
            return FormValidation.error("Error: Host found but private token is incorrect");
        } catch (GitLabApiException e) {
            return FormValidation.error("Error: Could not establish a connection");
        }
    }

    /**
     * Saves the configured values from the submitted form.
     *
     * @return true if configuration succeeded else false
     */
    @Override
    public boolean configure(StaplerRequest req, JSONObject formData) {
        setServerUrl(formData.getString("serverUrl"));
        setPrivateToken(formData.getString("privateToken"));
        save();

        return true;
    }

    /**
     * Returns the singleton instance of this class.
     *
     * @return the instance or null if Jenkins misbehaves
     */
    public static GitLabConfiguration getInstance() {
        ExtensionList<GitLabConfiguration> list = Jenkins.getInstance().getExtensionList(GitLabConfiguration.class);

        // return the singleton instance if available from the extension list
        return (list != null && !list.isEmpty()) ? list.get(0) : null;
    }
}