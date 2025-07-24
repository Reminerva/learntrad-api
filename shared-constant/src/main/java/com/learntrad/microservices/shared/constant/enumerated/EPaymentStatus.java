package com.learntrad.microservices.shared.constant.enumerated;

import com.learntrad.microservices.shared.constant.ConstantBash;

public enum EPaymentStatus {
    PENDING("PENDING"),
    SUCCESS("SUCCESS"),
    FAILED("FAILED");

    private final String description;

    EPaymentStatus (String description){
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static String getValidTypes() {
        String validTypes = "";
        for (EPaymentStatus type : values()) {
            validTypes += type.description + ", ";
        }
        return validTypes.substring(0, validTypes.length() - 2);
    }

    public static EPaymentStatus findByDescription(String description){
        for (EPaymentStatus type : values()){
            if (type.description.equalsIgnoreCase(description)){
                return type;
            }
        }
        throw new IllegalArgumentException(ConstantBash.INVALID_ENUM + description + ". " + EPaymentStatus.class.getSimpleName() + " " + ConstantBash.VALID_ENUM + getValidTypes());
    }
}
