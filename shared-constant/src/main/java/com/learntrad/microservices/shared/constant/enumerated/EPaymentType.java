package com.learntrad.microservices.shared.constant.enumerated;

import com.learntrad.microservices.shared.constant.ConstantBash;

public enum EPaymentType {
    BANK_TRANSFER("BANK TRANSFER"),
    PAYPAL("PAYPAL"),
    DANA("DANA");

    private final String description;

    EPaymentType (String description){
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static EPaymentType findByDescription(String description){
        for (EPaymentType type : values()){
            if (type.description.equalsIgnoreCase(description)){
                return type;
            }
        }
        throw new IllegalArgumentException(ConstantBash.INVALID_ENUM + description + ". " + EPaymentType.class.getSimpleName() + " " + ConstantBash.VALID_ENUM + getValidTypes());
    }

    public static String getValidTypes() {
        String validTypes = "";
        for (EPaymentType type : values()) {
            validTypes += type.description + ", ";
        }
        return validTypes.substring(0, validTypes.length() - 2);
    }
}
