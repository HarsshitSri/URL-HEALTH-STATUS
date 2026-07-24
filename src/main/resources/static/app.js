const form = document.getElementById("audit-form");
const urlInput = document.getElementById("url-input");
const submitBtn = document.getElementById("submit-btn");
const statusEl = document.getElementById("status");
const errorEl = document.getElementById("error");
const reportEl = document.getElementById("report");
const reportFields = document.getElementById("report-fields");

const FIELD_LABELS = [
  ["url", "Final URL"],
  ["http_status", "HTTP status"],
  ["response_time_ms", "Response time (ms)"],
  ["title", "Title"],
  ["meta_description", "Meta description"],
  ["h1_count", "H1 count"],
  ["images_missing_alt", "Images missing alt"],
  ["word_count", "Word count"],
];

function setLoading(isLoading) {
  submitBtn.disabled = isLoading;
  submitBtn.textContent = isLoading ? "Checking…" : "Check";
  statusEl.hidden = !isLoading;
  statusEl.textContent = isLoading ? "Fetching and analyzing the page…" : "";
}

function showError(message) {
  reportEl.hidden = true;
  errorEl.hidden = false;
  errorEl.textContent = message;
}

function showReport(data) {
  errorEl.hidden = true;
  reportFields.innerHTML = "";

  for (const [key, label] of FIELD_LABELS) {
    const row = document.createElement("div");
    const dt = document.createElement("dt");
    const dd = document.createElement("dd");
    dt.textContent = label;
    const value = data[key];
    dd.textContent = value === "" || value === null || value === undefined ? "—" : String(value);
    row.append(dt, dd);
    reportFields.append(row);
  }

  reportEl.hidden = false;
}

form.addEventListener("submit", async (event) => {
  event.preventDefault();
  const url = urlInput.value.trim();
  if (!url) {
    showError("Please enter a URL.");
    return;
  }

  setLoading(true);
  errorEl.hidden = true;
  reportEl.hidden = true;

  try {
    const response = await fetch(
      `/api/health-status?url=${encodeURIComponent(url)}`
    );
    const payload = await response.json().catch(() => null);

    if (!response.ok) {
      const message =
        payload && payload.error
          ? `${payload.error}${payload.code ? ` (${payload.code})` : ""}`
          : `Request failed with status ${response.status}`;
      showError(message);
      return;
    }

    showReport(payload);
  } catch (err) {
    showError("Could not reach the server. Please try again.");
  } finally {
    setLoading(false);
  }
});
