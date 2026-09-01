package com.agripulse.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agripulse.repository.FarmRepository;
import com.agripulse.repository.FertilizerRequestRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FertilizerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FertilizerRequestRepository requestRepository;

    @Autowired
    private FarmRepository farmRepository;

    @BeforeEach
    void setUp() {
        requestRepository.deleteAll();
        farmRepository.deleteAll();
    }

    @Test
    void testCreateRequest_AutoRegistersNewFarm_Success() throws Exception {
        String requestDtoJson = """
                {
                    "farmName": "Sunrise Estate",
                    "contactNumber": "0755554444",
                    "region": "Ratnapura",
                    "cropType": "Rubber",
                    "landSize": 6.2,
                    "fertilizerType": "Urea",
                    "requestedBags": 25,
                    "benefitScore": 75.0,
                    "urgencyLevel": "HIGH"
                }
                """;

        mockMvc.perform(post("/api/fertilizer/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestDtoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.fertilizerType").value("Urea"))
                .andExpect(jsonPath("$.requestedBags").value(25))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void testAllocate_ProductionKnapsackDP_PersistsResults() throws Exception {
        // Create request
        String requestDtoJson = """
                {
                    "farmName": "Sunrise Estate",
                    "contactNumber": "0755554444",
                    "region": "Ratnapura",
                    "cropType": "Rubber",
                    "landSize": 6.2,
                    "fertilizerType": "Urea",
                    "requestedBags": 20,
                    "benefitScore": 80.0,
                    "urgencyLevel": "HIGH"
                }
                """;

        mockMvc.perform(post("/api/fertilizer/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestDtoJson));

        // Allocate with capacity = 25
        String allocateJson = """
                {
                    "totalCapacity": 25
                }
                """;

        mockMvc.perform(post("/api/fertilizer/allocate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(allocateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCapacity").value(25))
                .andExpect(jsonPath("$.allocatedRequests.length()").value(1))
                .andExpect(jsonPath("$.rejectedRequests.length()").value(0))
                .andExpect(jsonPath("$.totalBenefitAchieved").value(80.0));
    }

    @Test
    void testRunBenchmark_ReturnsComparisonResults() throws Exception {
        mockMvc.perform(get("/api/fertilizer/benchmark"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(9));
    }
}
