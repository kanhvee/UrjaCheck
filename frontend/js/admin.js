/* ============================================================
   UrjaCheck — admin.js
   Admin-side dashboard logic.

   INTEGRATION NOTE: all statistics now come from the backend's
   real, stored assessments:
     GET {API_BASE_URL}/admin/summary
     GET {API_BASE_URL}/admin/households
     GET {API_BASE_URL}/admin/appliances
     GET {API_BASE_URL}/assessment/{id}   (household drill-down)
   No client-side dummy data is generated. A freshly-created database
   with zero assessments will show honest empty states rather than
   fabricated numbers.
   ============================================================ */

const ADMIN_CREDENTIALS = { username: "admin", password: "admin123" }; // DEMO AUTH ONLY — never use in production.

let selectedHouseholdId = null;
let cachedHouseholds = []; // last fetched /admin/households response, filtered/sorted client-side

/* ---------------- Auth ---------------- */

async function adminLogin() {
  const key = document.getElementById("adminKey").value.trim();
  const errorEl = document.getElementById("loginError");

  if (!key) {
    errorEl.textContent = "Please enter the admin key.";
    errorEl.style.display = "block";
    return;
  }

  try {
    const res = await fetch(API_BASE_URL + "/admin/summary", {
      headers: {
        "X-Admin-Key": key
      }
    });

    if (!res.ok) {
      errorEl.textContent = "Invalid admin key. Please try again.";
      errorEl.style.display = "block";
      return;
    }

    // Keep the key only in this browser tab's memory.
    sessionStorage.setItem("urjacheckAdminKey", key);

    errorEl.style.display = "none";
    document.getElementById("loginScreen").style.display = "none";
    document.getElementById("adminApp").style.display = "block";

    showAdminView("dashboard");

  } catch (err) {
    console.error("Admin login error:", err);
    errorEl.textContent = "Could not connect to the UrjaCheck server.";
    errorEl.style.display = "block";
  }
}

function adminLogout() {
  sessionStorage.removeItem("urjacheckAdminKey");

  document.getElementById("adminApp").style.display = "none";
  document.getElementById("loginScreen").style.display = "flex";
  document.getElementById("adminKey").value = "";
}

/* ---------------- View routing ---------------- */

function showAdminView(name) {
  document.querySelectorAll(".admin-view").forEach(v => v.classList.remove("active"));
  const target = document.getElementById("admin-view-" + name);
  if (target) target.classList.add("active");

  document.querySelectorAll("#adminMainNav button").forEach(b => b.classList.remove("active"));
  const navBtn = document.querySelector('#adminMainNav button[data-nav="' + name + '"]');
  if (navBtn) navBtn.classList.add("active");

  window.scrollTo({ top: 0 });

  if (name === "dashboard") renderDashboard();
  if (name === "households") loadAndRenderHouseholds();
  if (name === "appliances") renderApplianceAnalytics();
  if (name === "analytics") renderBenchmarkAnalytics();

  const nav = document.getElementById("adminMainNav");
  if (nav) nav.classList.remove("mobile-open");
}

document.addEventListener("DOMContentLoaded", () => {
  const toggle = document.getElementById("adminNavToggle");
  if (toggle) {
    toggle.addEventListener("click", () => {
      document.getElementById("adminMainNav").classList.toggle("mobile-open");
    });
  }
  document.querySelectorAll(".admin-view").forEach(v => v.classList.add("view"));
});

/* ---------------- Fetch helper ---------------- */

async function fetchJson(path) {
  const adminKey = sessionStorage.getItem("urjacheckAdminKey");

  const res = await fetch(API_BASE_URL + path, {
    headers: {
      "X-Admin-Key": adminKey || ""
    }
  });

  if (!res.ok) {
    const body = await res.json().catch(() => ({}));

    if (res.status === 401) {
      sessionStorage.removeItem("urjacheckAdminKey");
      document.getElementById("adminApp").style.display = "none";
      document.getElementById("loginScreen").style.display = "flex";

      const errorEl = document.getElementById("loginError");
      errorEl.textContent = "Your admin session is invalid. Please log in again.";
      errorEl.style.display = "block";
    }

    throw new Error(
      body.message || ("Request to " + path + " failed with status " + res.status)
    );
  }

  return res.json();
}

function handleFetchError(err, context) {
  console.error(context, err);
  showAdminToast(
    err.message && err.message !== "Failed to fetch"
      ? err.message
      : "Could not reach the UrjaCheck server. Is the backend running on " + API_BASE_URL + "?"
  );
}

/* ---------------- Dashboard ---------------- */

async function renderDashboard() {
  try {
    const c = await fetchJson("/admin/summary");
    const chartCard = document.getElementById("dashboardChartCard");
    const emptyEl = document.getElementById("dashboardEmpty");

    if (!c.totalAssessments) {
      document.getElementById("dashboardStats").innerHTML = "";
      chartCard.style.display = "none";
      emptyEl.style.display = "block";
      return;
    }
    chartCard.style.display = "block";
    emptyEl.style.display = "none";

    const pctAbove = Math.round((c.householdsAboveReference / c.totalAssessments) * 100);

    document.getElementById("dashboardStats").innerHTML = `
      <div class="stat-card"><div class="lbl">Households Assessed</div><div class="num">${c.totalAssessments}</div></div>
      <div class="stat-card"><div class="lbl">Average Monthly Consumption</div><div class="num">${c.averageMonthlyConsumption} kWh</div></div>
      <div class="stat-card"><div class="lbl">Average Estimated Cost</div><div class="num">₹${c.averageEstimatedCost.toLocaleString("en-IN")}</div></div>
      <div class="stat-card"><div class="lbl">Households Above Reference</div><div class="num">${pctAbove}%</div></div>
    `;

    const maxVal = Math.max(c.averageMonthlyConsumption, c.averageReferenceConsumption, 1);
    document.getElementById("communityCompareBars").innerHTML = `
      <div class="compare-bar-row">
        <div class="bar-label"><span>Average actual</span><span>${c.averageMonthlyConsumption} kWh</span></div>
        <div class="compare-bar-track"><div class="compare-bar-fill you" style="width:${(c.averageMonthlyConsumption / maxVal * 100)}%"></div></div>
      </div>
      <div class="compare-bar-row">
        <div class="bar-label"><span>Average reference</span><span>${c.averageReferenceConsumption} kWh</span></div>
        <div class="compare-bar-track"><div class="compare-bar-fill ref" style="width:${(c.averageReferenceConsumption / maxVal * 100)}%"></div></div>
      </div>
    `;
  } catch (err) {
    handleFetchError(err, "renderDashboard");
  }
}

/* ---------------- Households table ---------------- */

async function loadAndRenderHouseholds() {
  try {
    cachedHouseholds = await fetchJson("/admin/households");
    renderHouseholdTable();
  } catch (err) {
    handleFetchError(err, "loadAndRenderHouseholds");
  }
}

function renderHouseholdTable() {
  const search = (document.getElementById("householdSearch")?.value || "").trim().toLowerCase();
  const statusFilter = document.getElementById("statusFilter")?.value || "all";
  const sortBy = document.getElementById("sortBy")?.value || "consumption_desc";

  let rows = cachedHouseholds.filter(h => {
    const matchesSearch = !search || h.assessmentId.toLowerCase().includes(search);
    const matchesStatus = statusFilter === "all" || h.status === statusFilter;
    return matchesSearch && matchesStatus;
  });

  if (sortBy === "consumption_desc") rows.sort((a, b) => b.totalMonthlyKwh - a.totalMonthlyKwh);
  if (sortBy === "consumption_asc") rows.sort((a, b) => a.totalMonthlyKwh - b.totalMonthlyKwh);
  if (sortBy === "id") rows.sort((a, b) => a.assessmentId.localeCompare(b.assessmentId));

  const tbody = document.getElementById("householdTableBody");
  const emptyEl = document.getElementById("householdTableEmpty");

  if (rows.length === 0) {
    tbody.innerHTML = "";
    emptyEl.style.display = "block";
    return;
  }
  emptyEl.style.display = "none";

  tbody.innerHTML = rows.map(h => {
    const diff = h.differenceKwh;
    return `
    <tr onclick="openHouseholdDetail('${h.assessmentId}')">
      <td><b>${h.assessmentId}</b></td>
      <td>${h.householdSize}</td>
      <td>${h.houseType || "—"}</td>
      <td>${h.totalMonthlyKwh}</td>
      <td>${h.referenceMonthlyKwh}</td>
      <td>${diff >= 0 ? "+" : ""}${diff}</td>
      <td><span class="status-chip ${STATUS_CLASSES[h.status] || "status-good"}">${STATUS_LABELS[h.status] || h.status}</span></td>
    </tr>`;
  }).join("");
}

/* ---------------- Household detail ---------------- */

// Maps GET /api/assessment/{id} (AssessmentResponse) into the shape the
// detail view below renders, mirroring app.js's mapApiResponseToLocalAssessment.
function mapAssessmentDetail(apiResponse) {
  const s = apiResponse.summary;
  return {
    assessmentId: apiResponse.assessmentId,
    householdSize: apiResponse.householdSize,
    houseType: apiResponse.houseType,
    status: s.status,
    totalMonthlyKwh: s.monthlyKwh,
    referenceMonthlyKwh: s.referenceMonthlyKwh,
    differenceKwh: s.differenceKwh,
    estimatedMonthlyCost: s.estimatedMonthlyCost,
    appliances: apiResponse.appliances.map(a => ({
      applianceType: a.applianceName,
      quantity: a.quantity,
      powerWatts: a.powerWatts,
      operatingHours: a.hoursPerDay,
      monthlyEnergyKWh: a.monthlyKwh,
      referenceEnergyKWh: a.referenceMonthlyKwh
    }))
  };
}

async function openHouseholdDetail(id) {
  selectedHouseholdId = id;
  try {
    const apiResponse = await fetchJson("/assessment/" + encodeURIComponent(id));
    const h = mapAssessmentDetail(apiResponse);

    const sorted = [...h.appliances].sort((a, b) => b.monthlyEnergyKWh - a.monthlyEnergyKWh);
    const highest = sorted[0] ? sorted[0].applianceType : "—";

    document.getElementById("householdDetailContent").innerHTML = `
      <div class="admin-page-head"><h1>Assessment ${h.assessmentId}</h1>
        <span class="status-chip ${STATUS_CLASSES[h.status] || "status-good"}">${STATUS_LABELS[h.status] || h.status}</span>
      </div>
      <div class="household-detail-grid">
        <div class="card">
          <h3 style="margin-top:0;">Household</h3>
          <div class="summary-list">
            <div class="row"><span>People</span><b>${h.householdSize}</b></div>
            <div class="row"><span>Home type</span><b>${h.houseType || "—"}</b></div>
            <div class="row"><span>Highest consumer</span><b>${highest}</b></div>
          </div>
        </div>
        <div class="card">
          <h3 style="margin-top:0;">Consumption</h3>
          <div class="summary-list">
            <div class="row"><span>Monthly consumption</span><b>${h.totalMonthlyKwh} kWh</b></div>
            <div class="row"><span>Reference</span><b>${h.referenceMonthlyKwh} kWh</b></div>
            <div class="row"><span>Difference</span><b>${h.differenceKwh >= 0 ? "+" : ""}${h.differenceKwh} kWh</b></div>
            <div class="row"><span>Estimated cost</span><b>₹${h.estimatedMonthlyCost.toLocaleString("en-IN")}</b></div>
          </div>
        </div>
      </div>
      <div class="card">
        <h3 style="margin-top:0;">Appliances</h3>
        <div class="table-wrap">
          <table class="mini-table">
            <thead><tr><th>Appliance</th><th>Quantity</th><th>Power</th><th>Hours/day</th><th>Monthly kWh</th><th>Reference kWh</th></tr></thead>
            <tbody>
              ${sorted.map(a => `<tr>
                <td>${(APPLIANCE_CATALOG[a.applianceType] || {}).icon || "🔌"} ${a.applianceType}</td>
                <td>${a.quantity}</td>
                <td>${a.powerWatts} W</td>
                <td>${a.operatingHours}</td>
                <td>${a.monthlyEnergyKWh}</td>
                <td>${a.referenceEnergyKWh}</td>
              </tr>`).join("")}
            </tbody>
          </table>
        </div>
      </div>
    `;

    document.querySelectorAll(".admin-view").forEach(v => v.classList.remove("active"));
    document.getElementById("admin-view-household-detail").classList.add("active");
  } catch (err) {
    handleFetchError(err, "openHouseholdDetail");
  }
}

/* ---------------- Appliance analytics ---------------- */

async function renderApplianceAnalytics() {
  try {
    const list = await fetchJson("/admin/appliances");

    if (!list.length) {
      document.getElementById("applianceShareChart").innerHTML = `<p class="field-hint">No appliance data available.</p>`;
      document.getElementById("commonAppliancesTable").innerHTML = "";
      return;
    }

    const grandTotal = list.reduce((s, e) => s + e.totalMonthlyKwh, 0) || 1;
    const shareEntries = [...list]
      .sort((a, b) => b.totalMonthlyKwh - a.totalMonthlyKwh)
      .slice(0, 8)
      .map(e => ({ ...e, pct: (e.totalMonthlyKwh / grandTotal) * 100 }));

    document.getElementById("applianceShareChart").innerHTML = shareEntries.map(e => `
      <div class="hbar-row">
        <div class="hbar-label"><span>${(APPLIANCE_CATALOG[e.applianceName] || {}).icon || "🔌"} ${e.applianceName}</span><span>${e.pct.toFixed(1)}%</span></div>
        <div class="hbar-track"><div class="hbar-fill" style="width:${e.pct.toFixed(1)}%"></div></div>
      </div>
    `).join("");

    const commonEntries = [...list].sort((a, b) => b.householdCount - a.householdCount).slice(0, 8);
    document.getElementById("commonAppliancesTable").innerHTML = `
      <thead><tr><th>Appliance</th><th>Households</th></tr></thead>
      <tbody>
        ${commonEntries.map(e => `<tr><td>${(APPLIANCE_CATALOG[e.applianceName] || {}).icon || "🔌"} ${e.applianceName}</td><td>${e.householdCount}</td></tr>`).join("")}
      </tbody>
    `;
  } catch (err) {
    handleFetchError(err, "renderApplianceAnalytics");
  }
}

/* ---------------- Benchmark analytics ---------------- */

async function renderBenchmarkAnalytics() {
  try {
    const c = await fetchJson("/admin/summary");
    const total = c.totalAssessments;

    if (!total) {
      document.getElementById("benchmarkCards").innerHTML = "";
      document.getElementById("benchmarkAverages").innerHTML = `<p class="field-hint">No assessments found.</p>`;
      return;
    }

    const cardsData = [
      { key: "below", count: c.householdsBelowReference, label: "Below Reference" },
      { key: "within", count: c.householdsWithinReference, label: "Within Reference Range" },
      { key: "above", count: c.householdsAboveReference, label: "Above Reference" }
    ];
    const classFor = { below: "within", within: "within", above: "significant" }; // reuse existing card color classes

    document.getElementById("benchmarkCards").innerHTML = cardsData.map(c2 => {
      const pct = Math.round((c2.count / total) * 100);
      return `
      <div class="card benchmark-card ${classFor[c2.key]}">
        <div class="num">${pct}%</div>
        <div class="count">${c2.count} households</div>
        <div class="pct">${c2.label}</div>
      </div>`;
    }).join("");

    const avgDiff = +(c.averageMonthlyConsumption - c.averageReferenceConsumption).toFixed(1);
    document.getElementById("benchmarkAverages").innerHTML = `
      <div class="row"><span>Average Actual Consumption</span><b>${c.averageMonthlyConsumption} kWh/month</b></div>
      <div class="row"><span>Average Reference</span><b>${c.averageReferenceConsumption} kWh/month</b></div>
      <div class="row"><span>Average Difference</span><b>${avgDiff >= 0 ? "+" : ""}${avgDiff} kWh/month</b></div>
    `;
  } catch (err) {
    handleFetchError(err, "renderBenchmarkAnalytics");
  }
}

/* ---------------- Reports ---------------- */

async function exportCSV() {
  try {
    const households = await fetchJson("/admin/households");
    const header = ["Assessment ID", "People", "House Type", "Monthly kWh", "Reference kWh", "Difference", "Status"];
    const rows = households.map(h => [
      h.assessmentId,
      h.householdSize,
      h.houseType || "",
      h.totalMonthlyKwh,
      h.referenceMonthlyKwh,
      h.differenceKwh,
      STATUS_LABELS[h.status] || h.status
    ]);
    const csv = [header, ...rows].map(r => r.map(v => `"${v}"`).join(",")).join("\n");
    downloadTextFile(csv, "urjacheck-households.csv", "text/csv");
    showAdminToast("CSV exported.");
  } catch (err) {
    handleFetchError(err, "exportCSV");
  }
}

async function generateCommunityReport() {
  try {
    const c = await fetchJson("/admin/summary");
    const lines = [
      "UrjaCheck — Community Energy Report",
      "Generated: " + new Date().toLocaleString(),
      "",
      `Households assessed: ${c.totalAssessments}`,
      `Average monthly consumption: ${c.averageMonthlyConsumption} kWh`,
      `Average reference consumption: ${c.averageReferenceConsumption} kWh`,
      `Average estimated cost: ₹${c.averageEstimatedCost}`,
      `Households above reference: ${c.householdsAboveReference}`,
      `Households within reference range: ${c.householdsWithinReference}`,
      `Households below reference: ${c.householdsBelowReference}`,
      "",
      "Reference levels are project-defined or BEE-sourced benchmarks used for awareness comparison, not compliance limits.",
      "This report reflects live data stored in the UrjaCheck database at the time of generation."
    ].join("\n");
    downloadTextFile(lines, "urjacheck-community-report.txt", "text/plain");
    showAdminToast("Community report generated.");
  } catch (err) {
    handleFetchError(err, "generateCommunityReport");
  }
}

function downloadTextFile(content, filename, mime) {
  const blob = new Blob([content], { type: mime });
  const url = URL.createObjectURL(blob);
  const link = document.createElement("a");
  link.href = url;
  link.download = filename;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  URL.revokeObjectURL(url);
}

let adminToastTimer = null;
function showAdminToast(msg) {
  const toast = document.getElementById("toast");
  toast.textContent = msg;
  toast.classList.add("show");
  clearTimeout(adminToastTimer);
  adminToastTimer = setTimeout(() => toast.classList.remove("show"), 3600);
}
