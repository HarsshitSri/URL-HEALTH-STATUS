package com.urlhealthstatus.service;

import com.urlhealthstatus.exception.FetchFailedException;
import com.urlhealthstatus.exception.FetchTimeoutException;
import com.urlhealthstatus.exception.NonHtmlException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class PageFetchService {

    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    private final HttpClient httpClient;

    public PageFetchService() {
        this.httpClient =
                HttpClient.newBuilder()
                        .followRedirects(HttpClient.Redirect.NORMAL)
                        .connectTimeout(TIMEOUT)
                        .build();
    }

    public FetchedPage fetch(URI uri) {
        HttpRequest request =
                HttpRequest.newBuilder(uri)
                        .timeout(TIMEOUT)
                        .header("User-Agent", "URL-Health-Status/1.0")
                        .header("Accept", "text/html,application/xhtml+xml;q=0.9,*/*;q=0.8")
                        .GET()
                        .build();

        long started = System.nanoTime();
        try {
            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            long responseTimeMs = (System.nanoTime() - started) / 1_000_000L;

            Optional<String> contentType = response.headers().firstValue("Content-Type");
            if (contentType.isEmpty() || !HtmlParseService.isHtmlContentType(contentType.get())) {
                throw new NonHtmlException(
                        "Response Content-Type is not HTML"
                                + (contentType.map(ct -> " (got: " + ct + ")").orElse(" (missing)")));
            }

            return new FetchedPage(
                    response.uri().toString(),
                    response.statusCode(),
                    responseTimeMs,
                    response.body() == null ? "" : response.body());
        } catch (NonHtmlException ex) {
            throw ex;
        } catch (java.net.http.HttpTimeoutException ex) {
            throw new FetchTimeoutException("Timed out fetching URL after 10 seconds", ex);
        } catch (IOException ex) {
            if (isTimeout(ex)) {
                throw new FetchTimeoutException("Timed out fetching URL after 10 seconds", ex);
            }
            throw new FetchFailedException(buildFetchFailureMessage(uri, ex), ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new FetchFailedException("Fetch was interrupted", ex);
        } catch (IllegalArgumentException ex) {
            throw new FetchFailedException("Failed to fetch URL: " + ex.getMessage(), ex);
        }
    }

    private boolean isTimeout(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            if (current instanceof java.net.http.HttpTimeoutException
                    || current instanceof java.net.SocketTimeoutException) {
                return true;
            }
            String message = current.getMessage();
            if (message != null && message.toLowerCase().contains("timed out")) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    private String buildFetchFailureMessage(URI uri, IOException exception) {
        String detail = exception.getMessage();
        if (detail == null || detail.isBlank()) {
            detail = exception.getClass().getSimpleName();
        }
        return "Failed to fetch " + uri.getHost() + ": " + detail;
    }

    public record FetchedPage(String finalUrl, int httpStatus, long responseTimeMs, String body) {}
}
