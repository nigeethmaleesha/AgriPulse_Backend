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
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FarmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FarmRepository farmRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        farmRepository.deleteAll();
    }

    @Test
    void testRegisterFarm_Success() throws Exception {
        String farmJson = """
                {
                    "farmName": "Green Valley Farm",
                    "contactNumber": "0771234567",
                    "region": "Kandy",
                    "cropType": "Tea",
                    "landSize": 4.5
                }
                """;

        mockMvc.perform(post("/api/farms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(farmJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.farmName").value("Green Valley Farm"))
                .andExpect(jsonPath("$.contactNumber").value("0771234567"))
                .andExpect(jsonPath("$.region").value("Kandy"));
    }

    @Test
    void testRegisterFarm_DuplicateContactNumber_ReturnsBadRequest() throws Exception {
        String farmJson = """
                {
                    "farmName": "Green Valley Farm",
                    "contactNumber": "0771234567",
                    "region": "Kandy",
                    "cropType": "Tea",
                    "landSize": 4.5
                }
                """;

        mockMvc.perform(post("/api/farms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(farmJson))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/farms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(farmJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("A farm with contact number 0771234567 is already registered."));
    }

    @Test
    void testGetAllFarms_ReturnsFarmList() throws Exception {
        String farmJson = """
                {
                    "farmName": "Highland Farm",
                    "contactNumber": "0719876543",
                    "region": "Nuwara Eliya",
                    "cropType": "Tea",
                    "landSize": 8.0
                }
                """;

        mockMvc.perform(post("/api/farms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(farmJson));

        mockMvc.perform(get("/api/farms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].farmName").value("Highland Farm"));
    }
}
