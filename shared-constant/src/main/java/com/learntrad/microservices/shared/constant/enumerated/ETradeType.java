package com.learntrad.microservices.shared.constant.enumerated;

import com.learntrad.microservices.shared.constant.ConstantBash;

public enum ETradeType {
    BUY_STOP("BUY STOP"),
    BUY_LIMIT("BUY LIMIT"),
    SELL_STOP("SELL STOP"),
    SELL_LIMIT("SELL LIMIT"),
    MARKET_EXECUTION_BUY("MARKET EXECUTION BUY"),
    MARKET_EXECUTION_SELL("MARKET EXECUTION SELL");

    private final String description;

    ETradeType (String description){
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static ETradeType findByDescription(String description){
        for (ETradeType type : values()){
            if (type.description.equalsIgnoreCase(description)){
                return type;
            }
        }
        throw new IllegalArgumentException(ConstantBash.INVALID_ENUM + description + ". " + ETradeType.class.getSimpleName() + " " + ConstantBash.VALID_ENUM + getValidTypes());
    }

    public static String getValidTypes() {
        String validTypes = "";
        for (ETradeType type : values()) {
            validTypes += type.description + ", ";
        }
        return validTypes.substring(0, validTypes.length() - 2);
    }
}
