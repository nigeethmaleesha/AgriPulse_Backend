# AgriPulse Backend - PDSA2 Module 5 Factory Scheduling

This Spring Boot service contains the factory scheduling API, Genetic Algorithm, Simulated Annealing comparison, and scheduling benchmarks.

- Port: `8084`
- Database: `agripulse_module5_scheduling`
- PostgreSQL user: `postgres`
- PostgreSQL password: `1234`
- Frontend proxy: `/module5-api`

Start the shared PostgreSQL service from the project-root `docker-compose.yml`, then run `.\mvnw.cmd spring-boot:run`.

Main endpoints:

- `POST /api/scheduling/genetic`
- `POST /api/scheduling/annealing`
- `POST /api/scheduling/compare`
- `GET /api/scheduling/benchmark/presets`
- `POST /api/scheduling/benchmark`
