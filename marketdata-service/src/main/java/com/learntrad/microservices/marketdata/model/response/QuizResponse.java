package com.learntrad.microservices.marketdata.model.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizResponse {
    private String id;
    private String userId;
    private String nSize;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private AnswerResponse answer;
    private Long dataCount;
    private List<?> quizMarketData;
}
