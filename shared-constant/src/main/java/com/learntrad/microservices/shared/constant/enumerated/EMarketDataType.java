package com.learntrad.microservices.shared.constant.enumerated;

import java.util.ArrayList;
import java.util.List;

import com.learntrad.microservices.shared.constant.ConstantBash;

public enum EMarketDataType {
    XAUUSD("XAUUSD", 100, "XAU/USD"),
    NASDAQ("NASDAQ", 100, "");

    private final String description;
    private final Integer multiplier;
    private final String exchangeName;

    EMarketDataType (String description, Integer multiplier, String exchangeName){
        this.description = description;
        this.multiplier = multiplier;
        this.exchangeName = exchangeName;
    }

    public String getDescription() {
        return description;
    }

    public Integer getMultiplier() {
        return multiplier;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public static String getValidTypes() {
        String validTypes = "";
        for (EMarketDataType type : values()) {
            validTypes += type.description + ", ";
        }
        return validTypes.substring(0, validTypes.length() - 2);
    }

    public static List<EMarketDataType> getListEnum() {
        List<EMarketDataType> list = new ArrayList<>();
        for (EMarketDataType type : values()) {
            list.add(type);
        }
        return list;
    }

    public static EMarketDataType findByDescription(String description){
        for (EMarketDataType type : values()){
            if (type.description.equalsIgnoreCase(description)){
                return type;
            }
        }
        throw new IllegalArgumentException(ConstantBash.INVALID_ENUM + description + ". " + EMarketDataType.class.getSimpleName() + " " + ConstantBash.VALID_ENUM + getValidTypes());
    }

}
