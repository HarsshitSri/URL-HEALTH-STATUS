package com.urlhealthstatus.service;

import java.util.Locale;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class HtmlParseService {

    public ParsedPage parse(String html) {
        Document document = Jsoup.parse(html == null ? "" : html);

        String title = document.title();
        if (title == null) {
            title = "";
        }

        String metaDescription = "";
        Element meta = document.selectFirst("meta[name=description]");
        if (meta != null) {
            String content = meta.attr("content");
            metaDescription = content == null ? "" : content;
        }

        int h1Count = document.select("h1").size();
        int imagesMissingAlt = countImagesMissingAlt(document);
        int wordCount = countVisibleWords(document);

        return new ParsedPage(title, metaDescription, h1Count, imagesMissingAlt, wordCount);
    }

    private int countImagesMissingAlt(Document document) {
        Elements images = document.select("img");
        int missing = 0;
        for (Element img : images) {
            if (!img.hasAttr("alt") || img.attr("alt").trim().isEmpty()) {
                missing++;
            }
        }
        return missing;
    }

    private int countVisibleWords(Document document) {
        Document copy = document.clone();
        copy.select("script, style, noscript").remove();
        String text = copy.body() != null ? copy.body().text() : copy.text();
        if (text == null || text.isBlank()) {
            return 0;
        }
        String[] words = text.trim().split("\\s+");
        return words.length;
    }

    public static boolean isHtmlContentType(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return false;
        }
        String mediaType = contentType.split(";", 2)[0].trim().toLowerCase(Locale.ROOT);
        return mediaType.equals("text/html") || mediaType.equals("application/xhtml+xml");
    }

    public record ParsedPage(
            String title,
            String metaDescription,
            int h1Count,
            int imagesMissingAlt,
            int wordCount) {}
}
