package com.urlhealthstatus.service;

import com.urlhealthstatus.exception.InvalidUrlException;
import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UrlValidationService {

    public URI validate(String url) {
        if (!StringUtils.hasText(url)) {
            throw new InvalidUrlException("URL is required");
        }

        String trimmed = url.trim();
        URI uri;
        try {
            uri = new URI(trimmed);
        } catch (URISyntaxException ex) {
            throw new InvalidUrlException("URL is not a valid absolute HTTP or HTTPS address");
        }

        if (!uri.isAbsolute()) {
            throw new InvalidUrlException("URL must be absolute (include http:// or https://)");
        }

        String scheme = uri.getScheme();
        if (scheme == null
                || !(scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))) {
            throw new InvalidUrlException("URL scheme must be http or https");
        }

        if (!StringUtils.hasText(uri.getHost())) {
            throw new InvalidUrlException("URL must include a host");
        }

        return uri;
    }
}
