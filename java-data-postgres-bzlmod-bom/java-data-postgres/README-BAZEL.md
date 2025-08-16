# Bazel (Bzlmod) Usage

This repository uses **Bzlmod** (MODULE.bazel) with `rules_jvm_external` and a Spring Boot BOM.

## First-time
```bash
bazel run @maven//:pin
```

## Build / Run / Test
```bash
bazel build //:java_data_postgres_app
bazel run //:java_data_postgres_app
bazel test //:java_data_postgres_tests
```

## Notes
- Spring Boot BOM from the parent POM is wired via `bom_artifacts`, so Spring starters/tests can be **versionless**.
- If any non-Spring dependency fails to resolve (e.g., lombok if not managed by BOM), add an explicit version to `artifacts`.
