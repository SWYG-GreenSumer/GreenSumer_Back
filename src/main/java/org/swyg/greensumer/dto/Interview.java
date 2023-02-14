package org.swyg.greensumer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.swyg.greensumer.domain.InterviewEntity;
import org.swyg.greensumer.domain.constant.UserRole;

@Getter
@Builder
@AllArgsConstructor
public class Interview {
    private Long interviewId;
    private String writer;
    private String store;
    private String opinion;
    private UserRole target;

    public static Interview fromInterviewEntity(InterviewEntity interviewEntity) {
        return Interview.builder()
                .interviewId(interviewEntity.getId())
                .writer(interviewEntity.getWriter())
                .store(interviewEntity.getStoreName())
                .opinion(interviewEntity.getOpinion())
                .target(interviewEntity.getTarget())
                .build();
    }
}
