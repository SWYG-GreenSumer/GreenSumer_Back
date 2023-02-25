package org.swyg.greensumer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.swyg.greensumer.domain.InterviewEntity;
import org.swyg.greensumer.domain.constant.UserRole;
import org.swyg.greensumer.dto.Interview;
import org.swyg.greensumer.dto.request.InterviewCreateRequest;
import org.swyg.greensumer.dto.request.InterviewModifyRequest;
import org.swyg.greensumer.exception.ErrorCode;
import org.swyg.greensumer.exception.GreenSumerBackApplicationException;
import org.swyg.greensumer.repository.interview.InterviewCacheRepository;
import org.swyg.greensumer.repository.interview.InterviewEntityRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class InterviewService {

    private final InterviewEntityRepository interviewEntityRepository;
    private final InterviewCacheRepository interviewCacheRepository;

    public void saveInterview(InterviewCreateRequest request) {
        interviewEntityRepository.save(InterviewEntity.builder()
                .storeName(request.getStore())
                .writer(request.getWriter())
                .opinion(request.getOpinion())
                .target(UserRole.valueOf(request.getTarget()))
                .build());
    }

    public void deleteInterview(Long interviewId) {
        interviewEntityRepository.deleteById(interviewId);
    }

    @Transactional
    public void modifyInterview(Long interviewId, InterviewModifyRequest request) {
        InterviewEntity interviewEntity = interviewEntityRepository.findById(interviewId).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.INTERVIEW_NOT_FOUND, String.format("interview-%d not founded", request.getStore()));
        });

        interviewEntity.updateInterview(request.getWriter(), request.getStore(), request.getOpinion(), UserRole.valueOf(request.getTarget()));
        interviewCacheRepository.modifyInterview(interviewEntity.getId(), Interview.fromInterviewEntity(interviewEntity));
    }

    public List<Interview> getInterviews() {
        List<Interview> interviews = interviewCacheRepository.getInterviews();

        if (interviews.isEmpty()) {
            interviews.addAll(getInterviewsFromUser());
            interviews.addAll(getInterviewsFromSeller());
        }

        return interviews;
    }


    private List<Interview> getInterviewsFromUser() {
        return interviewEntityRepository.findAllByTargetOrderByIdDesc(UserRole.ROLE_USER).stream().map(Interview::fromInterviewEntity).limit(5).collect(Collectors.toList());
    }

    private List<Interview> getInterviewsFromSeller() {
        return interviewEntityRepository.findAllByTargetOrderByIdDesc(UserRole.ROLE_SELLER).stream().map(Interview::fromInterviewEntity).limit(5).collect(Collectors.toList());
    }
}
