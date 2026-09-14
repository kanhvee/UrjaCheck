/* ============================================================
   UrjaCheck — calculations.js

   The backend (EnergyCalculationUtil.java) is now the authoritative
   source of truth for every number shown on the Result screen.
   The functions below are used ONLY for the local, client-side
   preview on the Review screen (before the assessment is submitted),
   so the resident sees an instant estimate while the real numbers
   are computed server-side a moment later.

   The status thresholds mirror the backend exactly (+/-10% band)
   so the preview never looks different from the final result.
   ============================================================ */

function calculateDailyEnergy(quantity, powerWatts, operatingHours) {
  return +((quantity * powerWatts * operatingHours) / 1000).toFixed(3);
}

function calculateMonthlyEnergy(dailyEnergyKWh) {
  return +(dailyEnergyKWh * 30).toFixed(2);
}

function calculateMonthlyCost(monthlyEnergyKWh, tariff = DEFAULT_TARIFF) {
  return +(monthlyEnergyKWh * tariff).toFixed(2);
}

function calculateReferenceEnergy(quantity, powerWatts, referenceHours) {
  return +((quantity * powerWatts * referenceHours * 30) / 1000).toFixed(2);
}

function calculatePercentageDifference(actual, reference) {
  if (!reference) return 0;
  return ((actual - reference) / reference) * 100;
}

// Matches EnergyCalculationUtil.classify() on the backend:
// BELOW_REFERENCE / WITHIN_REFERENCE_RANGE / ABOVE_REFERENCE, using a
// +/-10% "reasonably close" band. This is a benchmark comparison for
// awareness, not a judgment — consumption varies with household size,
// appliances, climate and habits.
function getAssessmentStatus(actualMonthlyKWh, referenceMonthlyKWh) {
  if (!referenceMonthlyKWh) return "WITHIN_REFERENCE_RANGE";
  const lower = referenceMonthlyKWh * 0.9;
  const upper = referenceMonthlyKWh * 1.1;
  if (actualMonthlyKWh < lower) return "BELOW_REFERENCE";
  if (actualMonthlyKWh > upper) return "ABOVE_REFERENCE";
  return "WITHIN_REFERENCE_RANGE";
}

const STATUS_LABELS = {
  BELOW_REFERENCE: "Below Reference Level",
  WITHIN_REFERENCE_RANGE: "Within Reference Range",
  ABOVE_REFERENCE: "Above Reference Level"
};

const STATUS_CLASSES = {
  BELOW_REFERENCE: "status-good",
  WITHIN_REFERENCE_RANGE: "status-good",
  ABOVE_REFERENCE: "status-warning"
};

// Human-readable labels for the reference_type values returned by the
// backend's ReferenceSourceDto, so the UI never claims something is an
// official standard unless the backend actually says so.
const REFERENCE_TYPE_LABELS = {
  GOVERNMENT_MANDATED: "Government-mandated standard",
  BEE_BENCHMARK: "Bureau of Energy Efficiency (BEE) benchmark",
  STAR_LABEL_INFO: "BEE efficiency information (general)",
  PROJECT_REFERENCE: "Project reference assumption"
};

/**
 * Local, pre-submission fallback recommendations shown only if the
 * network request to the backend fails outright. Once a real response
 * comes back, the backend's per-appliance `recommendation` field
 * (RecommendationService.java) is used instead — see app.js.
 */
function generateRecommendations(appliances) {
  if (!appliances.length) return [];
  const sorted = [...appliances].sort((a, b) => b.monthlyEnergyKWh - a.monthlyEnergyKWh);
  const top = sorted.slice(0, 3);
  const recs = [];

  const messages = {
    "Air Conditioner": { icon: "❄️", title: "Air Conditioner", body: "Your air conditioner is currently a large contributor to household consumption. Reducing unnecessary operating hours, or raising the set temperature slightly, may help lower consumption." },
    "Ceiling Fan": { icon: "🌀", title: "Ceiling Fans", body: "Consider switching off fans when rooms are unoccupied." },
    "Table Fan": { icon: "🌀", title: "Table Fans", body: "Consider switching off fans when rooms are unoccupied." },
    "Lighting Fixtures": { icon: "💡", title: "Lighting", body: "Consider energy-efficient LED lighting and switching off lights when they are not required." },
    "LED Bulb": { icon: "💡", title: "Lighting", body: "Switch off lights when a room is unoccupied." },
    "Tube Light": { icon: "💡", title: "Lighting", body: "Consider replacing older tube lights with LED alternatives." },
    "Water Pump": { icon: "🚿", title: "Water Pump", body: "Avoid unnecessary pumping cycles and check whether the pump runs longer than required." },
    "Refrigerator": { icon: "🧊", title: "Refrigerator", body: "Avoid keeping the door open unnecessarily and ensure adequate ventilation around the appliance." },
    "Water Heater/Geyser": { icon: "🚿", title: "Water Heater", body: "Consider limiting continuous heating time and using a timer if your geyser supports one." },
    "Washing Machine": { icon: "🧺", title: "Washing Machine", body: "Running full loads instead of frequent small loads can reduce overall energy use." }
  };

  for (const item of top) {
    const template = messages[item.applianceType];
    recs.push(template || {
      icon: "🔌",
      title: item.applianceType,
      body: "This appliance is among your higher energy contributors. Check whether its usage hours can be reduced without affecting comfort."
    });
  }
  return recs;
}
