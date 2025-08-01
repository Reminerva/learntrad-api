package com.learntrad.microservices.notification.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import com.learntrad.microservices.notification.service.intrface.NotificationService;
import com.learntrad.microservices.shared.constant.enumerated.ETradeStatus;
import com.learntrad.microservices.trade.event.TradeEditedEvent;
import com.learntrad.microservices.trade.event.TradePlacedEvent;
import com.learntrad.microservices.trade.event.TradeStatusUpdatedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final JavaMailSender javaMailSender;

    @Value("${mail.email}")
    private String from;


    @Override
    @KafkaListener(topics = "trade-placed", containerFactory = "kafkaListenerContainerFactory")
    public void listenTradePlaced(TradePlacedEvent event) {
        log.info("Received Message from trade-placed topic {}", event);
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
            helper.setFrom(from);
            helper.setTo(event.getEmail().toString());
            helper.setSubject("Trade Placed with TradeId " + event.getTradeId());
            helper.setText(String.format("""
                    Hello %s,

                    Your trade has been successfully placed with the following details:

                    Trade ID        : %s
                    Username        : %s
                    Customer Name   : %s
                    Market          : %s
                    Trade At        : %s
                    Trade Type      : %s
                    Entry Price     : %.2f
                    Stop Loss       : %.2f
                    Take Profit     : %.2f
                    Lot             : %.2f
                    Expired At      : %s (%s)

                    You can monitor the trade status through your dashboard.

                    Regards,
                    Learntrad Team
                    """,
                    event.getUsername(),
                    event.getTradeId(),
                    event.getUsername(),
                    event.getCustomerFullname(),
                    event.getMarketDataType(),
                    event.getTradeAt(),
                    event.getTradeType(),
                    event.getPriceAt(),
                    event.getStopLossAt(),
                    event.getTakeProfitAt(),
                    event.getLot(),
                    event.getExpiredAt() == null ? "" : event.getExpiredAt().toString().split("T")[0],
                    event.getExpiredAt() == null ? "" : event.getExpiredAt().toString().split("T")[1]
            ));
        };
        log.info("Start - Sending email to {}", event.getEmail());
        sendEmail(messagePreparator);
        log.info("End - Email sent to {}", event.getEmail());
    }

    @Override
    @KafkaListener(topics = "trade-edited")
    public void listenTradeEdited(TradeEditedEvent event) {
        log.info("Received Message from trade-edited topic {}", event);
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
            helper.setFrom(from);
            helper.setTo(event.getEmail().toString());
            helper.setSubject("Trade Edited with TradeId " + event.getTradeId());
            helper.setText(String.format("""
                    Hello %s,

                    Your trade with the following details has been successfully updated:

                    Trade ID        : %s
                    Username        : %s
                    Customer Name   : %s
                    Market          : %s
                    Trade At        : %s
                    Trade Type      : %s
                    Lot             : %.2f

                    --- Updated Values ---
                    Stop Loss       : %.2f -> %.2f
                    Take Profit     : %.2f -> %.2f
                    Expired At      : %s (%s) -> %s (%s)

                    Please review your updated trade in the dashboard.

                    Regards,
                    Learntrad Team
                    """,
                    event.getUsername(),
                    event.getTradeId(),
                    event.getUsername(),
                    event.getCustomerFullname(),
                    event.getMarketDataType(),
                    event.getTradeAt(),
                    event.getTradeType(),
                    event.getLot(),
                    event.getOldStopLossAt(), event.getNewStopLossAt(),
                    event.getOldTakeProfitAt(), event.getNewTakeProfitAt(),
                    event.getOldExpiredAt() == null ? "None" : event.getOldExpiredAt().toString().split("T")[0], event.getOldExpiredAt() == null ? "None" : event.getOldExpiredAt().toString().split("T")[1], 
                    event.getNewExpiredAt() == null ? "None" : event.getNewExpiredAt().toString().split("T")[0], event.getNewExpiredAt() == null ? "None" : event.getNewExpiredAt().toString().split("T")[1]
            ));
        };
        log.info("Start - Sending email to {}", event.getEmail());
        sendEmail(messagePreparator);
        log.info("End - Email sent to {}", event.getEmail());
    }

    @KafkaListener(topics = "trade-status-updated")
    public void listenTradeStatusUpdated(TradeStatusUpdatedEvent event) {
        log.info("Received Message from trade-status-updated topic {}", event);

        ETradeStatus tradeStatus = ETradeStatus.findByDescription(event.getTradeStatus());

        String subject = "Trade Update - " + event.getTradeId();

        switch (tradeStatus) {
            case PROFIT -> {
                log.info("This Trade {} closed with PROFIT", event.getTradeId());
                String messageBody = String.format("""
                        Hello %s,

                        Congratulations! Your trade has closed with a **PROFIT**.

                        Trade ID        : %s
                        Market          : %s
                        Trade Type      : %s
                        Entry Price     : %.2f
                        Closed At       : %.2f
                        Take Profit     : %.2f
                        Lot             : %.2f

                        Keep up the good trading!

                        Regards,
                        Learntrad Team
                        """,
                        event.getUsername(),
                        event.getTradeId(),
                        event.getMarketDataType(),
                        event.getTradeType(),
                        event.getPriceAt(),
                        event.getClosedAt(),
                        event.getTakeProfitAt(),
                        event.getLot()
                );
                MimeMessagePreparator messagePreparator = mimeMessage -> {
                    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
                    helper.setFrom(from);
                    helper.setTo(event.getEmail().toString());
                    helper.setSubject(subject);
                    helper.setText(messageBody);
                };
                
                log.info("Start - Sending email to {}", event.getEmail());
                sendEmail(messagePreparator);
                log.info("End - Email sent to {}", event.getEmail());
            }

            case LOSS -> {
                log.info("This Trade {} closed with LOSS", event.getTradeId());
                String messageBody = String.format("""
                        Hello %s,

                        Unfortunately, your trade has been closed with a **LOSS**.

                        Trade ID        : %s
                        Market          : %s
                        Trade Type      : %s
                        Entry Price     : %.2f
                        Closed At       : %.2f
                        Stop Loss       : %.2f
                        Lot             : %.2f

                        Don't give up. Review your strategy and keep learning.

                        Regards,
                        Learntrad Team
                        """,
                        event.getUsername(),
                        event.getTradeId(),
                        event.getMarketDataType(),
                        event.getTradeType(),
                        event.getPriceAt(),
                        event.getClosedAt(),
                        event.getStopLossAt(),
                        event.getLot()
                );
                MimeMessagePreparator messagePreparator = mimeMessage -> {
                    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
                    helper.setFrom(from);
                    helper.setTo(event.getEmail().toString());
                    helper.setSubject(subject);
                    helper.setText(messageBody);
                };
                
                log.info("Start - Sending email to {}", event.getEmail());
                sendEmail(messagePreparator);
                log.info("End - Email sent to {}", event.getEmail());
            }

            case CANCELED -> {
                log.info("This Trade {} has been CANCELED", event.getTradeId());
                String messageBody = String.format("""
                        Hello %s,

                        Your trade has been **CANCELED**.

                        Trade ID        : %s
                        Market          : %s
                        Trade Type      : %s
                        Entry Price     : %.2f
                        Lot             : %.2f

                        You may place a new trade anytime from your dashboard.

                        Regards,
                        Learntrad Team
                        """,
                        event.getUsername(),
                        event.getTradeId(),
                        event.getMarketDataType(),
                        event.getTradeType(),
                        event.getPriceAt(),
                        event.getLot()
                );
                MimeMessagePreparator messagePreparator = mimeMessage -> {
                    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
                    helper.setFrom(from);
                    helper.setTo(event.getEmail().toString());
                    helper.setSubject(subject);
                    helper.setText(messageBody);
                };
                
                log.info("Start - Sending email to {}", event.getEmail());
                sendEmail(messagePreparator);
                log.info("End - Email sent to {}", event.getEmail());
            }

            case EXPIRED -> {
                log.info("This Trade {} has been EXPIRED", event.getTradeId());
                String messageBody = String.format("""
                        Hello %s,

                        Your pending trade has **EXPIRED** due to no execution within the allowed time.

                        Trade ID        : %s
                        Market          : %s
                        Trade Type      : %s
                        Entry Price     : %.2f
                        Lot             : %.2f

                        You may review your setup and try again.

                        Regards,
                        Learntrad Team
                        """,
                        event.getUsername(),
                        event.getTradeId(),
                        event.getMarketDataType(),
                        event.getTradeType(),
                        event.getPriceAt(),
                        event.getLot()
                );
                MimeMessagePreparator messagePreparator = mimeMessage -> {
                    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
                    helper.setFrom(from);
                    helper.setTo(event.getEmail().toString());
                    helper.setSubject(subject);
                    helper.setText(messageBody);
                };
                
                log.info("Start - Sending email to {}", event.getEmail());
                sendEmail(messagePreparator);
                log.info("End - Email sent to {}", event.getEmail());
            }

            case RUNNING -> {
                log.info("This Trade {} is currently RUNNING", event.getTradeId());
                String messageBody = String.format("""
                        Hello %s,

                        Your trade is currently **RUNNING**.

                        Trade ID        : %s
                        Market          : %s
                        Trade Type      : %s
                        Entry Price     : %.2f
                        Stop Loss       : %.2f
                        Take Profit     : %.2f
                        Lot             : %.2f

                        You can monitor the trade status through your dashboard.

                        Regards,
                        Learntrad Team
                        """,
                        event.getUsername(),
                        event.getTradeId(),
                        event.getMarketDataType(),
                        event.getTradeType(),
                        event.getPriceAt(),
                        event.getStopLossAt(),
                        event.getTakeProfitAt(),
                        event.getLot()
                );
                MimeMessagePreparator messagePreparator = mimeMessage -> {
                    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
                    helper.setFrom(from);
                    helper.setTo(event.getEmail().toString());
                    helper.setSubject(subject);
                    helper.setText(messageBody);
                };
                
                log.info("Start - Sending email to {}", event.getEmail());
                sendEmail(messagePreparator);
                log.info("End - Email sent to {}", event.getEmail());
            }

            default -> {
                log.warn("Unknown trade status received: {}", event.getTradeStatus());
                return;
            }
        }
    }

    @Override
    @KafkaListener(topics = "trade-canceled")
    public void listenTradeCanceled(TradeStatusUpdatedEvent event) {
        log.info("Received Message from trade-canceled topic {}", event);
        String subject = "Trade Update - " + event.getTradeId();
        String messageBody = String.format("""
            Hello %s,

            Your trade has been **CANCELED**.

            Trade ID        : %s
            Market          : %s
            Trade Type      : %s
            Entry Price     : %.2f
            Lot             : %.2f

            You may place a new trade anytime from your dashboard.

            Regards,
            Learntrad Team
            """,
            event.getUsername(),
            event.getTradeId(),
            event.getMarketDataType(),
            event.getTradeType(),
            event.getPriceAt(),
            event.getLot()
        );
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
            helper.setFrom(from);
            helper.setTo(event.getEmail().toString());
            helper.setSubject(subject);
            helper.setText(messageBody);
        };
        
        log.info("Start - Sending email to {}", event.getEmail());
        sendEmail(messagePreparator);
        log.info("End - Email sent to {}", event.getEmail());
    }

    private void sendEmail(MimeMessagePreparator messagePreparator) {
        try {
            log.info("Sending email...");
            javaMailSender.send(messagePreparator);
            log.info("The email has been sent successfully");
        } catch (Exception e) {
            log.error("Error while sending email", e);
            throw new RuntimeException(e);
        }
    }

}
