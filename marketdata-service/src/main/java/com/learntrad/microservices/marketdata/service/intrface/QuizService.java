package com.learntrad.microservices.marketdata.service.intrface;

import java.util.List;

import com.learntrad.microservices.marketdata.model.request.AnswerRequest;
import com.learntrad.microservices.marketdata.model.request.QuizRequest;
import com.learntrad.microservices.marketdata.model.response.QuizResponse;

public interface QuizService {
    QuizResponse generateQuiz(String authHeader, QuizRequest quizRequest);
    QuizResponse getMineById(String authHeader, String id);
    QuizResponse getQuizById(String id);
    List<QuizResponse> getAll();
    List<QuizResponse> getAllMine(String authHeader);
    QuizResponse answerQuiz(String authHeader, String id, AnswerRequest answerRequest);
}
