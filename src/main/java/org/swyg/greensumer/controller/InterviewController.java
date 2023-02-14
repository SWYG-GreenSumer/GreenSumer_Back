package org.swyg.greensumer.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @PostMapping("/save")
    public Response<Void> saveInterview(@RequestBody InterviewCreateRequest request) {
        interviewService.saveInterview(request);

        return Response.success();
    }

    @PostMapping("/saves")
    public Response<Void> saveInterviews(@RequestBody List<InterviewCreateRequest> requests) {
        interviewService.saveInterviews(requests);

        return Response.success();
    }

    @DeleteMapping("/{interviewId}")
    public Response<Void> deleteInterview(@PathVariable Long interviewId) {
        interviewService.deleteInterview(interviewId);
        return Response.success();
    }

    @PutMapping
    public Response<Void> modifyInterview(@RequestBody InterviewModifyRequest request) {
        interviewService.modifyInterview(request);
        return Response.success();
    }

    @GetMapping
    public Response<InterviewResponse> getInterviews() {
        List<Interview> interviews = interviewService.getInterviews();
        return Response.success(InterviewResponse.of(interviews));
    }
}
