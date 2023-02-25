package org.swyg.greensumer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.swyg.greensumer.dto.request.InterviewCreateRequest;
import org.swyg.greensumer.dto.request.InterviewModifyRequest;
import org.swyg.greensumer.service.InterviewService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willReturn;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.swyg.greensumer.fixture.Fixtures.interviews;
import static org.swyg.greensumer.fixture.RequestFixture.InterviewCreateRequest;
import static org.swyg.greensumer.fixture.RequestFixture.InterviewModifyRequest;

@DisplayName("View 컨트롤러 - 이미지 등록")
@ActiveProfiles("test")
@MockBean({JpaMetamodelMappingContext.class})
@WebMvcTest(InterviewController.class)
class InterviewControllerTest {

    private final MockMvc mvc;
    private final ObjectMapper objectMapper;

    @MockBean private InterviewService interviewService;

    public InterviewControllerTest(
            @Autowired MockMvc mvc,
            @Autowired ObjectMapper objectMapper
    ) {
        this.mvc = mvc;
        this.objectMapper = objectMapper;
    }

    @DisplayName("[view][POST] 인터뷰 등록 - 정상호출")
    @WithMockUser
    @Test
    void givenInterview_whenRequestingSaveInterview_thenSaveInterview() throws Exception {
        // Given
        willDoNothing().given(interviewService).saveInterview(any(InterviewCreateRequest.class));

        // When & Then
        mvc.perform(post("/api/interviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(InterviewCreateRequest()))
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @DisplayName("[view][POST] 인터뷰 등록 - 권한이 없는 경우")
    @WithAnonymousUser
    @Test
    void givenInterview_whenRequestingSaveInterview_thenReturnThrowUnAuthorizeException() throws Exception {
        // Given
        willDoNothing().given(interviewService).saveInterview(any(InterviewCreateRequest.class));

        // When & Then
        mvc.perform(post("/api/interviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(InterviewCreateRequest()))
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][DELETE] 인터뷰 삭제 - 정상호출")
    @WithMockUser
    @Test
    void givenInterview_whenRequestingDeleteInterview_thenDeleteInterview() throws Exception {
        // Given
        willDoNothing().given(interviewService).deleteInterview(any());

        // When & Then
        mvc.perform(delete("/api/interviews/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @DisplayName("[view][DELETE] 인터뷰 삭제 - 권한이 없는 경우")
    @WithAnonymousUser
    @Test
    void givenInterview_whenRequestingDeleteInterview_thenReturnThrowUnAuthorizeException() throws Exception {
        // Given
        willDoNothing().given(interviewService).deleteInterview(any());

        // When & Then
        mvc.perform(delete("/api/interviews/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][PUT] 인터뷰 수정 - 정상 호출")
    @WithMockUser
    @Test
    void givenInterviewId_whenRequestingModifyInterview_thenReturnNothing() throws Exception {
        // Given
        willDoNothing().given(interviewService).modifyInterview(any(), any(InterviewModifyRequest.class));

        // When & Then
        mvc.perform(put("/api/interviews/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(InterviewModifyRequest()))
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @DisplayName("[view][PUT] 인터뷰 수정 - 정상 호출")
    @WithAnonymousUser
    @Test
    void givenInterviewId_whenRequestingModifyInterview_thenThrowUnAuthorizeException() throws Exception {
        // Given
        willDoNothing().given(interviewService).modifyInterview(any(), any(InterviewModifyRequest.class));

        // When & Then
        mvc.perform(put("/api/interviews/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(InterviewModifyRequest()))
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][GET] 인터뷰 조회 - 정상 호출")
    @WithMockUser
    @Test
    void givenNothing_whenRequestingInterviews_thenReturnListInterview() throws Exception {
        // Given
        willReturn(interviews()).given(interviewService).getInterviews();

        // When & Then
        mvc.perform(get("/api/interviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

}