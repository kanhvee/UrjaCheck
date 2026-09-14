# UrjaCheck — Residential Energy Assessment (Frontend + Backend)

Your existing frontend prototype, now backed by a real Java + Spring Boot +
MySQL API. The UI, layout, colors, navigation, forms, cards and charts are
unchanged from your uploaded ZIP — only the JavaScript that used to compute
locally and store to `localStorage` now calls the backend instead.

```
energy-assessment/
├── backend/            Spring Boot + MySQL API (see backend/README.md)
│   ├── src/
│   ├── pom.xml
│   └── README.md
├── frontend/            your original UI, lightly wired to the API
│   ├── index.html
│   ├── admin.html
│   ├── css/style.css
│   └── js/
│       ├── config.js        NEW — API_BASE_URL
│       ├── data.js          trimmed — dummy-household generator removed
│       ├── calculations.js  status labels aligned with the backend
│       ├── app.js           now POSTs to /api/assessment
│       └── admin.js         now fetches /api/admin/*
└── README.md            (this file)
```

## 1. What changed in the frontend, and why

Per your instruction not to redesign anything, the changes are limited to
what integration actually requires:

| File | Change | Why |
|---|---|---|
| `js/config.js` | **New file.** Holds `API_BASE_URL`. | Single place to point the frontend at your backend. |
| `js/data.js` | Removed the seeded-random dummy-household generator (`generateDummyHouseholds`, `DUMMY_HOUSEHOLDS`). Kept `APPLIANCE_CATALOG` (icons/default power for the form), `HOUSE_TYPES`. Added a `category` field per appliance. | The admin dashboard must show real MySQL data now — the brief is explicit about not fabricating stats. `category` is sent to the API as metadata. |
| `js/calculations.js` | `getAssessmentStatus()` / `STATUS_LABELS` / `STATUS_CLASSES` changed from a 3-tier "within/moderate/significant" (all above-reference) model to `BELOW_REFERENCE` / `WITHIN_REFERENCE_RANGE` / `ABOVE_REFERENCE` with a ±10% band. Added `REFERENCE_TYPE_LABELS`. | This matches the backend's classification exactly (section 9 of your integration brief explicitly asks for a below/within/above model). The functions are still used, but now only for the instant client-side *preview* on the Review screen — the backend's numbers are authoritative. |
| `js/app.js` | `submitAssessment()` now POSTs to the API and renders the real response. Added `buildAssessmentRequestPayload()` and `mapApiResponseToLocalAssessment()`. Removed `saveAssessment()` (localStorage) — the backend persists it. `renderResults()` and `downloadReport()` kept the same structure/IDs, just fed from the mapped response. | This is the actual integration point. |
| `js/admin.js` | Every render function now `fetch`es from `/api/admin/*` or `/api/assessment/{id}` instead of reading `DUMMY_HOUSEHOLDS`. Added empty states for a fresh, empty database. | Real data instead of fake stats. |
| `index.html` | Added two small elements: `#resSourceLine` (reference-source transparency line) and `#resCostNote` (dynamic tariff text), plus a `<script src="js/config.js">` tag. No layout/structure changes. | The backend can tell you whether a benchmark is a project assumption or a real BEE figure — the UI needed *somewhere* to say that honestly. |
| `admin.html` | Replaced the old **fabricated 6-month trend chart** with a real "average actual vs. average reference" comparison (reusing the same `.compare-bar-*` CSS classes already used on the resident result page — no new visual language introduced). Added a dashboard empty state. Added `<script src="js/config.js">`. | You explicitly said not to fabricate admin statistics; the honest replacement uses the same visual style. |

Nothing else — no new pages, no new navigation, no color/typography changes,
no removed functionality.

## 2. Field mapping

**Frontend → API request → JPA entity → MySQL column → API response**

| Frontend (`state.appliances[]`) | API request (`ApplianceRequest`) | Entity (`ApplianceUsage`) | DB column | API response (`ApplianceResult`) |
|---|---|---|---|---|
| `applianceType` | `applianceName` | `applianceName` | `appliance_name` | `applianceName` |
| *(from `APPLIANCE_CATALOG[type].category`)* | `category` | `applianceCategory` | `appliance_category` | `category` |
| `quantity` | `quantity` | `quantity` | `quantity` | `quantity` |
| `powerWatts` | `powerWatts` | `powerWatts` | `power_watts` | `powerWatts` |
| `operatingHours` | `hoursPerDay` | `hoursPerDay` | `hours_per_day` | `hoursPerDay` |
| *(computed)* | — | `dailyKwh` | `daily_kwh` | `dailyKwh` |
| *(computed)* | — | `monthlyKwh` | `monthly_kwh` | `monthlyKwh` |
| *(from reference_standard)* | — | `referenceHoursPerDay` | `reference_hours_per_day` | `referenceHoursPerDay` |
| *(computed)* | — | `referenceMonthlyKwh` | `reference_monthly_kwh` | `referenceMonthlyKwh` |
| *(computed)* | — | `differenceKwh` / `differencePercentage` | `difference_kwh` / `difference_percentage` | `differenceKwh` / `differencePercentage` |
| *(computed)* | — | `status` | `status` | `status` |

**Household level:**

| Frontend (`state`) | API request (`AssessmentRequest`) | Entity (`Assessment`) | DB column | API response (`AssessmentResponse`/`EnergySummary`) |
|---|---|---|---|---|
| `householdSize` | `householdSize` | `householdSize` | `household_size` | `householdSize` |
| `houseType` | `houseType` | `houseType` | `house_type` | `houseType` |
| `actualBill` | `actualBill` | `actualBill` | `actual_bill` | `summary.actualBill` |
| *(none — server default or override)* | `tariff` *(optional)* | `tariff` | `tariff` | `summary.tariff` |
| *(generated by server)* | — | `assessmentUuid` | `assessment_uuid` | `assessmentId` |

## 3. REST API

Base URL: `http://localhost:8080/api`. Full interactive docs at
`http://localhost:8080/swagger-ui.html` once the backend is running.

### `POST /assessment`
Submit a new assessment.

```json
{
  "householdSize": 4,
  "houseType": "2 BHK",
  "actualBill": 3600,
  "appliances": [
    { "applianceName": "Ceiling Fan", "category": "Cooling", "quantity": 4, "powerWatts": 75, "hoursPerDay": 8 }
  ]
}
```

`201 Created` →
```json
{
  "assessmentId": "UC-4F2A9B10",
  "householdSize": 4,
  "houseType": "2 BHK",
  "createdAt": "2026-09-14T10:15:30",
  "summary": {
    "dailyKwh": 2.4, "monthlyKwh": 72, "estimatedMonthlyCost": 576,
    "referenceMonthlyKwh": 63, "differenceKwh": 9, "differencePercentage": 14.29,
    "status": "ABOVE_REFERENCE", "tariff": 8.0, "actualBill": 3600
  },
  "appliances": [
    {
      "applianceName": "Ceiling Fan", "category": "Cooling", "quantity": 4,
      "powerWatts": 75, "hoursPerDay": 8, "dailyKwh": 2.4, "monthlyKwh": 72,
      "referenceHoursPerDay": 7, "referenceMonthlyKwh": 63,
      "differenceKwh": 9, "differencePercentage": 14.29, "status": "ABOVE_REFERENCE",
      "recommendation": "Switch off fans in unoccupied rooms...",
      "referenceSource": {
        "referenceType": "PROJECT_REFERENCE",
        "sourceOrganization": "UrjaCheck Project",
        "sourceDocument": "Project-defined household reference assumption",
        "sourceUrl": null, "applicableYear": null,
        "notes": "No verified official BEE/Government benchmark was available..."
      }
    }
  ]
}
```

`400 Bad Request` on invalid input, e.g.:
```json
{ "success": false, "message": "Invalid input. Please check the highlighted fields.",
  "errors": { "appliances[0].quantity": "Quantity must be at least 1" } }
```

### `GET /assessment/{assessmentId}`
Returns the same shape as above. `404` if not found.

### `GET /admin/summary`
```json
{
  "totalAssessments": 12, "averageMonthlyConsumption": 301.4,
  "averageReferenceConsumption": 268.2, "totalCommunityConsumption": 3616.8,
  "averageEstimatedCost": 2411.2, "householdsAboveReference": 5,
  "householdsWithinReference": 6, "householdsBelowReference": 1
}
```

### `GET /admin/households`
Array of anonymized summaries (no PII — assessment UUID only):
```json
[{ "assessmentId": "UC-4F2A9B10", "householdSize": 4, "houseType": "2 BHK",
   "totalMonthlyKwh": 72, "referenceMonthlyKwh": 63, "differenceKwh": 9,
   "differencePercentage": 14.29, "status": "ABOVE_REFERENCE",
   "estimatedMonthlyCost": 576, "createdAt": "2026-09-14T10:15:30" }]
```

### `GET /admin/appliances`
```json
[{ "applianceName": "Ceiling Fan", "householdCount": 9, "totalQuantity": 34,
   "averageMonthlyKwh": 68.2, "totalMonthlyKwh": 613.8, "averageHoursPerDay": 7.6 }]
```

### `GET /reference-standards`
Returns every active `reference_standard` row, including `referenceType` and
source metadata, so any client can display where a benchmark comes from.

## 4. Error handling

| Status | When |
|---|---|
| 400 | Bean Validation failure (bad quantity/power/hours/household size, missing appliances) |
| 404 | `GET /assessment/{id}` with an unknown ID |
| 500 | Unexpected server error (message is generic — no internals leaked) |

## 5. Running the whole thing

1. **Start MySQL**, then create the database:
   ```sql
   CREATE DATABASE energy_assessment;
   ```
2. **Configure credentials** in `backend/src/main/resources/application.properties`
   (see `backend/README.md`).
3. **Run the backend**:
   ```bash
   cd backend
   mvn spring-boot:run
   ```
   It listens on `http://localhost:8080`.
4. **Serve the frontend** with a simple local server (not `file://` — see
   `backend/README.md` for why):
   ```bash
   cd frontend
   python3 -m http.server 5500
   ```
5. **Open** `http://localhost:5500/index.html`, complete an assessment, and
   submit it.
6. **Verify in MySQL**:
   ```sql
   SELECT * FROM assessment ORDER BY created_at DESC LIMIT 1;
   SELECT * FROM appliance_usage WHERE assessment_id = (SELECT id FROM assessment ORDER BY created_at DESC LIMIT 1);
   ```
7. **Verify the frontend** shows the same numbers back (Result screen —
   they're now coming from the API response, not client-side math).
8. **Open** `http://localhost:5500/admin.html`, log in with `admin` / `admin123`,
   and confirm the dashboard/household table/appliance analytics reflect the
   assessment you just submitted (submit 2–3 more from different browsers/tabs
   to see the community numbers move).

## 6. How a request flows end-to-end

```
Resident fills the form (index.html / app.js)
   → app.js: buildAssessmentRequestPayload()
   → fetch POST http://localhost:8080/api/assessment
   → AssessmentController.submitAssessment()
   → AssessmentService.createAssessment()
        → EnergyCalculationService.calculateAppliance() for each appliance
             → EnergyCalculationUtil (pure math: daily/monthly/reference/% diff)
             → ReferenceStandardService (looks up reference_standard row + source metadata)
             → RecommendationService (rule-based text)
        → EnergyCalculationService.summarize() (household totals)
        → AssessmentRepository.save() → MySQL (assessment + appliance_usage tables)
   → AssessmentResponse JSON returned
   → app.js: mapApiResponseToLocalAssessment()
   → renderResults() — same rendering code as the original prototype, now
     fed by real, persisted, server-calculated numbers
```

Admin analytics follow the same shape: `admin.js` fetches `/api/admin/*`,
which `AdminService` builds by aggregating everything currently in
`AssessmentRepository`/`ApplianceUsageRepository` — no separate "demo mode"
exists on the backend; the database's actual contents are the only source
of truth.

## 7. Test data & test cases

- **Sample request** (matches the brief's worked example exactly): see
  section 3 above. Expected: `dailyKwh=2.4`, `monthlyKwh=72`,
  `estimatedMonthlyCost=576`, `referenceMonthlyKwh=63`,
  `differencePercentage≈14.29`, `status="ABOVE_REFERENCE"`.
- **Automated tests** (`backend/src/test/java/...`):
  - `EnergyCalculationUtilTest` — daily/monthly/cost/reference/percentage math,
    zero-reference safety, and all three status bands.
  - `AssessmentValidationTest` — Bean Validation on `AssessmentRequest`/
    `ApplianceRequest` (household size 1–20, quantity ≥ 1, power > 0,
    0 ≤ hours ≤ 24, at least one appliance).
- **Manual test cases** worth trying once both servers are up:
  1. Submit an assessment with a single Ceiling Fan (4 / 75W / 8h) → confirm
     the Result screen shows exactly the numbers in section 3.
  2. Submit with `hoursPerDay: 30` → expect a 400 with a field error, shown
     as a toast in the UI (form should not silently proceed).
  3. Submit with no appliances → expect 400, `"Please add at least one
     appliance before continuing."`.
  4. Open `admin.html` before any assessment exists → dashboard shows the
     empty state, not zeros dressed up as real stats.
  5. Submit 3 assessments with different house types, then check
     `/api/admin/appliances` — `householdCount` for each appliance should
     match how many of the 3 you actually included it in.

## 8. Reference source transparency

Every appliance result includes `referenceSource.referenceType`, one of:

- `GOVERNMENT_MANDATED` — an official, regulated limit (not currently seeded — none applied)
- `BEE_BENCHMARK` — a published BEE figure (not currently seeded — see below)
- `STAR_LABEL_INFO` — general BEE efficiency information, no numeric target
- `PROJECT_REFERENCE` — a project-defined assumption (what every seeded row currently is)

**No numeric value in `data.sql` is claimed to be an official government or
BEE figure.** I did not have a verified, citable per-appliance household
operating-hours benchmark from BEE/Eco Niwas Samhita to seed as fact, and
inventing one would violate the brief's explicit instruction. The two
`STAR_LABEL_INFO` rows link to BEE's real site
(https://www.beeindia.gov.in/) for general context only.

If you have access to a real, sourced figure, add or edit a row in
`reference_standard` (via SQL or a future admin UI) with `reference_type`
set to `BEE_BENCHMARK` or `GOVERNMENT_MANDATED`, and the frontend's Result
screen will automatically say so instead of "project reference assumption" —
see `resSourceLine` in `app.js`.

The UI language itself is also deliberately neutral: results say *"above/within/
below the reference level"*, never *"more than the Government allows"* or
*"wasteful"* — consumption depends on household size, appliances, climate and
habits.
