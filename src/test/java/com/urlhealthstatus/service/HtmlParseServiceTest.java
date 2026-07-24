package com.urlhealthstatus.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HtmlParseServiceTest {

    private HtmlParseService htmlParseService;

    @BeforeEach
    void setUp() {
        htmlParseService = new HtmlParseService();
    }

    @Test
    void happyPath_parsesAllMetrics() {
        String html =
                """
                <html>
                  <head>
                    <title>Example Title</title>
                    <meta name="description" content="An example page" />
                  </head>
                  <body>
                    <h1>Welcome</h1>
                    <p>Hello world from the page.</p>
                    <img src="/a.png" alt="logo" />
                    <img src="/b.png" />
                  </body>
                </html>
                """;

        HtmlParseService.ParsedPage parsed = htmlParseService.parse(html);

        assertEquals("Example Title", parsed.title());
        assertEquals("An example page", parsed.metaDescription());
        assertEquals(1, parsed.h1Count());
        assertEquals(1, parsed.imagesMissingAlt());
        assertTrue(parsed.wordCount() >= 5);
    }

    @Test
    void missingTitleMetaAndH1_returnsEmptyDefaults() {
        String html = "<html><head></head><body><p>Only a paragraph here.</p></body></html>";

        HtmlParseService.ParsedPage parsed = htmlParseService.parse(html);

        assertEquals("", parsed.title());
        assertEquals("", parsed.metaDescription());
        assertEquals(0, parsed.h1Count());
        assertEquals(0, parsed.imagesMissingAlt());
        assertTrue(parsed.wordCount() > 0);
    }

    @Test
    void imagesMissingAndEmptyAlt_areCounted() {
        String html =
                """
                <html><body>
                  <img src="1.png">
                  <img src="2.png" alt="">
                  <img src="3.png" alt="   ">
                  <img src="4.png" alt="ok">
                </body></html>
                """;

        HtmlParseService.ParsedPage parsed = htmlParseService.parse(html);

        assertEquals(3, parsed.imagesMissingAlt());
    }

    @Test
    void emptyHtml_doesNotCrash() {
        HtmlParseService.ParsedPage parsed = htmlParseService.parse("");

        assertEquals("", parsed.title());
        assertEquals("", parsed.metaDescription());
        assertEquals(0, parsed.h1Count());
        assertEquals(0, parsed.imagesMissingAlt());
        assertEquals(0, parsed.wordCount());
    }

    @Test
    void garbageNonHtml_parsesWithoutCrash() {
        HtmlParseService.ParsedPage parsed = htmlParseService.parse("{not: html, at: all}");

        assertEquals("", parsed.title());
        assertEquals("", parsed.metaDescription());
        assertEquals(0, parsed.h1Count());
        assertEquals(0, parsed.imagesMissingAlt());
        assertTrue(parsed.wordCount() >= 0);
    }

    @Test
    void wordCount_excludesScriptAndStyleText() {
        String html =
                """
                <html>
                  <head>
                    <style>.x { color: red; } lots of style words here</style>
                    <script>var ignoreTheseWordsPlease = true;</script>
                  </head>
                  <body>
                    <p>Visible one two three</p>
                  </body>
                </html>
                """;

        HtmlParseService.ParsedPage parsed = htmlParseService.parse(html);

        assertEquals(4, parsed.wordCount());
        assertFalse(HtmlParseService.isHtmlContentType("application/json"));
        assertTrue(HtmlParseService.isHtmlContentType("text/html; charset=utf-8"));
    }
}
