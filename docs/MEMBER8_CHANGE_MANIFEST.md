# Member 8 Change Manifest

## Existing Member 7 Java files preserved unchanged
- `SpoilageRiskRankingApplication.java`
- `controller/RiskRankingController.java`
- `model/AlgorithmTestResult.java`
- `model/HarvestBatch.java`
- `repository/AlgorithmTestResultRepository.java`
- `repository/HarvestBatchRepository.java`
- `service/BenchmarkService.java`
- `service/RiskRankingService.java`
- `util/SyntheticBatchGenerator.java`

## Added for Member 8
- `datastructure/HarvestBatchMaxHeap.java`
- `service/PriorityQueueService.java`
- `service/PriorityQueueBenchmarkService.java`
- `controller/PriorityQueueController.java`
- `controller/ApiExceptionHandler.java`
- `dto/PriorityQueueStatusResponse.java`
- `dto/PriorityBenchmarkResponse.java`
- `src/test/.../HarvestBatchMaxHeapTest.java`
- `database/module4_schema.sql`
- `database/sample_batches.sql`
- `postman/AgriPulse_Module4_Member8.postman_collection.json`
- `docs/MEMBER8_A_TO_Z_SINGLISH.md`

## Configuration-only safety changes
- `application.properties`: database password is no longer hard-coded; environment variables are used. The default dev database is `agripulse_module4` and default port is `8081` to avoid conflict with Module 3.
- `pom.xml`: explicit Java 17 Maven compiler release added to avoid IntelliJ/JDK compiler-target mismatch. Dependencies and Member 7 application structure are unchanged.
