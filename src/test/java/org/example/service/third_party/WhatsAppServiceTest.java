package org.example.service.third_party;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.example.dto.task.TaskResponseDto;
import org.example.model.label.Label;
import org.example.model.task.Task;
import org.example.model.user.User;
import org.example.util.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
public class WhatsAppServiceTest {

    @InjectMocks
    private WhatsAppService whatsAppService;

    @Mock
    private RestTemplate restTemplate;

    @Test
    @DisplayName("""
            Should send task assignment to the logged in user
            """)
    public void sendTaskAssignmentWhatsApp_validInput_SendsTaskAssignment() {
        TaskResponseDto addPaymentCountryTaskDto = TestUtil.AddPaymentCountryTaskDto();
        User mockAssignee = TestUtil.John()
                .setId(addPaymentCountryTaskDto.getAssigneeId());
        Label mockLabel = new Label()
                .setId(addPaymentCountryTaskDto.getLabelId());
        Task mockTask = TestUtil.AddPaymentCountryTask()
                .setAssignee(mockAssignee)
                .setLabel(mockLabel);;

        String mockCalendarEventUrl = "mockCalendarEventUrl";

        // values
        ReflectionTestUtils.setField(whatsAppService, "apiUrl", "https://mock-api.com");
        ReflectionTestUtils.setField(whatsAppService, "phoneNumberId", "12345");
        ReflectionTestUtils.setField(whatsAppService, "accessToken", "mockToken");

        ResponseEntity<String> mockResponse = new ResponseEntity<>(HttpStatus.OK);

        doReturn(mockResponse)
                .when(restTemplate)
                .postForEntity(anyString(), any(HttpEntity.class), eq(String.class));

        whatsAppService.sendTaskAssignmentWhatsApp(mockTask, mockAssignee, mockCalendarEventUrl);

        verify(restTemplate, times(1))
                .postForEntity(anyString(), any(HttpEntity.class),  eq(String.class));
    }

    @Test
    @DisplayName("""
            Should return Illegal Argument Exception
            """)
    public void sendTaskAssignmentWhatsApp_incorrectPhoneNumber_ReturnsIllegalArgumentException() {
        TaskResponseDto addPaymentCountryTaskDto = TestUtil.AddPaymentCountryTaskDto();
        User mockAssignee = TestUtil.John()
                .setId(addPaymentCountryTaskDto.getAssigneeId())
                .setPhoneNumber(" ");
        Label mockLabel = new Label()
                .setId(addPaymentCountryTaskDto.getLabelId());
        Task mockTask = TestUtil.AddPaymentCountryTask()
                .setAssignee(mockAssignee)
                .setLabel(mockLabel);;

        String mockCalendarEventUrl = "mockCalendarEventUrl";

        // values
        ReflectionTestUtils.setField(whatsAppService, "apiUrl", "https://mock-api.com");
        ReflectionTestUtils.setField(whatsAppService, "phoneNumberId", "12345");
        ReflectionTestUtils.setField(whatsAppService, "accessToken", "mockToken");

        assertThrows(IllegalArgumentException.class,
                () -> whatsAppService.sendTaskAssignmentWhatsApp(
                        mockTask,
                        mockAssignee,
                        mockCalendarEventUrl));
    }

    @Test
    @DisplayName("""
            Should return RuntimeException
            """)
    public void sendTaskAssignmentWhatsApp_whatsAppError_ReturnsRuntimeException() {
        TaskResponseDto addPaymentCountryTaskDto = TestUtil.AddPaymentCountryTaskDto();
        User mockAssignee = TestUtil.John()
                .setId(addPaymentCountryTaskDto.getAssigneeId());
        Label mockLabel = new Label()
                .setId(addPaymentCountryTaskDto.getLabelId());
        Task mockTask = TestUtil.AddPaymentCountryTask()
                .setAssignee(mockAssignee)
                .setLabel(mockLabel);

        String mockCalendarEventUrl = "mockCalendarEventUrl";

        // values
        ReflectionTestUtils.setField(whatsAppService, "apiUrl", "https://mock-api.com");
        ReflectionTestUtils.setField(whatsAppService, "phoneNumberId", "12345");
        ReflectionTestUtils.setField(whatsAppService, "accessToken", "mockToken");

        ResponseEntity<String> mockResponse = new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        doReturn(mockResponse)
                .when(restTemplate)
                .postForEntity(anyString(), any(HttpEntity.class), eq(String.class));

        assertThrows(RuntimeException.class,
                () -> whatsAppService.sendTaskAssignmentWhatsApp(
                        mockTask,
                        mockAssignee,
                        mockCalendarEventUrl));
    }
}
