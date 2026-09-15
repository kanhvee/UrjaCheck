/* ============================================================
   UrjaCheck — app.js
   Resident-side application state, view routing, rendering.

   INTEGRATION NOTE: the assessment is now calculated and stored by
   the Spring Boot backend. This file only:
     1. collects form state,
     2. shows an instant *local preview* on the Review screen
        (calculations.js, clearly a preview — see submitAssessment),
     3. POSTs to {API_BASE_URL}/assessment on submit,
     4. renders whatever the backend returns using the exact same
        renderResults()/downloadReport() functions as before.
   ============================================================ */

const state = {
  householdSize: 4,
  houseType: null,
  billKnown: null,
  actualBill: null,
  appliances: [], // { id, applianceType, quantity, powerWatts, operatingHours, unknownPower }
  lastAssessment: null
};

let applianceIdCounter = 1;

/* ---------------- View routing ---------------- */

function showView(name) {
  document.querySelectorAll(".view").forEach(v => v.classList.remove("active"));
  const target = document.getElementById("view-" + name);
  if (target) target.classList.add("active");
  document.querySelectorAll("#mainNav button").forEach(b => b.classList.remove("active"));
  const navBtn = document.querySelector('#mainNav button[data-nav="' + (name === "landing" ? "landing" : "intro") + '"]');
  if (navBtn) navBtn.classList.add("active");
  window.scrollTo({ top: 0, behavior: "instant" in window ? "instant" : "auto" });
  closeMobileNav();
}

function startAssessment() {
  showView("intro");
}

function restartAssessment() {
  state.householdSize = 4;
  state.houseType = null;
  state.billKnown = null;
  state.actualBill = null;
  state.appliances = [];
  document.getElementById("householdSize").value = 4;
  document.getElementById("billAmount").value = "";
  document.getElementById("field-billamount").style.display = "none";
  document.getElementById("consentCheckbox").checked = false;
  renderHouseTypeGrid();
  renderBillKnownButtons();
  renderApplianceCards();
  showView("landing");
}

/* mobile nav toggle */
document.addEventListener("DOMContentLoaded", () => {
  const toggle = document.getElementById("navToggle");
  if (toggle) {
    toggle.addEventListener("click", () => {
      const nav = document.getElementById("mainNav");
      const isOpen = nav.classList.toggle("mobile-open");
      toggle.setAttribute("aria-expanded", isOpen ? "true" : "false");
    });
  }
  renderHouseTypeGrid();
  renderApplianceCards();
});

function closeMobileNav() {
  const nav = document.getElementById("mainNav");
  const toggle = document.getElementById("navToggle");
  if (nav) nav.classList.remove("mobile-open");
  if (toggle) toggle.setAttribute("aria-expanded", "false");
}

/* ---------------- Household step ---------------- */

function adjustHouseholdSize(delta) {
  const input = document.getElementById("householdSize");
  let val = parseInt(input.value, 10) + delta;
  val = Math.min(20, Math.max(1, val));
  input.value = val;
  state.householdSize = val;
}

function renderHouseTypeGrid() {
  const grid = document.getElementById("houseTypeGrid");
  grid.innerHTML = HOUSE_TYPES.map(t =>
    `<button type="button" class="option-card${state.houseType === t ? " selected" : ""}" role="radio" aria-checked="${state.houseType === t}" onclick="setHouseType('${t}')">${t}</button>`
  ).join("");
}

function setHouseType(type) {
  state.houseType = type;
  renderHouseTypeGrid();
  document.getElementById("field-housetype").classList.remove("has-error");
}

function renderBillKnownButtons() {
  document.querySelectorAll('#field-billknown .option-card').forEach(btn => {
    btn.classList.toggle("selected", btn.dataset.value === state.billKnown);
  });
}

function setBillKnown(value) {
  state.billKnown = value;
  renderBillKnownButtons();
  document.getElementById("field-billamount").style.display = value === "yes" ? "block" : "none";
}

function validateHousehold() {
  let valid = true;

  document.getElementById("field-size").classList.remove("has-error");
  document.getElementById("field-housetype").classList.remove("has-error");
  document.getElementById("field-billamount").classList.remove("has-error");

  state.householdSize = parseInt(document.getElementById("householdSize").value, 10);
  if (!state.householdSize || state.householdSize < 1 || state.householdSize > 20) {
    document.getElementById("field-size").classList.add("has-error");
    valid = false;
  }

  if (!state.houseType) {
    document.getElementById("field-housetype").classList.add("has-error");
    valid = false;
  }

  if (state.billKnown === "yes") {
    const amount = parseFloat(document.getElementById("billAmount").value);
    if (!amount || amount <= 0) {
      document.getElementById("field-billamount").classList.add("has-error");
      valid = false;
    } else {
      state.actualBill = amount;
    }
  } else {
    state.actualBill = null;
  }

  if (valid) showView("appliances");
}

/* ---------------- Appliance step ---------------- */

function openApplianceModal() {
  const list = document.getElementById("applianceOptionList");
  list.innerHTML = APPLIANCE_NAMES.map(name => {
    const cat = APPLIANCE_CATALOG[name];
    return `<button type="button" onclick="addAppliance('${name}')">
      <span class="icon">${cat.icon}</span> ${name}
      <span class="power-tag">${cat.defaultPower} W typical</span>
    </button>`;
  }).join("");
  document.getElementById("applianceModal").classList.add("open");
}

function closeApplianceModal() {
  document.getElementById("applianceModal").classList.remove("open");
}

function addAppliance(name) {
  const cat = APPLIANCE_CATALOG[name];
  state.appliances.push({
    id: applianceIdCounter++,
    applianceType: name,
    quantity: 1,
    powerWatts: cat.defaultPower,
    operatingHours: cat.referenceHours,
    unknownPower: false
  });
  closeApplianceModal();
  renderApplianceCards();
  document.getElementById("applianceListError").style.display = "none";
}

function removeAppliance(id) {
  state.appliances = state.appliances.filter(a => a.id !== id);
  renderApplianceCards();
}


function updateApplianceField(id, field, value) {
  const item = state.appliances.find(a => a.id === id);
  if (!item) return;

  if (field === "quantity") item.quantity = parseInt(value, 10) || 0;
  if (field === "powerWatts") item.powerWatts = parseFloat(value) || 0;
  if (field === "operatingHours") item.operatingHours = parseFloat(value) || 0;
}



function applianceValidation(item) {
  const errors = [];
  if (!item.quantity || item.quantity < 1) errors.push("Quantity must be at least 1.");
  if (!item.powerWatts || item.powerWatts <= 0) errors.push("Power rating must be greater than 0.");
  if (item.operatingHours < 0 || item.operatingHours > 24) errors.push("Operating hours must be between 0 and 24 hours.");
  return errors;
}

function renderApplianceCards() {
  const container = document.getElementById("applianceList");

  if (state.appliances.length === 0) {
    container.innerHTML = `<div class="empty-appliances card-flat">
      <span class="icon">🔌</span>
      No appliances added yet. Tap "Add Appliance" below to get started.
    </div>`;
    return;
  }

  container.innerHTML = state.appliances.map(item => {
    const cat = APPLIANCE_CATALOG[item.applianceType];
    const errors = applianceValidation(item);
    const daily = calculateDailyEnergy(item.quantity, item.powerWatts, item.operatingHours);
    const monthly = calculateMonthlyEnergy(daily);

    return `
    <div class="appliance-card${errors.length ? " has-error" : ""}">
      <div class="appliance-card-head">
        <div class="appliance-card-title"><span class="icon">${cat.icon}</span> ${item.applianceType}</div>
        <button type="button" class="remove-btn" onclick="removeAppliance(${item.id})">Remove</button>
      </div>
      <div class="appliance-grid">
        <div class="mini-field">
          <label for="qty-${item.id}">Quantity</label>
          <input type="number" id="qty-${item.id}" class="unit-input-plain text-input" min="1" value="${item.quantity}"
            oninput="updateApplianceField(${item.id}, 'quantity', this.value)">
        </div>
        <div class="mini-field">
          <label for="pow-${item.id}">Power rating</label>
          <div class="unit-input">

<input type="number" id="pow-${item.id}" min="1" value="${item.powerWatts}" readonly>

            <span>W</span>
          </div>
        </div>
        <div class="mini-field">
          <label for="hrs-${item.id}">Usage per day</label>
          <div class="unit-input">
            <input type="number" id="hrs-${item.id}" min="0" max="24" step="0.5" value="${item.operatingHours}"
              oninput="updateApplianceField(${item.id}, 'operatingHours', this.value)">
            <span>hrs</span>
          </div>
        </div>
      </div>
      <p class="power-info-hint">ⓘ Typical power rating: ${cat.defaultPower} W. You can change this if you know the appliance's actual rating.</p>



      <div class="unknown-power-note${item.unknownPower ? " show" : ""}">We'll use a typical reference value for this appliance.</div>
      ${errors.length ? `<p class="field-error" style="display:block;">${errors[0]}</p>` : ""}
      <div class="live-calc">
        <span>Daily: <b>${daily.toFixed(2)} kWh</b></span>
        <span>Monthly: <b>${monthly.toFixed(1)} kWh</b></span>
      </div>
    </div>`;
  }).join("");
}

function validateAppliancesAndContinue() {
  if (state.appliances.length === 0) {
    document.getElementById("applianceListError").style.display = "block";
    return;
  }
  const anyInvalid = state.appliances.some(item => applianceValidation(item).length > 0);
  if (anyInvalid) {
    showToast("Please fix the highlighted appliance details before continuing.");
    return;
  }
  document.getElementById("applianceListError").style.display = "none";
  renderReview();
  showView("review");
}

/* ---------------- Review step (local preview only) ---------------- */

// This builds a client-side ESTIMATE for the Review screen so the resident
// sees numbers immediately. The authoritative numbers come back from
// POST /api/assessment in submitAssessment() below.
function computePreview() {
  const appliances = state.appliances.map(item => {
    const cat = APPLIANCE_CATALOG[item.applianceType];
    const dailyEnergyKWh = calculateDailyEnergy(item.quantity, item.powerWatts, item.operatingHours);
    const monthlyEnergyKWh = calculateMonthlyEnergy(dailyEnergyKWh);
    const referenceHours = cat.referenceHours;
    const referenceEnergyKWh = calculateReferenceEnergy(item.quantity, item.powerWatts, referenceHours);
    return {
      applianceType: item.applianceType,
      quantity: item.quantity,
      powerWatts: item.powerWatts,
      operatingHours: item.operatingHours,
      dailyEnergyKWh,
      monthlyEnergyKWh,
      referenceHours,
      referenceEnergyKWh
    };
  });

  const totalDailyEnergyKWh = +appliances.reduce((s, a) => s + a.dailyEnergyKWh, 0).toFixed(2);
  const totalMonthlyEnergyKWh = +appliances.reduce((s, a) => s + a.monthlyEnergyKWh, 0).toFixed(1);
  const totalReferenceEnergyKWh = +appliances.reduce((s, a) => s + a.referenceEnergyKWh, 0).toFixed(1);
  const estimatedMonthlyCost = calculateMonthlyCost(totalMonthlyEnergyKWh, DEFAULT_TARIFF);

  return {
    householdSize: state.householdSize,
    houseType: state.houseType,
    actualBill: state.actualBill,
    appliances,
    totalDailyEnergyKWh,
    totalMonthlyEnergyKWh,
    totalReferenceEnergyKWh,
    estimatedMonthlyCost
  };
}

function renderReview() {
  const preview = computePreview();

  document.getElementById("reviewHousehold").innerHTML = `
    <div class="review-row"><span>Household size</span><span>${preview.householdSize} people</span></div>
    <div class="review-row"><span>Home type</span><span>${preview.houseType}</span></div>
    ${preview.actualBill ? `<div class="review-row"><span>Reported monthly bill</span><span>₹${preview.actualBill}</span></div>` : ""}
  `;

  document.getElementById("reviewAppliances").innerHTML = preview.appliances.map(item =>
    `<div class="review-row"><span>${item.quantity} × ${item.applianceType}</span><span class="qty">${item.operatingHours} h/day</span></div>`
  ).join("");

  document.getElementById("reviewSummary").innerHTML = `
    <div class="stat"><div class="num">${preview.totalDailyEnergyKWh}</div><div class="lbl">kWh/day (est.)</div></div>
    <div class="stat"><div class="num">${preview.totalMonthlyEnergyKWh}</div><div class="lbl">kWh/month (est.)</div></div>
    <div class="stat"><div class="num">₹${preview.estimatedMonthlyCost.toLocaleString("en-IN")}</div><div class="lbl">est. monthly cost</div></div>
  `;

  document.getElementById("consentCheckbox").checked = false;
  toggleSubmitEnabled();
}

function toggleSubmitEnabled() {
  document.getElementById("submitBtn").disabled = !document.getElementById("consentCheckbox").checked;
}

/* ---------------- API payload ---------------- */

// Frontend field -> API field mapping:
//   applianceType -> applianceName   quantity -> quantity
//   powerWatts    -> powerWatts      operatingHours -> hoursPerDay
//   (APPLIANCE_CATALOG category)     -> category
function buildAssessmentRequestPayload() {
  return {
    householdSize: state.householdSize,
    houseType: state.houseType,
    actualBill: state.actualBill,
    appliances: state.appliances.map(item => ({
      applianceName: item.applianceType,
      category: APPLIANCE_CATALOG[item.applianceType]?.category || "Other",
      quantity: item.quantity,
      powerWatts: item.powerWatts,
      hoursPerDay: item.operatingHours
    }))
  };
}

/* ---------------- Submit / Processing / Result ---------------- */

function submitAssessment() {
  if (!document.getElementById("consentCheckbox").checked) return;
  showView("processing");

  const animationDone = runProcessingAnimation();
  const submissionDone = fetch(API_BASE_URL + "/assessment", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(buildAssessmentRequestPayload())
  }).then(async res => {
    if (!res.ok) {
      const errorBody = await res.json().catch(() => ({}));
      throw new Error(errorBody.message || ("Request failed with status " + res.status));
    }
    return res.json();
  });

  Promise.all([animationDone, submissionDone])
    .then(([, apiResponse]) => {
      state.lastAssessment = mapApiResponseToLocalAssessment(apiResponse);
      renderResults(state.lastAssessment);
      showView("result");
    })
    .catch(err => {
      console.error("Assessment submission failed", err);
      showToast(err.message && err.message !== "Failed to fetch"
        ? err.message
        : "Could not reach the UrjaCheck server. Is the backend running on " + API_BASE_URL + "?");
      showView("review");
    });
}

function runProcessingAnimation() {
  return new Promise(resolve => {
    const items = document.querySelectorAll("#processingChecklist li");
    items.forEach(li => li.classList.remove("done"));
    let i = 0;
    const interval = setInterval(() => {
      if (items[i]) items[i].classList.add("done");
      i++;
      if (i >= items.length) {
        clearInterval(interval);
        setTimeout(resolve, 300);
      }
    }, 350);
  });
}

/**
 * Converts the backend's AssessmentResponse JSON shape into the shape the
 * existing renderResults()/downloadReport() functions expect, so the
 * original result-rendering code did not need to change.
 *
 * Backend field         -> Local field
 *   assessmentId         -> assessmentId
 *   summary.monthlyKwh   -> totalMonthlyEnergyKWh
 *   summary.referenceMonthlyKwh -> totalReferenceEnergyKWh
 *   summary.status       -> status (BELOW_REFERENCE / WITHIN_REFERENCE_RANGE / ABOVE_REFERENCE)
 *   appliances[].applianceName -> appliances[].applianceType
 *   appliances[].hoursPerDay   -> appliances[].operatingHours
 *   appliances[].monthlyKwh    -> appliances[].monthlyEnergyKWh
 */
function mapApiResponseToLocalAssessment(apiResponse) {
  const s = apiResponse.summary;
  return {
    assessmentId: apiResponse.assessmentId,
    householdSize: apiResponse.householdSize,
    houseType: apiResponse.houseType,
    actualBill: s.actualBill,
    tariff: s.tariff,
    timestamp: apiResponse.createdAt,
    appliances: apiResponse.appliances.map(a => ({
      applianceType: a.applianceName,
      category: a.category,
      quantity: a.quantity,
      powerWatts: a.powerWatts,
      operatingHours: a.hoursPerDay,
      dailyEnergyKWh: a.dailyKwh,
      monthlyEnergyKWh: a.monthlyKwh,
      referenceHours: a.referenceHoursPerDay,
      referenceEnergyKWh: a.referenceMonthlyKwh,
      status: a.status,
      recommendation: a.recommendation,
      referenceSource: a.referenceSource
    })),
    totalDailyEnergyKWh: s.dailyKwh,
    totalMonthlyEnergyKWh: s.monthlyKwh,
    totalReferenceEnergyKWh: s.referenceMonthlyKwh,
    estimatedMonthlyCost: s.estimatedMonthlyCost,
    percentageDifference: s.differencePercentage,
    status: s.status
  };
}

function renderResults(a) {
  const status = a.status || getAssessmentStatus(a.totalMonthlyEnergyKWh, a.totalReferenceEnergyKWh);
  const maxVal = Math.max(a.totalMonthlyEnergyKWh, a.totalReferenceEnergyKWh, 1);

  document.getElementById("resYourUsage").textContent = a.totalMonthlyEnergyKWh + " kWh/month";
  document.getElementById("resRefUsage").textContent = a.totalReferenceEnergyKWh + " kWh/month";
  document.getElementById("resYourUsageBarLabel").textContent = a.totalMonthlyEnergyKWh + " kWh";
  document.getElementById("resRefUsageBarLabel").textContent = a.totalReferenceEnergyKWh + " kWh";
  document.getElementById("resYourBar").style.width = (a.totalMonthlyEnergyKWh / maxVal * 100) + "%";
  document.getElementById("resRefBar").style.width = (a.totalReferenceEnergyKWh / maxVal * 100) + "%";

  document.getElementById("resStatusLine").innerHTML =
    `<span class="status-line ${STATUS_CLASSES[status]}">${STATUS_LABELS[status]}</span>`;

  const diff = +(a.totalMonthlyEnergyKWh - a.totalReferenceEnergyKWh).toFixed(1);
  document.getElementById("resDiffText").textContent =
    (diff >= 0 ? "+" : "") + diff + " kWh/month (" + (a.percentageDifference >= 0 ? "+" : "") + a.percentageDifference + "% vs reference)";

  document.getElementById("resCost").textContent = "₹" + a.estimatedMonthlyCost.toLocaleString("en-IN") + " / month";

  // Reference-source transparency line, driven entirely by what the
  // backend reports — never assume/claim a government source ourselves.
  const sourceLine = document.getElementById("resSourceLine");
  if (sourceLine) {
    const types = new Set((a.appliances || []).map(x => x.referenceSource && x.referenceSource.referenceType).filter(Boolean));
    let text;
    if (types.size === 0) {
      text = "Reference level: project reference assumption.";
    } else if (types.size === 1) {
      text = "Reference level: " + (REFERENCE_TYPE_LABELS[[...types][0]] || [...types][0]) + ".";
    } else {
      text = "Reference level: a mix of " + [...types].map(t => REFERENCE_TYPE_LABELS[t] || t).join(" and ") + ".";
    }
    sourceLine.textContent = text + " This is a benchmark comparison for awareness, not a compliance test — actual needs vary by household.";
  }

  const costNoteEl = document.getElementById("resCostNote");
  if (costNoteEl) {
    costNoteEl.textContent = "Based on the illustrative tariff assumption used by the server (₹" + (a.tariff || DEFAULT_TARIFF) +
      "/kWh). Actual bills may include slab-based tariffs, fixed charges and taxes.";
  }

  const billCompareEl = document.getElementById("resBillCompare");
  if (a.actualBill) {
    const billDiff = Math.round(a.actualBill - a.estimatedMonthlyCost);
    billCompareEl.style.display = "flex";
    billCompareEl.innerHTML = `
      <div class="row"><span>Estimated</span><b>₹${a.estimatedMonthlyCost.toLocaleString("en-IN")}</b></div>
      <div class="row"><span>Reported bill</span><b>₹${a.actualBill.toLocaleString("en-IN")}</b></div>
      <div class="row"><span>Difference</span><b>${billDiff >= 0 ? "+" : ""}₹${Math.abs(billDiff).toLocaleString("en-IN")}</b></div>
    `;
  } else {
    billCompareEl.style.display = "none";
  }

  const sorted = [...a.appliances].sort((x, y) => y.monthlyEnergyKWh - x.monthlyEnergyKWh);
  const highest = sorted[0] ? sorted[0].applianceType : "—";

  document.getElementById("resSummaryList").innerHTML = `
    <div class="row"><span>Total appliances</span><b>${a.appliances.length}</b></div>
    <div class="row"><span>Daily consumption</span><b>${a.totalDailyEnergyKWh} kWh</b></div>
    <div class="row"><span>Monthly consumption</span><b>${a.totalMonthlyEnergyKWh} kWh</b></div>
    <div class="row"><span>Reference level</span><b>${a.totalReferenceEnergyKWh} kWh</b></div>
    <div class="row"><span>Highest consumer</span><b>${highest}</b></div>
    <div class="row"><span>Difference from reference</span><b>${(a.percentageDifference >= 0 ? "+" : "") + a.percentageDifference}%</b></div>
  `;

  const totalMonthly = a.totalMonthlyEnergyKWh || 1;
  document.getElementById("resBreakdown").innerHTML = sorted.map(item => {
    const pct = ((item.monthlyEnergyKWh / totalMonthly) * 100).toFixed(1);
    return `
    <div class="breakdown-row">
      <div class="breakdown-label"><span>${(APPLIANCE_CATALOG[item.applianceType] || {}).icon || "🔌"} ${item.applianceType}</span><span class="pct">${item.monthlyEnergyKWh} kWh · ${pct}%</span></div>
      <div class="breakdown-track"><div class="breakdown-fill" style="width:${pct}%"></div></div>
    </div>`;
  }).join("") || `<p class="field-hint">No appliance data available.</p>`;

  // Prefer the backend's per-appliance recommendations (RecommendationService.java).
  // Fall back to the local generator only if the backend didn't supply any
  // (e.g. an older cached response).
  const hasBackendRecs = a.appliances.some(x => x.recommendation);
  let recCards;
  if (hasBackendRecs) {
    recCards = sorted
      .filter(x => x.recommendation && !/^No specific recommendation/i.test(x.recommendation))
      .slice(0, 3)
      .map(x => ({
        icon: (APPLIANCE_CATALOG[x.applianceType] || {}).icon || "🔌",
        title: x.applianceType,
        body: x.recommendation
      }));
    if (recCards.length === 0) {
      recCards = [{ icon: "🌱", title: "You're doing well", body: "Your appliances are all within or below their reference levels — no specific suggestions right now." }];
    }
  } else {
    recCards = generateRecommendations(a.appliances);
  }

  document.getElementById("resRecommendations").innerHTML = recCards.map(r => `
    <div class="card rec-card">
      <span class="icon">${r.icon}</span>
      <div><h4>${r.title}</h4><p>${r.body}</p></div>
    </div>
  `).join("");
}

function downloadReport() {
  const a = state.lastAssessment;
  if (!a) return;

  const generated = a.timestamp
    ? new Date(a.timestamp).toLocaleString()
    : new Date().toLocaleString();

  const statusLabel = STATUS_LABELS[a.status] || a.status || "Not available";

  const applianceRows = (a.appliances || []).map(i => `
    <tr>
      <td>${i.quantity}</td>
      <td>${i.applianceType}</td>
      <td>${i.operatingHours} h/day</td>
      <td>${Number(i.monthlyEnergyKWh || 0).toFixed(1)} kWh</td>
    </tr>
  `).join("");

  const percentage = Number(a.percentageDifference || 0);

  const html = `
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>UrjaCheck Energy Assessment</title>

  <style>
    body {
      font-family: Arial, sans-serif;
      margin: 0;
      padding: 40px;
      color: #1f2937;
      background: #f3f4f6;
    }

    .report {
      max-width: 900px;
      margin: auto;
      background: white;
      padding: 40px;
      border-radius: 12px;
    }

    h1 {
      margin-bottom: 5px;
    }

    h2 {
      margin-top: 32px;
      border-bottom: 2px solid #e5e7eb;
      padding-bottom: 8px;
    }

    .subtitle {
      color: #6b7280;
      margin-bottom: 30px;
    }

    .summary {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 15px;
      margin: 20px 0;
    }

    .card {
      border: 1px solid #e5e7eb;
      border-radius: 8px;
      padding: 18px;
    }

    .label {
      font-size: 13px;
      color: #6b7280;
    }

    .value {
      font-size: 22px;
      font-weight: bold;
      margin-top: 5px;
    }

    .comparison {
      margin-top: 20px;
    }

    .bar-label {
      display: flex;
      justify-content: space-between;
      margin-bottom: 6px;
    }

    .bar {
      height: 18px;
      background: #e5e7eb;
      border-radius: 10px;
      overflow: hidden;
      margin-bottom: 15px;
    }

    .actual-bar {
      height: 100%;
      width: ${Math.min(
        100,
        Math.max(
          5,
          (Number(a.totalMonthlyEnergyKWh || 0) /
            Math.max(Number(a.totalReferenceEnergyKWh || 1), 1)) * 100
        )
      )}%;
      background: #2563eb;
    }

    table {
      width: 100%;
      border-collapse: collapse;
      margin-top: 15px;
    }

    th, td {
      padding: 10px;
      border-bottom: 1px solid #e5e7eb;
      text-align: left;
    }

    th {
      background: #f9fafb;
    }

    .status {
      display: inline-block;
      padding: 8px 14px;
      border-radius: 20px;
      font-weight: bold;
      background: #f3f4f6;
    }

    .note {
      margin-top: 30px;
      padding: 16px;
      background: #f9fafb;
      border-left: 4px solid #6b7280;
    }

    .print-button {
      padding: 12px 20px;
      border: none;
      border-radius: 6px;
      cursor: pointer;
      background: #111827;
      color: white;
      font-size: 15px;
      margin-bottom: 25px;
    }

    @media print {
      body {
        background: white;
        padding: 0;
      }

      .report {
        max-width: none;
        padding: 20px;
      }

      .print-button {
        display: none;
      }
    }

    @media (max-width: 600px) {
      body {
        padding: 15px;
      }

      .report {
        padding: 20px;
      }

      .summary {
        grid-template-columns: 1fr;
      }
    }
  </style>
</head>

<body>

<div class="report">

  <button class="print-button" onclick="window.print()">
    Print / Save as PDF
  </button>

  <h1>UrjaCheck</h1>
  <div class="subtitle">Household Energy Assessment Report</div>

  <p>
    <strong>Assessment ID:</strong> ${a.assessmentId}
  </p>

  <p>
    <strong>Generated:</strong> ${generated}
  </p>

  <h2>Household Information</h2>

  <div class="summary">
    <div class="card">
      <div class="label">Household Size</div>
      <div class="value">${a.householdSize}</div>
      <div>people</div>
    </div>

    <div class="card">
      <div class="label">Home Type</div>
      <div class="value">${a.houseType}</div>
    </div>

    ${
      a.actualBill
        ? `
        <div class="card">
          <div class="label">Reported Electricity Bill</div>
          <div class="value">₹${a.actualBill}</div>
        </div>
        `
        : ""
    }
  </div>

  <h2>Energy Consumption</h2>

  <div class="summary">

    <div class="card">
      <div class="label">Estimated Daily Consumption</div>
      <div class="value">
        ${Number(a.totalDailyEnergyKWh || 0).toFixed(2)} kWh
      </div>
    </div>

    <div class="card">
      <div class="label">Estimated Monthly Consumption</div>
      <div class="value">
        ${Number(a.totalMonthlyEnergyKWh || 0).toFixed(1)} kWh
      </div>
    </div>

    <div class="card">
      <div class="label">Reference Monthly Consumption</div>
      <div class="value">
        ${Number(a.totalReferenceEnergyKWh || 0).toFixed(1)} kWh
      </div>
    </div>

    <div class="card">
      <div class="label">Estimated Monthly Cost</div>
      <div class="value">
        ₹${Number(a.estimatedMonthlyCost || 0).toFixed(0)}
      </div>

      <div>
        Illustrative tariff:
        ₹${a.tariff || DEFAULT_TARIFF}/kWh
      </div>
    </div>

  </div>

  <h2>Reference Comparison</h2>

  <div class="comparison">

    <div class="bar-label">
      <span>Actual consumption</span>
      <strong>
        ${Number(a.totalMonthlyEnergyKWh || 0).toFixed(1)} kWh/month
      </strong>
    </div>

    <div class="bar">
      <div class="actual-bar"></div>
    </div>

    <div class="bar-label">
      <span>Reference level</span>
      <strong>
        ${Number(a.totalReferenceEnergyKWh || 0).toFixed(1)} kWh/month
      </strong>
    </div>

    <p>
      Difference from reference:
      <strong>${percentage.toFixed(1)}%</strong>
    </p>

    <p>
      Assessment status:
      <span class="status">${statusLabel}</span>
    </p>

  </div>

  <h2>Appliance-wise Consumption</h2>

  <table>
    <thead>
      <tr>
        <th>Quantity</th>
        <th>Appliance</th>
        <th>Operating Hours</th>
        <th>Monthly Consumption</th>
      </tr>
    </thead>

    <tbody>
      ${applianceRows || `
        <tr>
          <td colspan="4">No appliance data available.</td>
        </tr>
      `}
    </tbody>
  </table>

  <h2>Methodology</h2>

  <div class="note">

    <p>
      This assessment estimates household electricity consumption using
      appliance quantity, typical power estimates and reported operating
      hours.
    </p>

    <p>
      Reference values are used as benchmarks for awareness and comparison.
      Where applicable, UrjaCheck uses parameters published by the
      Bureau of Energy Efficiency (BEE). These parameters should not be
      interpreted as a universal "ideal household usage" or as a
      compliance limit.
    </p>

    <p>
      For appliances where a directly comparable government benchmark is
      not available, a project reference value is used.
    </p>

    <p>
      Electricity cost is illustrative and depends on the applicable
      electricity tariff and billing structure.
    </p>

  </div>

  <h2>Conclusion</h2>

  <p>
    This report is intended to help households understand their electricity
    consumption, identify major energy-consuming appliances and compare
    their estimated usage with a reference benchmark.
  </p>

  <p>
    <strong>UrjaCheck is intended for awareness and educational purposes
    and is not a regulatory or compliance assessment.</strong>
  </p>

</div>

</body>
</html>
  `;

  const blob = new Blob([html], { type: "text/html" });
  const url = URL.createObjectURL(blob);

  const link = document.createElement("a");
  link.href = url;
  link.download = `UrjaCheck-Assessment-${a.assessmentId}.html`;

  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);

  URL.revokeObjectURL(url);

  showToast("Assessment report downloaded.");
}

/* ---------------- Toast ---------------- */

let toastTimer = null;
function showToast(msg) {
  const toast = document.getElementById("toast");
  toast.textContent = msg;
  toast.classList.add("show");
  clearTimeout(toastTimer);
  toastTimer = setTimeout(() => toast.classList.remove("show"), 3600);
}
