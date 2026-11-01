package com.sctrcd.buspassws;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;

/**
 * The HTTP API, end to end through the real rules. The expected bodies are
 * the ones shown in the README.
 */
@SpringBootTest
@AutoConfigureMockMvc
class BusPassControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldIssueChildBusPassUnderSixteen() throws Exception {
        mockMvc.perform(get("/buspass").param("name", "Steve").param("age", "15"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content()
                        .json(
                                "{\"person\":{\"name\":\"Steve\",\"age\":15},\"busPassType\":\"ChildBusPass\"}",
                                JsonCompareMode.STRICT));
    }

    @Test
    void shouldIssueAdultBusPassAtSixteen() throws Exception {
        mockMvc.perform(get("/buspass").param("name", "Steve").param("age", "16"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content()
                        .json(
                                "{\"person\":{\"name\":\"Steve\",\"age\":16},\"busPassType\":\"AdultBusPass\"}",
                                JsonCompareMode.STRICT));
    }

    @Test
    void shouldEchoThePersonRequested() throws Exception {
        mockMvc.perform(get("/buspass").param("name", "Ani").param("age", "70"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.person.name").value("Ani"))
                .andExpect(jsonPath("$.person.age").value(70))
                .andExpect(jsonPath("$.busPassType").value("AdultBusPass"));
    }

    @Test
    void shouldRejectRequestWithoutName() throws Exception {
        mockMvc.perform(get("/buspass").param("age", "15")).andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectRequestWithoutAge() throws Exception {
        mockMvc.perform(get("/buspass").param("name", "Steve")).andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectNonNumericAge() throws Exception {
        mockMvc.perform(get("/buspass").param("name", "Steve").param("age", "fifteen"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldExposeActuatorHealth() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}
