package com.learntrad.microservices.marketdata.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learntrad.microservices.marketdata.model.request.AnswerRequest;
import com.learntrad.microservices.marketdata.model.request.QuizRequest;
import com.learntrad.microservices.marketdata.model.response.QuizResponse;
import com.learntrad.microservices.marketdata.service.intrface.QuizService;
import com.learntrad.microservices.shared.annotation.RequireRoles;
import com.learntrad.microservices.shared.constant.ApiBash;
import com.learntrad.microservices.shared.constant.ConstantBash;
import com.learntrad.microservices.shared.model.response.CommonResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiBash.MARKET_DATA + ApiBash.QUIZ)
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @GetMapping
    @RequireRoles({ConstantBash.HAS_ROLE_ADMIN})
    public ResponseEntity<CommonResponse<List<QuizResponse>>> getAll() {
        List<QuizResponse> quizResponse = quizService.getAll();
        CommonResponse<List<QuizResponse>> response = CommonResponse.<List<QuizResponse>>builder()
                .message(ApiBash.FETCH_QUIZ_SUCCESS)
                .status(HttpStatus.OK.value())
                .data(quizResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @RequireRoles({ConstantBash.HAS_ROLE_ADMIN})
    public ResponseEntity<CommonResponse<QuizResponse>> getById(@PathVariable("id") String id) {
        QuizResponse quizResponse = quizService.getQuizById(id);
        CommonResponse<QuizResponse> response = CommonResponse.<QuizResponse>builder()
                .message(ApiBash.FETCH_QUIZ_SUCCESS)
                .status(HttpStatus.OK.value())
                .data(quizResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/generate")
    @RequireRoles({ConstantBash.HAS_ROLE_CUSTOMER, ConstantBash.HAS_ROLE_ADMIN})
    public ResponseEntity<CommonResponse<QuizResponse>> generateQuiz(HttpServletRequest request, @RequestBody @Valid QuizRequest quizRequest) {
        QuizResponse quizResponse = quizService.generateQuiz(request.getHeader("Authorization"), quizRequest);
        CommonResponse<QuizResponse> response = CommonResponse.<QuizResponse>builder()
                .message(ApiBash.GENERATE_QUIZ_SUCCESS)
                .status(HttpStatus.CREATED.value())
                .data(quizResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/mine")
    @RequireRoles({ConstantBash.HAS_ROLE_CUSTOMER, ConstantBash.HAS_ROLE_ADMIN})
    public ResponseEntity<CommonResponse<List<QuizResponse>>> getMine(HttpServletRequest request) {
        List<QuizResponse> quizResponse = quizService.getAllMine(request.getHeader("Authorization"));
        CommonResponse<List<QuizResponse>> response = CommonResponse.<List<QuizResponse>>builder()
                .message(ApiBash.FETCH_QUIZ_SUCCESS)
                .status(HttpStatus.OK.value())
                .data(quizResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/mine/{id}")
    @RequireRoles({ConstantBash.HAS_ROLE_CUSTOMER, ConstantBash.HAS_ROLE_ADMIN})
    public ResponseEntity<CommonResponse<QuizResponse>> getMineById(HttpServletRequest request, @PathVariable("id") String id) {
        QuizResponse quizResponse = quizService.getMineById(request.getHeader("Authorization"), id);
        CommonResponse<QuizResponse> response = CommonResponse.<QuizResponse>builder()
                .message(ApiBash.FETCH_QUIZ_SUCCESS)
                .status(HttpStatus.OK.value())
                .data(quizResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/mine/{id}/answer")
    @RequireRoles({ConstantBash.HAS_ROLE_CUSTOMER, ConstantBash.HAS_ROLE_ADMIN})
    public ResponseEntity<CommonResponse<QuizResponse>> answer(HttpServletRequest request, @PathVariable("id") String id, @RequestBody @Valid AnswerRequest quizRequest) {
        QuizResponse quizResponse = quizService.answerQuiz(request.getHeader("Authorization"), id, quizRequest);
        CommonResponse<QuizResponse> response = CommonResponse.<QuizResponse>builder()
                .message(ApiBash.ANSWER_QUIZ_SUCCESS)
                .status(HttpStatus.OK.value())
                .data(quizResponse)
                .build();
        return ResponseEntity.ok(response);
    }

}
