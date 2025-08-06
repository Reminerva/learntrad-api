package com.learntrad.microservices.shared.constant.enumerated;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import com.learntrad.microservices.shared.constant.ConstantBash;

public enum ETimeFrame {

    ONE_MINUTE("1M", 1),
    FIVE_MINUTE("5M", 5),
    FIFTEEN_MINUTE("15M", 15),
    THIRTY_MINUTE("30M", 30),
    ONE_HOUR("1H", 60),
    TWO_HOUR("2H", 120),
    FOUR_HOUR("4H", 240),
    SIX_HOUR("6H", 360),
    EIGHT_HOUR("8H", 480),
    TWELVE_HOUR("12H", 720),
    ONE_DAY("1D", 1440),
    THREE_DAY("3D", 4320),
    ONE_WEEK("1W", 10080),
    ONE_MONTH("1Mo", 43200);

    private final String description;
    private final Integer inMinutes;

    ETimeFrame (String description, Integer inMinutes){
        this.description = description;
        this.inMinutes = inMinutes;
    }

    public String getDescription() {
        return description;
    }
    
    public Integer getInMinutes() {
        return inMinutes;
    }

    public static ETimeFrame findByDescription(String description) {
        for (ETimeFrame type : values()) {
            if (type.description.equalsIgnoreCase(description)) {
                return type;
            }
        }
        throw new IllegalArgumentException(ConstantBash.INVALID_ENUM + description + ". " + ETimeFrame.class.getSimpleName() + " " + ConstantBash.VALID_ENUM + getValidTypes());
    }

    public static String getValidTypes() {
        String validTypes = "";
        for (ETimeFrame type : values()) {
            validTypes += type.description + ", ";
        }
        return validTypes.substring(0, validTypes.length() - 2);
    }

    public Instant truncatedBasedOnTimeFrame(Instant time) {
        if (this.equals(ETimeFrame.ONE_MINUTE) ||
            this.equals(ETimeFrame.FIVE_MINUTE) ||
            this.equals(ETimeFrame.FIFTEEN_MINUTE) ||
            this.equals(ETimeFrame.THIRTY_MINUTE)) {
            return time.truncatedTo(ChronoUnit.MINUTES);
        } else if (this.equals(ETimeFrame.ONE_HOUR) ||
            this.equals(ETimeFrame.TWO_HOUR) ||
            this.equals(ETimeFrame.FOUR_HOUR) ||
            this.equals(ETimeFrame.SIX_HOUR) ||
            this.equals(ETimeFrame.EIGHT_HOUR) ||
            this.equals(ETimeFrame.TWELVE_HOUR)) {
            return time.truncatedTo(ChronoUnit.HOURS);
        } else if (this.equals(ETimeFrame.ONE_DAY) ||
            this.equals(ETimeFrame.THREE_DAY) ||
            this.equals(ETimeFrame.ONE_WEEK)) {
            return time.truncatedTo(ChronoUnit.DAYS);
        } else if (this.equals(ETimeFrame.ONE_MONTH)) {
            return time.atZone(java.time.ZoneOffset.UTC)
                    .withDayOfMonth(1)
                    .truncatedTo(ChronoUnit.DAYS)
                    .toInstant();
        } else {
            return time;
        }
    }

    public String toPostgresInterval() {
        return switch (this) {
            case ONE_MINUTE -> "1 minute";
            case FIVE_MINUTE -> "5 minutes";
            case FIFTEEN_MINUTE -> "15 minutes";
            case THIRTY_MINUTE -> "30 minutes";
            case ONE_HOUR -> "1 hour";
            case TWO_HOUR -> "2 hours";
            case FOUR_HOUR -> "4 hours";
            case SIX_HOUR -> "6 hours";
            case EIGHT_HOUR -> "8 hours";
            case TWELVE_HOUR -> "12 hours";
            case ONE_DAY -> "1 day";
            case THREE_DAY -> "3 days";
            case ONE_WEEK -> "1 week";
            case ONE_MONTH -> "1 month";
        };
    }

    public Instant setDefaultTimeBucketStartMin(Instant timeBucketStartMax) {
        Integer dataNum = 120;
        return switch (this) {
            case ONE_MINUTE -> timeBucketStartMax.minus(Duration.ofMinutes(dataNum));
            case FIVE_MINUTE -> timeBucketStartMax.minus(Duration.ofMinutes(dataNum * 5));
            case FIFTEEN_MINUTE -> timeBucketStartMax.minus(Duration.ofMinutes(dataNum * 15));
            case THIRTY_MINUTE -> timeBucketStartMax.minus(Duration.ofMinutes(dataNum * 30));
            case ONE_HOUR -> timeBucketStartMax.minus(Duration.ofHours(dataNum * 1));
            case TWO_HOUR -> timeBucketStartMax.minus(Duration.ofHours(dataNum * 2));
            case FOUR_HOUR -> timeBucketStartMax.minus(Duration.ofHours(dataNum * 4));
            case SIX_HOUR -> timeBucketStartMax.minus(Duration.ofHours(dataNum * 6));
            case EIGHT_HOUR -> timeBucketStartMax.minus(Duration.ofHours(dataNum * 8));
            case TWELVE_HOUR -> timeBucketStartMax.minus(Duration.ofHours(dataNum * 12));
            case ONE_DAY -> timeBucketStartMax.minus(Duration.ofDays(dataNum));
            case THREE_DAY -> timeBucketStartMax.minus(Duration.ofDays(dataNum * 3));
            case ONE_WEEK -> timeBucketStartMax.minus(Duration.ofDays(dataNum * 7));
            case ONE_MONTH -> timeBucketStartMax.minus(Duration.ofDays(dataNum * 30));
        };
    }

}
