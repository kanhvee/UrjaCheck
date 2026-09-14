/* ============================================================
   UrjaCheck — data.js
   Frontend-only reference data used to populate the assessment
   form (icons, default power ratings, category labels for the
   API payload). The authoritative reference-hours values and all
   calculations now live on the backend (see
   backend/src/main/resources/data.sql and EnergyCalculationUtil) —
   this file no longer generates dummy households; the admin
   dashboard reads real data from /api/admin/*.
   ============================================================ */

// Illustrative tariff assumption (₹ per kWh), shown before submission.
// The backend has the authoritative value; the submitted result always
// reflects whatever tariff the server actually used.
const DEFAULT_TARIFF = 8;

// category mirrors the categories seeded in the backend's reference_standard
// table, so the appliance list and the admin/reference views describe the
// same appliance the same way.
const APPLIANCE_CATALOG = {
  "Lighting Fixtures":   { icon: "💡", defaultPower: 10,   referenceHours: 5,  category: "Lighting" },
  "LED Bulb":            { icon: "💡", defaultPower: 10,   referenceHours: 5,  category: "Lighting" },
  "Tube Light":          { icon: "💡", defaultPower: 20,   referenceHours: 5,  category: "Lighting" },
  "Ceiling Fan":         { icon: "🌀", defaultPower: 75,   referenceHours: 7,  category: "Cooling" },
  "Table Fan":           { icon: "🌀", defaultPower: 50,   referenceHours: 6,  category: "Cooling" },
  "Air Conditioner":     { icon: "❄️", defaultPower: 1500, referenceHours: 4,  category: "Cooling" },
  "Air Cooler":          { icon: "🧊", defaultPower: 200,  referenceHours: 5,  category: "Cooling" },
  "Refrigerator":        { icon: "🧊", defaultPower: 200,  referenceHours: 10, category: "Kitchen" },
  "Television":          { icon: "📺", defaultPower: 100,  referenceHours: 3,  category: "Entertainment" },
  "Computer/Laptop":     { icon: "💻", defaultPower: 100,  referenceHours: 5,  category: "Electronics" },
  "Washing Machine":     { icon: "🧺", defaultPower: 500,  referenceHours: 1,  category: "Laundry" },
  "Iron":                { icon: "🔥", defaultPower: 1000, referenceHours: 0.5, category: "Laundry" },
  "Water Heater/Geyser": { icon: "🚿", defaultPower: 2000, referenceHours: 1,  category: "Water Heating" },
  "Microwave":           { icon: "🍽️", defaultPower: 1200, referenceHours: 0.5, category: "Kitchen" },
  "Induction Cooktop":   { icon: "🍳", defaultPower: 1500, referenceHours: 1,  category: "Kitchen" },
  "Mixer/Grinder":       { icon: "🥤", defaultPower: 500,  referenceHours: 0.25, category: "Kitchen" },
  "Electric Kettle":     { icon: "☕", defaultPower: 1500, referenceHours: 0.5, category: "Kitchen" },
  "Water Pump":          { icon: "🚰", defaultPower: 750,  referenceHours: 1,  category: "Water Supply" },
  "Security System":     { icon: "🔒", defaultPower: 50,   referenceHours: 24, category: "Security" },
  "Other":               { icon: "🔌", defaultPower: 100,  referenceHours: 2,  category: "Other" }
};

const APPLIANCE_NAMES = Object.keys(APPLIANCE_CATALOG);

const HOUSE_TYPES = ["1 BHK", "2 BHK", "3 BHK", "4+ BHK"];
