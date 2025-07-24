package com.learntrad.microservices.shared.constant.enumerated;

import com.learntrad.microservices.shared.constant.ConstantBash;

public enum ETradeStatus {
    PENDING("PENDING"),
    RUNNING("RUNNING"),
    PROFIT("PROFIT"),
    LOSS("LOSS"),
    CANCELED("CANCELED"),
    EXPIRED("EXPIRED");

    private final String description;

    ETradeStatus (String description){
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static ETradeStatus findByDescription(String description){
        for (ETradeStatus type : values()){
            if (type.description.equalsIgnoreCase(description)){
                return type;
            }
        }
        throw new IllegalArgumentException(ConstantBash.INVALID_ENUM + description + ". " + ETradeStatus.class.getSimpleName() + " " + ConstantBash.VALID_ENUM + getValidTypes());
    }

    public static String getValidTypes() {
        String validTypes = "";
        for (ETradeStatus type : values()) {
            validTypes += type.description + ", ";
        }
        return validTypes.substring(0, validTypes.length() - 2);
    }
}
