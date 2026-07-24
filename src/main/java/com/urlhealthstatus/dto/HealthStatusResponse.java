package com.urlhealthstatus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HealthStatusResponse {

    private final String url;

    @JsonProperty("http_status")
    private final int httpStatus;

    @JsonProperty("response_time_ms")
    private final long responseTimeMs;

    private final String title;

    @JsonProperty("meta_description")
    private final String metaDescription;

    @JsonProperty("h1_count")
    private final int h1Count;

    @JsonProperty("images_missing_alt")
    private final int imagesMissingAlt;

    @JsonProperty("word_count")
    private final int wordCount;

    public HealthStatusResponse(
            String url,
            int httpStatus,
            long responseTimeMs,
            String title,
            String metaDescription,
            int h1Count,
            int imagesMissingAlt,
            int wordCount) {
        this.url = url;
        this.httpStatus = httpStatus;
        this.responseTimeMs = responseTimeMs;
        this.title = title;
        this.metaDescription = metaDescription;
        this.h1Count = h1Count;
        this.imagesMissingAlt = imagesMissingAlt;
        this.wordCount = wordCount;
    }

    public String getUrl() {
        return url;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public long getResponseTimeMs() {
        return responseTimeMs;
    }

    public String getTitle() {
        return title;
    }

    public String getMetaDescription() {
        return metaDescription;
    }

    public int getH1Count() {
        return h1Count;
    }

    public int getImagesMissingAlt() {
        return imagesMissingAlt;
    }

    public int getWordCount() {
        return wordCount;
    }
}
