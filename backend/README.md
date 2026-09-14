# UrjaCheck Backend

Spring Boot + MySQL backend for the UrjaCheck residential energy assessment app.

## Requirements

- Java 17+ (JDK)
- Maven 3.8+ (or use the wrapper if you add one)
- MySQL 8.x running locally (or reachable over the network)

## 1. Create the database

```sql
CREATE DATABASE energy_assessment;
```

No tables need to be created by hand — Hibernate creates them automatically on
first run (`spring.jpa.hibernate.ddl-auto=update`), and `data.sql` then seeds
the `reference_standard` table.

## 2. Configure credentials

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/energy_assessment?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

Never commit real credentials. For anything beyond local development, prefer
environment variables (`SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`)
over editing this file directly — Spring Boot picks those up automatically.

## 3. Run it

```bash
cd backend
mvn spring-boot:run
```

The API starts on **http://localhost:8080**. Swagger UI is at
**http://localhost:8080/swagger-ui.html**.

## 4. Run the tests

```bash
mvn test
```

`EnergyCalculationUtilTest` verifies the exact worked example from the
project brief (4 ceiling fans × 75 W × 8 h/day → 2.4 kWh/day → 72 kWh/month →
₹576/month, vs. a 63 kWh/month reference → ~14.29% above). `AssessmentValidationTest`
checks the Bean Validation rules (quantity ≥ 1, power > 0, 0 ≤ hours ≤ 24,
household size 1–20, at least one appliance).

## What's in here

```
backend/
├── pom.xml
├── README.md                 (this file)
└── src/
    ├── main/
    │   ├── java/com/energyassessment/
    │   │   ├── EnergyAssessmentApplication.java
    │   │   ├── controller/    AssessmentController, AdminController, ReferenceStandardController
    │   │   ├── service/       AssessmentService, EnergyCalculationService,
    │   │   │                  ReferenceStandardService, RecommendationService, AdminService
    │   │   ├── repository/    AssessmentRepository, ApplianceUsageRepository, ReferenceStandardRepository
    │   │   ├── entity/        Assessment, ApplianceUsage, ReferenceStandard
    │   │   ├── dto/           request/response objects (see root README for shapes)
    │   │   ├── exception/     GlobalExceptionHandler, ResourceNotFoundException
    │   │   ├── config/        CorsConfig
    │   │   └── util/          EnergyCalculationUtil (pure calculation functions)
    │   └── resources/
    │       ├── application.properties
    │       └── data.sql       (reference_standard seed — see note below)
    └── test/java/com/energyassessment/
        ├── util/EnergyCalculationUtilTest.java
        └── service/AssessmentValidationTest.java
```

## On the reference/benchmark data — please read

`data.sql` seeds one row per appliance in the `reference_standard` table. **Every
seeded row is `reference_type = 'PROJECT_REFERENCE'`.** These are the same
household operating-hour assumptions the original frontend prototype used —
carried over here so the numbers stay consistent, not because they're official.

I did not have a verified, citable Bureau of Energy Efficiency (BEE) or
Eco Niwas Samhita figure for per-appliance *household operating hours* to
seed as fact, and the brief is explicit that inventing one and labeling it
"government" would be worse than not having it. So:

- If you have a real, sourced BEE/ENS/BIS figure for a specific appliance,
  update that row: set `reference_type` to `BEE_BENCHMARK` or
  `GOVERNMENT_MANDATED` as appropriate, and fill in `source_document`,
  `source_url`, and `applicable_year` with the actual citation.
- Two `STAR_LABEL_INFO` rows (Air Conditioner, Refrigerator) point to BEE's
  public site (`https://www.beeindia.gov.in/`) as general context. They carry
  no numeric value and are never used in the calculation.
- The API always returns which category a value belongs to
  (`referenceSource.referenceType`), so the frontend can — and does — say
  "project reference assumption" instead of implying a government standard
  when that's what it is.

## CORS / running the frontend

CORS is configured (`config/CorsConfig.java`) to allow any `localhost` or
`127.0.0.1` origin/port. If you open `frontend/index.html` directly as a
`file://` URL, `fetch()` calls to the backend may behave inconsistently across
browsers. Serve the frontend with a simple local server instead:

```bash
cd frontend
python3 -m http.server 5500
# then open http://localhost:5500/index.html
```
