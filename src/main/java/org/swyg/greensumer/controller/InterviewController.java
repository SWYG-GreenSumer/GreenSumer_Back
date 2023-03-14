package org.swyg.greensumer.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.swyg.greensumer.dto.Interview;
import org.swyg.greensumer.dto.request.InterviewCreateRequest;
import org.swyg.greensumer.dto.request.InterviewModifyRequest;
import org.swyg.greensumer.dto.response.InterviewResponse;
import org.swyg.greensumer.dto.response.Response;
import org.swyg.greensumer.service.InterviewService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/interviews")
public class InterviewController {

    private final InterviewService interviewService;

    @GetMapping("/test")
    public Response<Void> test() {
        return Response.success();
    }

    @PostMapping
    public Response<Void> saveInterview(@RequestBody InterviewCreateRequest request, Authentication authentication) {
        interviewService.saveInterview(request);
        return Response.success();
    }

    @DeleteMapping("/{interviewId}")
    public Response<Void> deleteInterview(@PathVariable Long interviewId, Authentication authentication) {
        interviewService.deleteInterview(interviewId);
        return Response.success();
    }

    @PutMapping("/{interviewId}")
    public Response<Void> modifyInterview(@PathVariable Long interviewId, @RequestBody InterviewModifyRequest request, Authentication authentication) {
        interviewService.modifyInterview(interviewId, request);
        return Response.success();
    }

    @GetMapping
    public Response<InterviewResponse> getInterviews() {
        List<Interview> interviews = interviewService.getInterviews();
        return Response.success(InterviewResponse.of(interviews));
    }
}
