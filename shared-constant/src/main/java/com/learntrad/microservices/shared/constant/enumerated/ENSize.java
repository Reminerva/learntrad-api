package com.learntrad.microservices.shared.constant.enumerated;

import java.util.ArrayList;
import java.util.List;

import com.learntrad.microservices.shared.constant.ConstantBash;

public enum ENSize {

    TINY("TINY", 60),
    STANDARD("STANDARD", 90),
    LARGE("LARGE", 120);

    private final String description;
    private final Integer size;

    ENSize (String description, Integer size){
        this.description = description;
        this.size = size;
    }

    public String getDescription() {
        return description;
    }

    public Integer getSize() {
        return size;
    }

    public static String getValidTypes() {
        String validTypes = "";
        for (ENSize type : values()) {
            validTypes += type.description + ", ";
        }
        return validTypes.substring(0, validTypes.length() - 2);
    }

    public static List<ENSize> getListEnum() {
        List<ENSize> list = new ArrayList<>();
        for (ENSize type : values()) {
            list.add(type);
        }
        return list;
    }

    public static ENSize findByDescription(String description){
        for (ENSize type : values()){
            if (type.description.equalsIgnoreCase(description)){
                return type;
            }
        }
        throw new IllegalArgumentException(ConstantBash.INVALID_ENUM + description + ". " + ENSize.class.getSimpleName() + " " + ConstantBash.VALID_ENUM + getValidTypes());
    }
}
