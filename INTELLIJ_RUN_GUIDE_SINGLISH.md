# AgriPulse Backend - IntelliJ Run Guide (Singlish)

## Important
Me project eka Maven project ekak. `Build > Build Project` eken first build karanna epa. First Maven import eka complete karala Maven Lifecycle `clean` saha `install` run karanna.

## Recommended JDK
IntelliJ eke JDK 21 use karanna puluwan. Project bytecode target eka Java 17 (`release 17`). Java 26 use karanna epa.

## Fresh import
1. Old project close karanna.
2. Me FIXED folder eka extract karanna.
3. IntelliJ -> File -> Open -> me folder eke `pom.xml` select karanna.
4. `Open as Project` / `Load Maven Project` denna.
5. File -> Project Structure -> Project SDK -> JDK 21.
6. Settings -> Build Tools -> Maven -> Runner -> JRE -> Project JDK (21).
7. Maven panel -> Reload All Maven Projects.

## Build
Maven panel -> Lifecycle:
1. `clean`
2. `install`

`BUILD SUCCESS` enna one.

## PostgreSQL
Database name: `agripulse`
User: `postgres`
Port: `5432`

Password source code eke hard-code karanna epa. Run configuration environment variable ekak widihata denna:
`DB_PASSWORD=YOUR_REAL_POSTGRES_PASSWORD`

## Run
`AgriPulseApplication.java` open karala green Run button click karanna.
Successful nam `Tomcat started on port 8080` saha `Started AgriPulseApplication` enawa.

## Why previous error happened
`java: error: release version 5 not supported` Maven algorithm code error ekak newei. Old IntelliJ project metadata / failed Maven import eka module compiler target eka wrong value ekakata (Java 5) set karala tibba. Me fixed project eke old `.idea` metadata remove karala POM eke compiler release 17 explicitly set karala thiyenawa.
