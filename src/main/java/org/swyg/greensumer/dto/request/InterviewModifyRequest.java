package org.swyg.greensumer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.swyg.greensumer.domain.constant.UserRole;

@Getter
@AllArgsConstructor
public class InterviewModifyRequest {
    private int interviewId;
    private String writer;
    private String store;
    private String opinion;
    private UserRole target;
}
