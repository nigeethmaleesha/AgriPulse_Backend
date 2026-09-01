package com.agripulse.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PumpControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testRunPumpBenchmark_Success() throws Exception {
        mockMvc.perform(get("/api/pumps/benchmark")
                        .param("numberOfFarms", "20")
                        .param("availablePumps", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].algorithmUsed").value("Max-Heap (Primary)"))
                .andExpect(jsonPath("$[1].algorithmUsed").value("Full Sort (Comparison)"))
                .andExpect(jsonPath("$[2].algorithmUsed").value("Greedy Baseline"));
    }
}
