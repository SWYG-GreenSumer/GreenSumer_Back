package org.swyg.greensumer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.domain.constant.UserRole;

import javax.persistence.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name = "interview")
public class InterviewEntity extends DateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String writer;          // 작성자명
    private String storeName;       // 가게 명
    private String opinion;         // 의견
    private UserRole target;          // 인터뷰 대상(0: 판매자, 1: 일반 소비자)

    public void updateInterview(String writer, String storeName, String opinion, UserRole target) {
        this.writer = writer;
        this.storeName = storeName;
        this.opinion = opinion;
        this.target = target;
    }

}
