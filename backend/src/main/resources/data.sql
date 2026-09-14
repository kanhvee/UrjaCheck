-- ============================================================
-- UrjaCheck — reference_standard seed data
--
-- IMPORTANT: every row below is reference_type = 'PROJECT_REFERENCE'.
-- These are the same household operating-hour assumptions the original
-- frontend prototype used (data.js), carried over here as the
-- authoritative, single source of truth on the backend.
--
-- They are NOT official Bureau of Energy Efficiency (BEE) or Government
-- of India figures. No verified, citable BEE/ENS per-appliance household
-- operating-hour benchmark was available to seed here, so none is
-- invented. If your organisation has a real, sourced BEE/ENS figure for
-- a given appliance, update that row's reference_type to 'BEE_BENCHMARK'
-- (or 'GOVERNMENT_MANDATED' if applicable) and fill in source_document /
-- source_url / applicable_year with the real citation.
--
-- Two general, non-numeric STAR_LABEL_INFO rows are also included for
-- Air Conditioners and Refrigerators, pointing to BEE's public Star
-- Labelling Programme. They carry no reference_hours_per_day and are
-- never used in the energy calculation — they exist purely so the UI can
-- show a "learn more about efficiency ratings" link with a real source.
-- ============================================================

INSERT INTO reference_standard
  (appliance_category, appliance_name, reference_type, reference_value, unit, reference_hours_per_day,
   source_organization, source_document, source_url, applicable_year, notes, active)
VALUES
  ('Lighting', 'Lighting Fixtures', 'PROJECT_REFERENCE', NULL, 'hours/day', 5,
   'UrjaCheck Project', 'Project-defined household reference assumption', NULL, NULL,
   'No verified official BEE/Government household operating-hour benchmark was available; this is a project assumption for awareness comparison only.', TRUE),

  ('Lighting', 'LED Bulb', 'PROJECT_REFERENCE', NULL, 'hours/day', 5,
   'UrjaCheck Project', 'Project-defined household reference assumption', NULL, NULL,
   'No verified official BEE/Government household operating-hour benchmark was available; this is a project assumption for awareness comparison only.', TRUE),

  ('Lighting', 'Tube Light', 'PROJECT_REFERENCE', NULL, 'hours/day', 5,
   'UrjaCheck Project', 'Project-defined household reference assumption', NULL, NULL,
   'No verified official BEE/Government household operating-hour benchmark was available; this is a project assumption for awareness comparison only.', TRUE),

  ('Cooling', 'Ceiling Fan', 'PROJECT_REFERENCE', NULL, 'hours/day', 7,
   'UrjaCheck Project', 'Project-defined household reference assumption', NULL, NULL,
   'No verified official BEE/Government household operating-hour benchmark was available; this is a project assumption for awareness comparison only.', TRUE),

  ('Cooling', 'Table Fan', 'PROJECT_REFERENCE', NULL, 'hours/day', 6,
   'UrjaCheck Project', 'Project-defined household reference assumption', NULL, NULL,
   'No verified official BEE/Government household operating-hour benchmark was available; this is a project assumption for awareness comparison only.', TRUE),

  ('Cooling', 'Air Conditioner', 'PROJECT_REFERENCE', NULL, 'hours/day', 4,
   'UrjaCheck Project', 'Project-defined household reference assumption', NULL, NULL,
   'No verified official BEE/Government household operating-hour benchmark was available; this is a project assumption for awareness comparison only.', TRUE),

  ('Cooling', 'Air Cooler', 'PROJECT_REFERENCE', NULL, 'hours/day', 5,
   'UrjaCheck Project', 'Project-defined household reference assumption', NULL, NULL,
   'No verified official BEE/Government household operating-hour benchmark was available; this is a project assumption for awareness comparison only.', TRUE),

  ('Kitchen', 'Refrigerator', 'PROJECT_REFERENCE', NULL, 'hours/day', 10,
   'UrjaCheck Project', 'Project-defined household reference assumption', NULL, NULL,
   'No verified official BEE/Government household operating-hour benchmark was available; this is a project assumption for awareness comparison only.', TRUE),

  ('Entertainment', 'Television', 'PROJECT_REFERENCE', NULL, 'hours/day', 3,
   'UrjaCheck Project', 'Project-defined household reference assumption', NULL, NULL,
   'No verified official BEE/Government household operating-hour benchmark was available; this is a project assumption for awareness comparison only.', TRUE),

  ('Electronics', 'Computer/Laptop', 'PROJECT_REFERENCE', NULL, 'hours/day', 5,
   'UrjaCheck Project', 'Project-defined household reference assumption', NULL, NULL,
   'No verified official BEE/Government household operating-hour benchmark was available; this is a project assumption for awareness comparison only.', TRUE),

  ('Laundry', 'Washing Machine', 'PROJECT_REFERENCE', NULL, 'hours/day', 1,
   'UrjaCheck Project', 'Project-defined household reference assumption', NULL, NULL,
   'No verified official BEE/Government household operating-hour benchmark was available; this is a project assumption for awareness comparison only.', TRUE),

  ('Laundry', 'Iron', 'PROJECT_REFERENCE', NULL, 'hours/day', 0.5,
   'UrjaCheck Project', 'Project-defined household reference assumption', NULL, NULL,
   'No verified official BEE/Government household operating-hour benchmark was available; this is a project assumption for awareness comparison only.', TRUE),

  ('Water Heating', 'Water Heater/Geyser', 'PROJECT_REFERENCE', NULL, 'hours/day', 1,
   'UrjaCheck Project', 'Project-defined household reference assumption', NULL, NULL,
   'No verified official BEE/Government household operating-hour benchmark was available; this is a project assumption for awareness comparison only.', TRUE),

  ('Kitchen', 'Microwave', 'PROJECT_REFERENCE', NULL, 'hours/day', 0.5,
   'UrjaCheck Project', 'Project-defined household reference assumption', NULL, NULL,
   'No verified official BEE/Government household operating-hour benchmark was available; this is a project assumption for awareness comparison only.', TRUE),

  ('Kitchen', 'Induction Cooktop', 'PROJECT_REFERENCE', NULL, 'hours/day', 1,
   'UrjaCheck Project', 'Project-defined household reference assumption', NULL, NULL,
   'No verified official BEE/Government household operating-hour benchmark was available; this is a project assumption for awareness comparison only.', TRUE),

  ('Kitchen', 'Mixer/Grinder', 'PROJECT_REFERENCE', NULL, 'hours/day', 0.25,
   'UrjaCheck Project', 'Project-defined household reference assumption', NULL, NULL,
   'No verified official BEE/Government household operating-hour benchmark was available; this is a project assumption for awareness comparison only.', TRUE),

  ('Kitchen', 'Electric Kettle', 'PROJECT_REFERENCE', NULL, 'hours/day', 0.5,
   'UrjaCheck Project', 'Project-defined household reference assumption', NULL, NULL,
   'No verified official BEE/Government household operating-hour benchmark was available; this is a project assumption for awareness comparison only.', TRUE),

  ('Water Supply', 'Water Pump', 'PROJECT_REFERENCE', NULL, 'hours/day', 1,
   'UrjaCheck Project', 'Project-defined household reference assumption', NULL, NULL,
   'No verified official BEE/Government household operating-hour benchmark was available; this is a project assumption for awareness comparison only.', TRUE),

  ('Security', 'Security System', 'PROJECT_REFERENCE', NULL, 'hours/day', 24,
   'UrjaCheck Project', 'Project-defined household reference assumption', NULL, NULL,
   'Runs continuously by design; reference hours reflect that, not an efficiency target.', TRUE),

  ('Other', 'Other', 'PROJECT_REFERENCE', NULL, 'hours/day', 2,
   'UrjaCheck Project', 'Project-defined household reference assumption', NULL, NULL,
   'Generic fallback for appliances not covered by a specific category.', TRUE),

  -- General, non-numeric efficiency information (never used in calculation):
  ('Cooling', 'Air Conditioner', 'STAR_LABEL_INFO', NULL, NULL, NULL,
   'Bureau of Energy Efficiency (BEE)', 'BEE Star Labelling Programme (general appliance efficiency information)',
   'https://www.beeindia.gov.in/', NULL,
   'General information only. Does not represent a numeric household consumption benchmark; refer to the source for current star ratings.', TRUE),

  ('Kitchen', 'Refrigerator', 'STAR_LABEL_INFO', NULL, NULL, NULL,
   'Bureau of Energy Efficiency (BEE)', 'BEE Star Labelling Programme (general appliance efficiency information)',
   'https://www.beeindia.gov.in/', NULL,
   'General information only. Does not represent a numeric household consumption benchmark; refer to the source for current star ratings.', TRUE);
