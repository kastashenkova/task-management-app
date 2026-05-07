package org.example.controller;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.example.dto.attachment.AttachmentResponseDto;
import org.example.service.third_party.DropboxService;
import org.example.util.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AttachmentControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DropboxService dropboxService;

    @WithMockUser(username = "john.carter", roles = "ADMIN")
    @Test
    @Sql(scripts = {
            "classpath:database/projects/add-projects-to-projects-table.sql",
            "classpath:database/users/add-roles-to-roles-table.sql",
            "classpath:database/users/add-users-to-users-table.sql",
            "classpath:database/labels/add-labels-to-labels-table.sql",
            "classpath:database/tasks/add-tasks-to-tasks-table.sql",
            "classpath:database/attachments/add-attachments-to-attachments-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/attachments/delete-attachments-from-attachments-table.sql",
            "classpath:database/tasks/delete-tasks-from-tasks-table.sql",
            "classpath:database/labels/delete-labels-from-labels-table.sql",
            "classpath:database/projects/delete-projects-from-projects-table.sql",
            "classpath:database/users/delete-users-from-users-table.sql",
            "classpath:database/users/delete-roles-from-roles-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
           Should return all available attachments for the specific task
           """)
    void getAttachmentsForTask_threeAttachments_ReturnsAllAttachments() throws Exception {
        MvcResult result = mockMvc.perform(get("/attachments?taskId=3"))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        JsonNode root = objectMapper.readTree(json);
        JsonNode contentNode = root.get("content");

        List<AttachmentResponseDto> actualList = objectMapper.readValue(
                contentNode.toString(),
                objectMapper.getTypeFactory()
                        .constructParametricType(List.class, AttachmentResponseDto.class)
        );

        assertNotNull(actualList);
        assertEquals(3, actualList.size());
    }

    @WithMockUser(username = "john.carter", roles = "ADMIN")
    @Test
    @Sql(scripts = {
            "classpath:database/projects/add-projects-to-projects-table.sql",
            "classpath:database/users/add-roles-to-roles-table.sql",
            "classpath:database/users/add-users-to-users-table.sql",
            "classpath:database/labels/add-labels-to-labels-table.sql",
            "classpath:database/tasks/add-tasks-to-tasks-table.sql",
            "classpath:database/attachments/add-attachments-to-attachments-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/attachments/delete-attachments-from-attachments-table.sql",
            "classpath:database/tasks/delete-tasks-from-tasks-table.sql",
            "classpath:database/labels/delete-labels-from-labels-table.sql",
            "classpath:database/projects/delete-projects-from-projects-table.sql",
            "classpath:database/users/delete-users-from-users-table.sql",
            "classpath:database/users/delete-roles-from-roles-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
           Should return empty list
           """)
    void getAttachmentsForTask_noAttachments_ReturnsEmptyList() throws Exception {
        MvcResult result = mockMvc.perform(get("/attachments?taskId=4"))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        JsonNode root = objectMapper.readTree(json);
        JsonNode contentNode = root.get("content");

        List<AttachmentResponseDto> actualList = objectMapper.readValue(
                contentNode.toString(),
                objectMapper.getTypeFactory()
                        .constructParametricType(List.class, AttachmentResponseDto.class)
        );

        assertNotNull(actualList);
        assertEquals(0, actualList.size());
    }

    @WithMockUser(username = "john.carter", roles = "ADMIN")
    @Test
    @DisplayName("""
           Create a new Attachment within the specific task
           """)
    @Sql(scripts = {
            "classpath:database/projects/add-projects-to-projects-table.sql",
            "classpath:database/users/add-roles-to-roles-table.sql",
            "classpath:database/users/add-users-to-users-table.sql",
            "classpath:database/labels/add-labels-to-labels-table.sql",
            "classpath:database/tasks/add-tasks-to-tasks-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/attachments/delete-attachments-from-attachments-table.sql",
            "classpath:database/tasks/delete-tasks-from-tasks-table.sql",
            "classpath:database/labels/delete-labels-from-labels-table.sql",
            "classpath:database/projects/delete-projects-from-projects-table.sql",
            "classpath:database/users/delete-users-from-users-table.sql",
            "classpath:database/users/delete-roles-from-roles-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createAttachment_validRequestDto_ReturnsCreatedAttachment() throws Exception {
        byte[] fileContent = "Test Content".getBytes();
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                fileContent
        );

        // in order to avoid real Dropbox connection
        when(dropboxService.uploadFile(any(MultipartFile.class)))
                .thenReturn("dbx_file_test-id");

        MvcResult result = mockMvc.perform(
                multipart("/attachments?taskId=1")
                        .file(mockFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isCreated())
                .andReturn();

        AttachmentResponseDto actual = objectMapper.readValue(result
                .getResponse()
                .getContentAsString(), AttachmentResponseDto.class);

        AttachmentResponseDto expected = TestUtil.TestAttachmentDto();

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertTrue(
                EqualsBuilder.reflectionEquals(expected, actual, "id",
                        "uploadDate")
        );
    }

    @WithMockUser(username = "john.carter", roles = "ADMIN")
    @Test
    @Sql(scripts = {
            "classpath:database/projects/add-projects-to-projects-table.sql",
            "classpath:database/users/add-roles-to-roles-table.sql",
            "classpath:database/users/add-users-to-users-table.sql",
            "classpath:database/labels/add-labels-to-labels-table.sql",
            "classpath:database/tasks/add-tasks-to-tasks-table.sql",
            "classpath:database/attachments/add-attachments-to-attachments-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/attachments/delete-attachments-from-attachments-table.sql",
            "classpath:database/tasks/delete-tasks-from-tasks-table.sql",
            "classpath:database/labels/delete-labels-from-labels-table.sql",
            "classpath:database/projects/delete-projects-from-projects-table.sql",
            "classpath:database/users/delete-users-from-users-table.sql",
            "classpath:database/users/delete-roles-from-roles-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
                Should return attachment by its id
                """)
    void retrieveAttachment_existingAttachment_ReturnsAttachment() throws Exception {
        byte[] expectedContent = "Test Content".getBytes();

        // in order to avoid real Dropbox connection
        when(dropboxService.downloadFile(anyString()))
                .thenReturn(expectedContent);

        MvcResult result = mockMvc.perform(get("/attachments/1"))
                .andExpect(status().isOk())
                .andReturn();

        byte[] actualContent = result.getResponse().getContentAsByteArray();
        assertArrayEquals(expectedContent, actualContent);
    }

    @WithMockUser(username = "john.carter", roles = "ADMIN")
    @Test
    @DisplayName("""
            Should return Not Found
            """)
    void retrieveAttachment_nonExistingAttachment_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/attachments/10"))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "john.carter", roles = "ADMIN")
    @Test
    @Sql(scripts = {
            "classpath:database/projects/add-projects-to-projects-table.sql",
            "classpath:database/users/add-roles-to-roles-table.sql",
            "classpath:database/users/add-users-to-users-table.sql",
            "classpath:database/labels/add-labels-to-labels-table.sql",
            "classpath:database/tasks/add-tasks-to-tasks-table.sql",
            "classpath:database/attachments/add-attachments-to-attachments-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/attachments/delete-attachments-from-attachments-table.sql",
            "classpath:database/tasks/delete-tasks-from-tasks-table.sql",
            "classpath:database/labels/delete-labels-from-labels-table.sql",
            "classpath:database/projects/delete-projects-from-projects-table.sql",
            "classpath:database/users/delete-users-from-users-table.sql",
            "classpath:database/users/delete-roles-from-roles-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
           Delete existing attachment by its id
           """)
    void deleteAttachment_existingAttachment_Success() throws Exception {
        // in order to avoid real Dropbox connection
        doNothing().when(dropboxService).deleteFile(anyString());
        mockMvc.perform(delete("/attachments/1"))
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = "john.carter", roles = "ADMIN")
    @Test
    @DisplayName("""
           Should return Not Found
           """)
    void deleteAttachment_nonExistingAttachment_ReturnsNotFound() throws Exception {
        mockMvc.perform(delete("/attachments/10"))
                .andExpect(status().isNotFound());
    }
}
