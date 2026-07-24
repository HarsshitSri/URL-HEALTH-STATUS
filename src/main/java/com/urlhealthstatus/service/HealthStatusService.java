package com.urlhealthstatus.service;

import com.urlhealthstatus.dto.HealthStatusResponse;
import java.net.URI;
import org.springframework.stereotype.Service;

@Service
public class HealthStatusService {

    private final UrlValidationService urlValidationService;
    private final PageFetchService pageFetchService;
    private final HtmlParseService htmlParseService;

    public HealthStatusService(
            UrlValidationService urlValidationService,
            PageFetchService pageFetchService,
            HtmlParseService htmlParseService) {
        this.urlValidationService = urlValidationService;
        this.pageFetchService = pageFetchService;
        this.htmlParseService = htmlParseService;
    }

    public HealthStatusResponse audit(String url) {
        URI uri = urlValidationService.validate(url);
        PageFetchService.FetchedPage fetched = pageFetchService.fetch(uri);
        HtmlParseService.ParsedPage parsed = htmlParseService.parse(fetched.body());

        return new HealthStatusResponse(
                fetched.finalUrl(),
                fetched.httpStatus(),
                fetched.responseTimeMs(),
                parsed.title(),
                parsed.metaDescription(),
                parsed.h1Count(),
                parsed.imagesMissingAlt(),
                parsed.wordCount());
    }
}
