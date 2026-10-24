# Corebank — Banking Core

A small core-banking service: accounts with authorization holds, monthly
statements, and interest accrual. Java 21, Spring Boot, Maven.

## Running

### Prerequisites
- Java 21 (JDK)
- Maven

Maven must run on Java 21, not an older JDK it may pick up by default — set
`JAVA_HOME` to your 21 install if `mvn test` reports an invalid target release.

```bash
mvn spring-boot:run          # http://localhost:8080
mvn test                     # everything
mvn test -Dtest='*VisibleTest'   # just the tests you can see
```

## Endpoints

| Method | Path | Purpose |
|---|---|---|
| GET | `/accounts/{id}/available-balance` | Spendable balance, net of active holds |
| GET | `/accounts/{id}/statement?periodStart=&nextPeriodStart=` | Postings for one statement period |
| GET | `/accounts/{id}/interest?principalCents=&annualRate=&from=&to=` | Interest accrued over a period |

## Layout

```
src/main/java/com/worksim/corebank/
  domain/     records: Account, Hold, Posting
  store/      in-memory storage
  service/    the logic that matters
  web/        thin REST layer
src/test/java/visible/   tests you can read and run
src/test/java/grading/   the suite your PR is graded against
```

This repository ships with known defects — that is the point. Your tickets say
which one is yours.
