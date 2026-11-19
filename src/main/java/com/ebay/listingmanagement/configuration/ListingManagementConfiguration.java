// Copyright 2026 eBay Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.ebay.listingmanagement.configuration;

import jakarta.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public final class ListingManagementConfiguration {
    private @Nullable String accessToken;
    private RequestOptions defaultRequestOptions = new RequestOptions();

    private final String feedBaseUrl;
    private final String inventoryBaseUrl;
    private final String mediaBaseUrl;
    private final String storesBaseUrl;
    private final String graphQLBaseUrl;
    private final boolean localTesting;

    public ListingManagementConfiguration() {
        this.localTesting = false;
        this.feedBaseUrl = "https://api.ebay.com/sell/feed/v1";
        this.inventoryBaseUrl = "https://api.ebay.com/sell/inventory/v1";
        this.mediaBaseUrl = "https://apim.ebay.com/commerce/media/v1_beta";
        this.storesBaseUrl = "https://api.ebay.com/sell/stores/v1";
        this.graphQLBaseUrl = "https://api.ebay.com/graphql";
    }

    public static ListingManagementConfiguration forLocalTesting(int port) {
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("Invalid local test port: " + port);
        }
        String origin = "http://127.0.0.1:" + port;
        return new ListingManagementConfiguration(
            origin + "/sell/feed/v1",
            origin + "/sell/inventory/v1",
            origin + "/commerce/media/v1_beta",
            origin + "/sell/stores/v1",
            origin + "/graphql",
            true);
    }

    private ListingManagementConfiguration(String feedBaseUrl, String inventoryBaseUrl,
            String mediaBaseUrl, String storesBaseUrl, String graphQLBaseUrl, boolean localTesting) {
        this.localTesting = localTesting;
        this.feedBaseUrl = feedBaseUrl;
        this.inventoryBaseUrl = inventoryBaseUrl;
        this.mediaBaseUrl = mediaBaseUrl;
        this.storesBaseUrl = storesBaseUrl;
        this.graphQLBaseUrl = graphQLBaseUrl;
    }

    public ListingManagementConfiguration(
            String feedBaseUrl,
            String inventoryBaseUrl,
            String mediaBaseUrl,
            String storesBaseUrl,
            String graphQLBaseUrl) {
        this.localTesting = false;
        this.feedBaseUrl = validateHost(feedBaseUrl);
        this.inventoryBaseUrl = validateHost(inventoryBaseUrl);
        this.mediaBaseUrl = validateHost(mediaBaseUrl);
        this.storesBaseUrl = validateHost(storesBaseUrl);
        this.graphQLBaseUrl = validateHost(graphQLBaseUrl);
    }

    private String validateHost(String url) {
        try {
            String host = new java.net.URI(url).getHost();
            if (host == null || !getAllowedHosts().contains(host)) {
                throw new IllegalArgumentException("URL host not in allowed list: " + url);
            }
        } catch (java.net.URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URL: " + url, e);
        }
        return url;
    }

    /**
     * Sets the OAuth Bearer token used for API calls when no per-call token is provided.
     *
     * <p><b>Security note:</b> The token is stored as a Java {@code String} and will remain
     * in heap memory until garbage collected. To minimize exposure: do not log this
     * configuration object, null out the token reference when it is no longer needed,
     * and avoid storing the configuration longer than the client lifecycle.
     */
    public void setAccessToken(@Nullable String token) {
        this.accessToken = token;
    }

    public @Nullable String getAccessToken() {
        return accessToken;
    }

    public RequestOptions getDefaultRequestOptions() {
        return defaultRequestOptions;
    }

    public Set<String> getAllowedHosts() {
        Set<String> hosts = new HashSet<>();
        hosts.add("api.ebay.com");
        hosts.add("apim.ebay.com");
        if (localTesting) {
            hosts.add("127.0.0.1");
        }
        return hosts;
    }

    public String getFeedBaseUrl() {
        return feedBaseUrl;
    }

    public String getInventoryBaseUrl() {
        return inventoryBaseUrl;
    }

    public String getMediaBaseUrl() {
        return mediaBaseUrl;
    }

    public String getStoresBaseUrl() {
        return storesBaseUrl;
    }

    public String getGraphQLBaseUrl() {
        return graphQLBaseUrl;
    }
}
