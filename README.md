# Football ETL Pipeline in Scala

A modular **ETL (Extract – Transform – Load) pipeline** built in Scala to process, validate, analyze, and generate reports from football player data stored in JSON format.

---

## Overview

This project processes potentially incomplete or inconsistent football player data through a robust data pipeline:

* **Extract** raw JSON data
* **Transform** by validating and cleaning records
* **Analyze** data with advanced statistics and rankings
* **Load / Output** structured reports

The project focuses on **data quality**, **functional programming**, and **modular pipeline design**.

---

## Architecture

```text
DataLoader
   ↓
DataValidator
   ↓
StatsCalculator
   ↓
ReportGenerator
   ↓
Main
```

### Modules

* **DataLoader**
  Reads JSON files and safely handles parsing errors.

* **DataValidator**
  Applies business rules and removes duplicates.

* **StatsCalculator**
  Computes rankings, aggregations, and advanced metrics.

* **ReportGenerator**
  Builds structured output files.

* **Main**
  Orchestrates the ETL pipeline.

---

## Tech Stack

* **Scala**
* **sbt**
* **Circe** (JSON parsing)
* **MUnit** (unit testing)
* Functional programming principles (`map`, `flatMap`, `groupBy`, `fold`)
* Error handling with `Either`, `Option`, and `Try`

---

## Features

* Safe JSON decoding and parsing error tracking
* Data validation based on business rules
* Duplicate removal using unique player ID
* Top 10 rankings:

  * scorers
  * assisters
  * market values
  * salaries
* Aggregations by league and position
* Discipline statistics (yellow/red cards)
* Advanced analytics:

  * offensive efficiency (goals per match)
  * value-for-money ratio (goals per salary)
  * league-level statistics
* Structured report generation
* Unit tests ensuring correctness of calculations

---

## Project Structure

```text
.
├── data/              # input datasets (clean, dirty, large)
├── output/            # generated results
├── src/               # source code
├── project/           # sbt configuration
├── build.sbt
├── README.md
└── .gitignore
```

---

## Run the Project

### Requirements

* Java 11+
* sbt
* Scala

### Commands

```bash
sbt compile
sbt run
sbt test
```

---

## Outputs

The pipeline generates:

* `results.json` → structured analytical results
* `report.txt` → human-readable summary

---

## ⚡ Performance

Tested on a large dataset:

* Execution time: **~2.8 seconds**
* Throughput: **~3500 records/second**

---

## Key Design Choices

### Functional Programming

* Immutable data structures
* Pure functions
* Use of `map`, `flatMap`, `fold`, `groupBy`

### Error Handling

* `Either` → critical errors (parsing, I/O)
* `Option` → missing values
* `Try` → safe file writing
* No use of `null`

### Data Quality

* Validation rules (age, matches, stats consistency)
* Handling missing values
* Deduplication logic

---

## What I Learned

Through this project, I strengthened my skills in:

* Designing modular **data pipelines**
* Writing clean and maintainable **Scala code**
* Applying **functional programming concepts**
* Handling real-world **data quality issues**
* Building **analytical reports from raw data**
* Writing **unit tests for data logic**

---

## Why This Project Matters

This project demonstrates key Data Engineering concepts:

* ETL pipeline design
* Data validation and cleaning
* Scalable analytics computation
* Structured data outputs
* Robust error handling

These concepts are directly applicable to real-world data systems.

---

### 👤 Author

**Farid ABOUBAKARI**
Engineering Student – Big Data & Machine Learning
EFREI Paris

---

### ⭐ If you found this project interesting

Feel free to star the repository or connect with me on LinkedIn.
