package com.learntrad.microservices.marketdata.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.util.Tuple;

import com.learntrad.microservices.marketdata.entity.QuizEntity;
import com.learntrad.microservices.marketdata.entity.XauusdEntity;
import com.learntrad.microservices.marketdata.model.request.AnswerRequest;
import com.learntrad.microservices.marketdata.model.request.QuizRequest;
import com.learntrad.microservices.marketdata.model.request.search.SearchMarketDataRequest;
import com.learntrad.microservices.marketdata.model.response.AnswerResponse;
import com.learntrad.microservices.marketdata.model.response.DataOfMarketData;
import com.learntrad.microservices.marketdata.model.response.QuizResponse;
import com.learntrad.microservices.marketdata.repository.QuizRepository;
import com.learntrad.microservices.marketdata.repository.XauusdRepository;
import com.learntrad.microservices.marketdata.service.intrface.MarketDataService;
import com.learntrad.microservices.marketdata.service.intrface.QuizService;
import com.learntrad.microservices.shared.constant.ConstantBash;
import com.learntrad.microservices.shared.constant.DbBash;
import com.learntrad.microservices.shared.constant.enumerated.EMarketDataType;
import com.learntrad.microservices.shared.constant.enumerated.ENSize;
import com.learntrad.microservices.shared.constant.enumerated.ETimeFrame;
import com.learntrad.microservices.shared.jwt.JwtClaim;
import com.learntrad.microservices.shared.jwt.JwtUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizServiceImpl implements QuizService {

    private final XauusdRepository xauusdRepository;
    private final QuizRepository quizRepository;
    private final MarketDataService marketDataService;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public QuizResponse generateQuiz(String authHeader, QuizRequest quizRequest) {
        try {
            JwtClaim jwtClaim = JwtUtil.getClaims(authHeader);

            log.info("Start - Generating quiz");
            EMarketDataType[] marketDataTypes = EMarketDataType.values();
            Random randomizer = new Random();

            ETimeFrame[] filteredTimeFrames = Arrays.stream(ETimeFrame.values())
                .filter(tf -> tf != ETimeFrame.THREE_DAY)
                .filter(tf -> tf != ETimeFrame.THREE_DAY)
                .filter(tf -> tf != ETimeFrame.ONE_WEEK)
                .filter(tf -> tf != ETimeFrame.ONE_MONTH)
                .toArray(ETimeFrame[]::new);

            ENSize nSize = ENSize.findByDescription(quizRequest.getNSize());
            
            EMarketDataType randomMarketDataType = marketDataTypes[randomizer.nextInt(marketDataTypes.length)];
            ETimeFrame randomTimeFrame = filteredTimeFrames[randomizer.nextInt(filteredTimeFrames.length)];

            switch(randomMarketDataType) {
                case XAUUSD:

                    log.info("Start - Finding min and max time bucket start");
                    Optional<XauusdEntity> minTimeBucketStart = xauusdRepository.findTopByOrderByTimeBucketStartAsc();
                    Optional<XauusdEntity> maxTimeBucketStart = xauusdRepository.findTopByOrderByTimeBucketStartDesc();

                    Instant min = minTimeBucketStart.get().getTimeBucketStart();
                    Instant max = maxTimeBucketStart.get().getTimeBucketStart()
                        .minus(Duration.ofMinutes(randomTimeFrame.getInMinutes() * (nSize.getSize() * 2)));

                    log.info("End - Finding min and max time bucket start, min: {}, max: {}", min, max);

                    long minMillis = min.toEpochMilli();
                    long maxMillis = max.toEpochMilli();

                    long randomMillis = minMillis + (long) (randomizer.nextDouble() * (maxMillis - minMillis));

                    Instant randomInstant = Instant.ofEpochMilli(randomMillis);
                    log.info("Chosen random time bucket start: {}", randomInstant);

                    Instant timeBucketStartMax = randomInstant.plus(randomTimeFrame.getInMinutes() * nSize.getSize(), ChronoUnit.MINUTES);
                    log.info("Chosen time bucket start max: {}", timeBucketStartMax);
                    SearchMarketDataRequest searchMarketDataRequest = SearchMarketDataRequest.builder()
                        .timeBucketStartMin(randomInstant)
                        .timeBucketStartMax(timeBucketStartMax)
                        .timeFrame(randomTimeFrame.getDescription())
                        .direction("asc")
                        .build();
                    List<?> data = marketDataService.fetchMarketData(randomMarketDataType.getDescription(), searchMarketDataRequest).getMarketData();
                    log.info("Got FIRST DATA : {}, LAST DATA : {}", data.getFirst(), data.getLast());

                    log.info("Start - Calculating scale and vertical translation");
                    BigDecimal highestHigh = xauusdRepository.findTopByHighBetween(randomInstant, timeBucketStartMax).get().getHigh();
                    BigDecimal lowestLow = xauusdRepository.findTopByLowBetween(randomInstant, timeBucketStartMax).get().getLow();
                    log.info("Got highest high {} and lowest low {}", highestHigh, lowestLow);

                    BigDecimal oldDataRange = highestHigh.subtract(lowestLow);
                    log.info("data range: {}", oldDataRange);
                    BigDecimal scale = BigDecimal.valueOf(1000.0).divide(oldDataRange, 3, RoundingMode.HALF_UP);
                    log.info("scale: {}", scale);

                    BigDecimal verticalTranslation = lowestLow.negate();
                    log.info("End - Calculating scale and vertical translation. highestHigh: {}, lowestLow: {}, oldDataRange: {}, scale: {}, verticalTranslation: {}", highestHigh, lowestLow, oldDataRange, scale, verticalTranslation);

                    List<?> transformedData = getTransformedData(data, scale, verticalTranslation);

                    QuizEntity entity = QuizEntity.builder()
                                .userId(jwtClaim.getUserId())
                                .timeBucketStart(randomInstant)
                                .nSize(nSize)
                                .timeFrame(randomTimeFrame)
                                .marketDataType(randomMarketDataType)
                                .verticalTranslation(verticalTranslation)
                                .scale(scale)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(null)
                                .build();

                    log.info("Start - Saving new quiz data to db: {}", entity);
                    QuizEntity savedEntity = quizRepository.save(entity);
                    log.info("End - Saving new quiz data to db: {}", savedEntity);
                    log.info("End - Generating quiz");
                    return toQuizResponse(savedEntity, transformedData, null);
                default:
                    throw new RuntimeException("Unsupported market data type: " + randomMarketDataType);
            }

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public QuizResponse getMineById(String authHeader, String id) {
        try {
            JwtClaim jwtClaim = JwtUtil.getClaims(authHeader);
            Optional<QuizEntity> optional = quizRepository.findByUserIdAndId(jwtClaim.getUserId(), id);
            if (!optional.isPresent()) {
                log.error("ERROR - " + DbBash.QUIZ_NOT_FOUND + " OR " + DbBash.QUIZ_UNAUTHORIZED);
                throw new RuntimeException(DbBash.QUIZ_NOT_FOUND + " OR " + DbBash.QUIZ_UNAUTHORIZED);
            }
            return toQuizResponse(optional.get(), null, toAnswerResponse(optional.get(), null)); // harusnya ada market datanya
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public QuizResponse getQuizById(String id) {
        try {
            log.info("Start - Getting quiz by id: {}", id);
            Optional<QuizEntity> optional = quizRepository.findById(id);
            if (!optional.isPresent()) {
                log.error("ERROR - " + DbBash.QUIZ_NOT_FOUND);
                throw new RuntimeException(DbBash.QUIZ_NOT_FOUND);
            }
            log.info("End - Getting quiz by id: {}", id);
            return toQuizResponse(optional.get(), null, toAnswerResponse(optional.get(), null)); // harusnya ada market datanya
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<QuizResponse> getAll() {
        try {
            log.info("Start - Getting all quizzes");
            List<QuizEntity> entities = quizRepository.findAll();
            log.info("End - Getting all quizzes");
            return entities.stream().map(entity -> toQuizResponse(entity, null, toAnswerResponse(entity, null))).collect(Collectors.toList());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<QuizResponse> getAllMine(String authHeader) {
        try {
            JwtClaim jwtClaim = JwtUtil.getClaims(authHeader);
            log.info("Start - Getting all quizzes for user: {}", jwtClaim.getUserId());
            List<QuizEntity> entities = quizRepository.findAllByUserId(jwtClaim.getUserId());
            log.info("End - Getting all quizzes for user: {}", jwtClaim.getUserId());
            return entities.stream().map(entity -> toQuizResponse(entity, null, toAnswerResponse(entity, null))).collect(Collectors.toList());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public QuizResponse answerQuiz(String authHeader, String id, AnswerRequest answerRequest) {
        try {
            log.info("Start - Answering quiz: {}", id);
            validateAnswerRequest(answerRequest);

            Optional<QuizEntity> entityOptional = quizRepository.findById(id);

            if (!entityOptional.isPresent()) {
                log.error("ERROR - " + DbBash.QUIZ_NOT_FOUND);
                throw new RuntimeException(DbBash.QUIZ_NOT_FOUND);
            }

            QuizEntity entity = entityOptional.get();
            if (!entity.getUserId().equals(JwtUtil.getClaims(authHeader).getUserId())) {
                log.error("ERROR - " + DbBash.QUIZ_UNAUTHORIZED);
                throw new RuntimeException(DbBash.QUIZ_UNAUTHORIZED);
            }
            if (entity.getResult() != null) {
                log.error("ERROR - " + ConstantBash.QUIZ_ALREADY_ANSWERED);
                throw new RuntimeException(ConstantBash.QUIZ_ALREADY_ANSWERED);
            }

            Instant quizTimeBucketStartMin = entity.getTimeBucketStart();
            Instant quizTimeBucketStartMax = quizTimeBucketStartMin.plus(entity.getTimeFrame().getInMinutes() * entity.getNSize().getSize(), ChronoUnit.MINUTES);
            log.info("Chosen quiz time bucket start max: {}", quizTimeBucketStartMax);

            Instant answerTimeBucketStartMin = quizTimeBucketStartMax;
            Instant answerTimeBucketStartMax = answerTimeBucketStartMin.plus(entity.getTimeFrame().getInMinutes() * entity.getNSize().getSize(), ChronoUnit.MINUTES);
            log.info("Chosen time bucket start max: {}", answerTimeBucketStartMax);

            BigDecimal translatedHighestHigh;
            BigDecimal translatedLowestLow;
            switch (entity.getMarketDataType()) {
                case XAUUSD:
                    XauusdEntity xauusdHighestHigh = xauusdRepository.findTopByHighBetween(answerTimeBucketStartMin, answerTimeBucketStartMax).get();
                    translatedHighestHigh = xauusdHighestHigh.getHigh().add(entity.getVerticalTranslation());

                    XauusdEntity xauusdLowestLow = xauusdRepository.findTopByLowBetween(answerTimeBucketStartMin, answerTimeBucketStartMax).get();
                    translatedLowestLow = xauusdLowestLow.getLow().add(entity.getVerticalTranslation());
                    log.info("Got highest high: {}, lowest low: {}, for {}", translatedHighestHigh, translatedLowestLow, entity.getMarketDataType().getDescription());
                    break;
                default:
                    throw new RuntimeException("Unsupported market data type: " + entity.getMarketDataType().getDescription());
            }

            BigDecimal transformedHighestHigh = translatedHighestHigh.multiply(entity.getScale());
            BigDecimal transformedLowestLow = translatedLowestLow.multiply(entity.getScale());

            SearchMarketDataRequest searchMarketDataRequest = SearchMarketDataRequest.builder()
                .timeBucketStartMin(answerTimeBucketStartMin)
                .timeBucketStartMax(answerTimeBucketStartMax)
                .timeFrame(entity.getTimeFrame().getDescription())
                .direction("asc")
                .build();

            List<?> data = marketDataService.fetchMarketData(entity.getMarketDataType().getDescription(), searchMarketDataRequest).getMarketData();

            Tuple<BigDecimal, List<?>> tuple = transformDataAndCalculateResult(
                data,
                transformedHighestHigh,
                transformedLowestLow,
                answerRequest,
                entity.getScale(),
                entity.getVerticalTranslation()
            );

            entity.setResult(tuple._1());
            entity.setPriceAt(answerRequest.getPriceAt());
            entity.setTakeProfitAt(answerRequest.getTakeProfitAt());
            entity.setStopLossAt(answerRequest.getStopLossAt());
            entity.setUpdatedAt(LocalDateTime.now());

            log.info("Start - Saving entity: {}", entity);
            quizRepository.save(entity);
            log.info("End - Saving entity: {}", entity);
            log.info("End - Answering quiz: {}", id);
            return toQuizResponse(entity, null, toAnswerResponse(entity, tuple._2()));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private List<?> getTransformedData(List<?> data, BigDecimal scale, BigDecimal verticalTranslation) {
        log.info("Start - Transforming data");
        List<?> transformedData = data.stream()
            .map(d -> {
                BigDecimal high;
                try {
                    high = (BigDecimal) d.getClass().getMethod("getHigh").invoke(d);
                    BigDecimal low = (BigDecimal) d.getClass().getMethod("getLow").invoke(d);
                    BigDecimal open = (BigDecimal) d.getClass().getMethod("getOpen").invoke(d);
                    BigDecimal close = (BigDecimal) d.getClass().getMethod("getClosed").invoke(d);

                    BigDecimal transformedHigh = high.add(verticalTranslation).multiply(scale);
                    BigDecimal transformedLow = low.add(verticalTranslation).multiply(scale);
                    BigDecimal transformedOpen = open.add(verticalTranslation).multiply(scale);
                    BigDecimal transformedClose = close.add(verticalTranslation).multiply(scale);

                    return new DataOfMarketData(
                        (Instant) d.getClass().getMethod("getTimeBucketStart").invoke(d),
                        transformedOpen,
                        transformedHigh,
                        transformedLow,
                        transformedClose,
                        Long.valueOf(0)
                    );
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException
                        | SecurityException e) {
                    e.printStackTrace();
                    log.error(e.getMessage());
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toList());
        log.info("End - Transforming data");
        return transformedData;
    }
    private void validateAnswerRequest(AnswerRequest answerRequest) {
        try {
            log.info("Start - Validating answer request: {}", answerRequest);

            if (answerRequest.getPriceAt().compareTo(answerRequest.getStopLossAt()) == 0) {
                throw new RuntimeException(ConstantBash.INVALID_QUIZ_PRICE_AT_AND_STOPLOSS_AT);
            }
            if (answerRequest.getPriceAt().compareTo(answerRequest.getTakeProfitAt()) == 0) {
                throw new RuntimeException(ConstantBash.INVALID_QUIZ_PRICE_AT_AND_TAKE_PROFIT_AT);
            }
            if (answerRequest.getStopLossAt().compareTo(answerRequest.getTakeProfitAt()) == 0) {
                throw new RuntimeException(ConstantBash.INVALID_QUIZ_STOPLOSS_AT_AND_TAKE_PROFIT_AT);
            }

            if (answerRequest.getPriceAt().compareTo(answerRequest.getTakeProfitAt()) < 0) {
                if (answerRequest.getTakeProfitAt().compareTo(answerRequest.getPriceAt()) < 0 || 
                    answerRequest.getStopLossAt().compareTo(answerRequest.getPriceAt()) > 0
                ) {
                    throw new RuntimeException(ConstantBash.getBuyInvalidMessage(answerRequest.getPriceAt(), answerRequest.getStopLossAt(), answerRequest.getTakeProfitAt()));
                }
            } else if (answerRequest.getPriceAt().compareTo(answerRequest.getTakeProfitAt()) > 0) {
                if (answerRequest.getTakeProfitAt().compareTo(answerRequest.getPriceAt()) > 0 || 
                    answerRequest.getStopLossAt().compareTo(answerRequest.getPriceAt()) < 0
                ) {
                    throw new RuntimeException(ConstantBash.getSellInvalidMessage(answerRequest.getPriceAt(), answerRequest.getStopLossAt(), answerRequest.getTakeProfitAt()));
                }
            }

            log.info("End - Validating answer request: {}", answerRequest);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Tuple<BigDecimal, List<?>> transformDataAndCalculateResult(
        List<?> data, 
        BigDecimal transformedHighestHigh, 
        BigDecimal transformedLowestLow,
        AnswerRequest answerRequest,
        BigDecimal scale, BigDecimal verticalTranslation
    ) {
        try {
            log.info("Start - Calculating result");
            AtomicBoolean isSkipResult = new AtomicBoolean(false);
            AtomicBoolean isRunning = new AtomicBoolean(false);
            AtomicReference<BigDecimal> result = new AtomicReference<>(BigDecimal.ZERO);
            log.info("priceAt: {}, transformedHighestHigh: {}, transformedLowestLow: {}", answerRequest.getPriceAt(), transformedHighestHigh, transformedLowestLow);
            if (answerRequest.getPriceAt().compareTo(transformedHighestHigh) > 0 || answerRequest.getPriceAt().compareTo(transformedLowestLow) < 0) {
                log.info("skip result");
                isSkipResult.set(true);
            }

            List<?> transformedData = data.stream()
                .map(d -> {
                    BigDecimal high;
                    try {
                        high = (BigDecimal) d.getClass().getMethod("getHigh").invoke(d);
                        BigDecimal low = (BigDecimal) d.getClass().getMethod("getLow").invoke(d);
                        BigDecimal open = (BigDecimal) d.getClass().getMethod("getOpen").invoke(d);
                        BigDecimal close = (BigDecimal) d.getClass().getMethod("getClosed").invoke(d);

                        BigDecimal transformedHigh = high.add(verticalTranslation).multiply(scale);
                        BigDecimal transformedLow = low.add(verticalTranslation).multiply(scale);
                        BigDecimal transformedOpen = open.add(verticalTranslation).multiply(scale);
                        BigDecimal transformedClose = close.add(verticalTranslation).multiply(scale);

                        if (!isSkipResult.get()) {
                            if (!isRunning.get()) {
                                if (answerRequest.getPriceAt().compareTo(transformedHigh) <= 0 &&
                                    answerRequest.getPriceAt().compareTo(transformedLow) >= 0
                                ) {
                                    log.info("Start - Running at : {}", d.getClass().getMethod("getTimeBucketStart").invoke(d));
                                    isRunning.set(true);
                                }
                            } else {
                                if (answerRequest.getStopLossAt().compareTo(transformedHigh) <= 0 &&
                                    answerRequest.getStopLossAt().compareTo(transformedLow) >= 0
                                ) {
                                    result.set(answerRequest.getStopLossAt().subtract(answerRequest.getPriceAt()).abs().negate());
                                    log.info("End - Running at : {} with loss result: {}", d.getClass().getMethod("getTimeBucketStart").invoke(d), result.get());
                                    isSkipResult.set(true);
                                } else if (answerRequest.getTakeProfitAt().compareTo(transformedHigh) <= 0 &&
                                    answerRequest.getTakeProfitAt().compareTo(transformedLow) >= 0
                                ) {
                                    result.set(answerRequest.getTakeProfitAt().subtract(answerRequest.getPriceAt()).abs());
                                    log.info("End - Running at : {} with profit result: {}", d.getClass().getMethod("getTimeBucketStart").invoke(d), result.get());
                                    isSkipResult.set(true);
                                }
                            }
                        }

                        return new DataOfMarketData(
                            (Instant) d.getClass().getMethod("getTimeBucketStart").invoke(d),
                            transformedOpen,
                            transformedHigh,
                            transformedLow,
                            transformedClose,
                            Long.valueOf(0)
                        );
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException
                            | SecurityException e) {
                        e.printStackTrace();
                        log.error(e.getMessage());
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());

            log.info("End - Transforming data");
            return new Tuple<>(result.get(), transformedData);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private QuizResponse toQuizResponse(QuizEntity entity, List<?> quizMarketData, AnswerResponse answerResponse) {
        try {
            log.info("Mapping entity to response");
            return QuizResponse.builder()
                    .id(entity.getId())
                    .userId(entity.getUserId())
                    .nSize(entity.getNSize().getDescription())
                    .createdAt(entity.getCreatedAt())
                    .updatedAt(entity.getUpdatedAt())
                    .dataCount(quizMarketData == null ? null : (long) quizMarketData.size())
                    .quizMarketData(quizMarketData == null ? new ArrayList<>() : quizMarketData)
                    .answer(answerResponse == null ? null : answerResponse)
                    .build();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private AnswerResponse toAnswerResponse(QuizEntity entity, List<?> answerMarketData) {
        try {
            log.info("Mapping entity to response");
            return AnswerResponse.builder()
                    .result(entity.getResult())
                    .priceAt(entity.getPriceAt())
                    .takeProfitAt(entity.getTakeProfitAt())
                    .stopLossAt(entity.getStopLossAt())
                    .dataCount(answerMarketData == null ? null : (long) answerMarketData.size())
                    .answerMarketData(answerMarketData == null ? new ArrayList<>() : answerMarketData)
                    .build();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
