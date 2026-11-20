package com.flash.alchemy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests of the REST API with the whole Spring application running.
 * A new context is created for each test so the memory starts empty.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ApiTest {

    private static final String FULL_BODY = "{\"devices\":["
            + "{\"name\":\"star_labs\",\"distance\":640.31,\"message\":[\"the\",\"\",\"is\",\"\",\"the\",\"\",\"factory\"]},"
            + "{\"name\":\"ccpd\",\"distance\":447.21,\"message\":[\"\",\"laboratory\",\"\",\"under\",\"\",\"old\",\"\"]},"
            + "{\"name\":\"star_satellite\",\"distance\":632.46,\"message\":[\"\",\"the\",\"\",\"is\",\"under\",\"\",\"old\",\"factory\"]}"
            + "]}";

    private static final String CCPD_BODY =
            "{\"distance\":447.21,\"message\":[\"\",\"laboratory\",\"\",\"under\",\"\",\"old\",\"\"]}";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void f1ProcessComplete() throws Exception {
        mockMvc.perform(post("/process").contentType(MediaType.APPLICATION_JSON).content(FULL_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("the laboratory is under the old factory"));
    }

    @Test
    void f2RegisterAndF3Consult() throws Exception {
        mockMvc.perform(post("/devices/ccpd").contentType(MediaType.APPLICATION_JSON).content(CCPD_BODY))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/devices/ccpd"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.distance").value(447.21));

        // Only one device: not enough information
        mockMvc.perform(get("/process"))
                .andExpect(status().isNotFound());
    }

    @Test
    void f4ReplaceF5UpdateF6Delete() throws Exception {
        mockMvc.perform(post("/devices/ccpd").contentType(MediaType.APPLICATION_JSON).content(CCPD_BODY))
                .andExpect(status().isCreated());

        mockMvc.perform(put("/devices/ccpd").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"distance\":100,\"message\":[\"new\"]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message[0]").value("new"));

        mockMvc.perform(patch("/devices/ccpd").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"distance\":50}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.distance").value(50.0))
                .andExpect(jsonPath("$.message[0]").value("new"));

        mockMvc.perform(delete("/devices/ccpd"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/devices/ccpd"))
                .andExpect(status().isNotFound());
    }

    @Test
    void errors() throws Exception {
        mockMvc.perform(post("/devices/batcave").contentType(MediaType.APPLICATION_JSON).content(CCPD_BODY))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/devices/ccpd").contentType(MediaType.APPLICATION_JSON).content("not json"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/devices/ccpd").contentType(MediaType.APPLICATION_JSON).content(CCPD_BODY))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/devices/ccpd").contentType(MediaType.APPLICATION_JSON).content(CCPD_BODY))
                .andExpect(status().isConflict());
    }
}
