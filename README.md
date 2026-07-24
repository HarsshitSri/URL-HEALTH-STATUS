# URL Health Status

Small Spring Boot tool that audits any public HTTP(S) URL and returns a JSON page-health report. A simple web UI calls the API and renders the result.

## Setup

### Prerequisites

- Java 17+
- Maven Wrapper is included (`./mvnw`); system Maven 3.9+ also works

### Run locally

```bash
./mvnw spring-boot:run
```

Or build and run the JAR:

```bash
./mvnw -DskipTests package
java -jar target/url-health-status-0.0.1-SNAPSHOT.jar
```

Open [http://localhost:8080](http://localhost:8080).

### Run tests

```bash
./mvnw test
```

## API contract

### Request

```
GET /api/health-status?url={absolute-http-or-https-url}
```

Example:

```bash
curl "http://localhost:8080/api/health-status?url=https%3A%2F%2Fexample.com"
```

### Success — `200 OK`

```json
{
  "url": "https://example.com",
  "http_status": 200,
  "response_time_ms": 123,
  "title": "Example Domain",
  "meta_description": "",
  "h1_count": 1,
  "images_missing_alt": 0,
  "word_count": 20
}
```

| Field | Meaning |
|-------|---------|
| `url` | Final URL after redirects |
| `http_status` | Final HTTP status code |
| `response_time_ms` | Round-trip fetch time in milliseconds |
| `title` | Document title, or `""` if missing |
| `meta_description` | `meta[name=description]` content, or `""` if missing |
| `h1_count` | Number of `h1` elements |
| `images_missing_alt` | Images with no `alt`, or empty/whitespace `alt` |
| `word_count` | Approximate visible-text word count |

### Errors

JSON body:

```json
{
  "error": "human-readable message",
  "code": "INVALID_URL"
}
```

| Condition | HTTP status | `code` |
|-----------|-------------|--------|
| Missing / invalid / non-http(s) URL | `400` | `INVALID_URL` |
| Upstream fetch timed out (10s) | `504` | `TIMEOUT` |
| `Content-Type` missing or not HTML | `415` | `NON_HTML` |
| Other fetch failures (DNS, connection, etc.) | `502` | `FETCH_FAILED` |

## Design decisions

1. **GET with a `url` query parameter**  
   The UI is a single input field, and recruiters often poke the API with a browser or `curl`. Query-param GET keeps both paths trivial (encode the URL and call). A POST JSON body would be slightly cleaner for complex payloads, but this tool only needs one string.

2. **Non-HTML detection via `Content-Type` only**  
   HTTP already declares media type in `Content-Type`. Accepting `text/html` and `application/xhtml+xml` (ignoring parameters like `charset`) is a clear, testable rule. Body sniffing is heuristic and harder to defend in review; a natural follow-up would be a careful fallback when the header is missing or wrong.

3. **JDK `HttpClient` for fetch + Jsoup for parse**  
   Separating network I/O from HTML parsing keeps timeouts, redirects, and status measurement explicit, and makes the parser easy to unit-test with fixture strings. Using Jsoup’s `connect()` would blur those concerns and make timeout/error mapping less transparent.

## Deploy on Render (Docker)

1. Push this repo to a **public GitHub** repository.
2. Sign up / log in at [https://render.com](https://render.com).
3. **New +** → **Web Service** → connect the GitHub repo.
4. Configure:
   - **Runtime:** Docker
   - **Dockerfile path:** `./Dockerfile`
   - **Instance type:** Free
5. Create the service and wait for the first deploy.
6. Open the `*.onrender.com` URL. Free tier sleeps after ~15 minutes idle; the first request after sleep can take ~30–60 seconds while Spring Boot starts — refresh once if needed.

Optional: set env var `JAVA_OPTS` in Render if you want different JVM flags (defaults are set in the Dockerfile).

## Project structure

- `src/main/java/com/urlhealthstatus/` — Spring Boot app, services, API
- `src/main/resources/static/` — UI (`index.html`, `styles.css`, `app.js`)
- `src/test/java/.../HtmlParseServiceTest.java` — parsing unit tests
- `Dockerfile` — multi-stage Maven build for Render
