package org.example.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.example.dto.label.LabelRequestDto;
import org.example.dto.label.LabelResponseDto;
import org.example.model.label.Color;
import org.example.util.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LabelControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @WithMockUser(username = "john.carter", roles = "ADMIN")
    @Test
    @Sql(scripts = {
            "classpath:database/labels/add-labels-to-labels-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/labels/delete-labels-from-labels-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
           Should return all available labels
           """)
    void getLabels_elevenLabels_ReturnsAllLabels() throws Exception {
        MvcResult result = mockMvc.perform(get("/labels"))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        JsonNode root = objectMapper.readTree(json);
        JsonNode contentNode = root.get("content");

        List<LabelResponseDto> actualList = objectMapper.readValue(
                contentNode.toString(),
                objectMapper.getTypeFactory()
                        .constructParametricType(List.class, LabelResponseDto.class)
        );

        assertNotNull(actualList);
        assertEquals(11, actualList.size());
    }

    @WithMockUser(username = "john.carter", roles = "ADMIN")
    @Test
    @DisplayName("""
           Should return empty list
           """)
    void getLabels_noLabels_ReturnsEmptyList() throws Exception {
        MvcResult result = mockMvc.perform(get("/labels"))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        JsonNode root = objectMapper.readTree(json);
        JsonNode contentNode = root.get("content");

        List<LabelResponseDto> actualList = objectMapper.readValue(
                contentNode.toString(),
                objectMapper.getTypeFactory()
                        .constructParametricType(List.class, LabelResponseDto.class)
        );

        assertNotNull(actualList);
        assertEquals(0, actualList.size());
    }

    @WithMockUser(username = "john.carter", roles = "ADMIN")
    @Test
    @DisplayName("""
           Create a new Label within the specific task
           """)
    @Sql(scripts = {
            "classpath:database/labels/delete-labels-from-labels-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createLabel_validRequestDto_ReturnsCreatedLabel() throws Exception {
        LabelRequestDto requestDto = new LabelRequestDto();
        requestDto.setName("Pull Request");
        requestDto.setColor(Color.SAGE);

        LabelResponseDto expected = TestUtil.PullRequestLabelDto();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/labels")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        LabelResponseDto actual = objectMapper.readValue(result
                .getResponse()
                .getContentAsString(), LabelResponseDto.class);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual, "id")
        );
    }

    @WithMockUser(username = "john.carter", roles = "ADMIN")
    @Test
    @DisplayName("""
            Should return label with updated info
            """)
    @Sql(scripts = {
            "classpath:database/labels/add-labels-to-labels-table.sql",
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/labels/delete-labels-from-labels-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateLabelById_validRequestDto_ReturnsUpdatedLabel() throws Exception {
        LabelRequestDto requestDto = new LabelRequestDto();
        requestDto.setName("Updated Label");
        requestDto.setColor(Color.SAGE);

        LabelResponseDto expected = TestUtil.PullRequestLabelDto();
        expected.setName("Updated Label");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(put("/labels/{id}",
                        2L)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        LabelResponseDto actual = objectMapper.readValue(result
                .getResponse()
                .getContentAsString(), LabelResponseDto.class);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual, "id")
        );
    }

    @WithMockUser(username = "john.carter", roles = "ADMIN")
    @Test
    @DisplayName("""
           Should return Not Found
           """)
    void updateLabelById_invalidLabelId_ReturnsNotFound() throws Exception {
        LabelRequestDto requestDto = new LabelRequestDto();
        requestDto.setName("Updated Label");
        requestDto.setColor(Color.BANANA);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(put("/labels/{id}",
                        10L)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @WithMockUser(username = "john.carter", roles = "ADMIN")
    @Test
    @Sql(scripts = {
            "classpath:database/labels/add-labels-to-labels-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/labels/delete-labels-from-labels-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
           Delete existing comment by its id
           """)
    void deleteLabelById_existingLabel_Success() throws Exception {
        mockMvc.perform(delete("/labels/1"))
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = "john.carter", roles = "ADMIN")
    @Test
    @DisplayName("""
           Should return Not Found
           """)
    void deleteLabelById_nonExistingLabel_ReturnsNotFound() throws Exception {
        mockMvc.perform(delete("/labels/10"))
                .andExpect(status().isNotFound());
    }
}
