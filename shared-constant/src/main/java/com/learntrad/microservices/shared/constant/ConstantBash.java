package com.learntrad.microservices.shared.constant;

public class ConstantBash {

    public static final String HAS_ROLE_CUSTOMER = "customer";
    public static final String HAS_ROLE_ADMIN = "admin";

    public static final String MIN_MAX_INVALID = "Invalid min or max value!";
    public static final String NOT_ALLOWED_TO_UPDATE_TRADE = "You are not allowed to update this trade";

    public static final String PLEASE_FILL_CUSTOMER_PROFILE = "Please fill the customer's profile first.";

    public static final String INVALID_TRADE_REQUEST = "Invalid trade request!";

    public static final String INVALID_ENUM = "Invalid enum description: ";
    public static final String VALID_ENUM = "Valid enum: ";

    public static final String INVALID_EXPIRED_AT = "The expired at must be in the future!";

    public static final String USER_NOT_AUTHORIZED = "User is not authorized!";

    public static final String TOP_UP_ALREADY_PAID = "This top up request is already paid";
    public static final String TOP_UP_ALREADY_FAILED = "This top up request is already expired or failed";

    public static final String INVALID_DIRECTION = "Invalid direction! Direction must be 'Desc' or 'Asc'";

    public static String getBuyInvalidMessage(Object priceAt, Object stopLossAt, Object takeProfitAt) {
        return String.format("""
        Invalid buy values! 'priceAt' must be greater than 'stopLossAt' and less than 'takeProfitAt'. (stopLossAt < priceAt < takeProfitAt). In this case: priceAt: %s stopLossAt: %s takeProfitAt: %s""", priceAt, stopLossAt, takeProfitAt);
    }

    public static String getSellInvalidMessage(Object priceAt, Object stopLossAt, Object takeProfitAt) {
        return String.format("""
        Invalid sell values! 'priceAt' must be greater than 'takeProfitAt' and less than 'stopLossAt'. (takeProfitAt < priceAt < stopLossAt). In this case: priceAt: %s stopLossAt: %s takeProfitAt: %s""", priceAt, stopLossAt, takeProfitAt);
    }

    public static String getBuyLimitPriceAtInvalidMessage(Object priceAt, Object priceNow) {
        return String.format("""
        Invalid buy limit values! 'priceAt' must be less than 'priceNow'. (priceAt < priceNow). In this case: priceAt: %s priceNow: %s""", priceAt, priceNow);
    }

    public static String getSellLimitPriceAtInvalidMessage(Object priceAt, Object priceNow) {
        return String.format("""
        Invalid sell limit values! 'priceAt' must be greater than 'priceNow'. (priceAt > priceNow). In this case: priceAt: %s priceNow: %s""", priceAt, priceNow);
    }

    public static String getBuyStopPriceAtInvalidMessage(Object priceAt, Object priceNow) {
        return String.format("""
        Invalid buy stop values! 'priceAt' must be greater than 'priceNow'. (priceAt > priceNow). In this case: priceAt: %s priceNow: %s""", priceAt, priceNow);
    }

    public static String getSellStopPriceAtInvalidMessage(Object priceAt, Object priceNow) {
        return String.format("""
        Invalid sell stop values! 'priceAt' must be less than 'priceNow'. (priceAt < priceNow). In this case: priceAt: %s priceNow: %s""", priceAt, priceNow);
    }

    public static String getBalanceInvalidMessage(Object customerBalance, Object lossPotential) {
        return String.format("""
        Insufficient balance! Customer balance: %s. Loss potential accumulation (based on your current running and pending trade): %s""", customerBalance, lossPotential);
    }

    public static String getTradeAtInvalidMessage(Object tradeAt, Object now) {
        return String.format("""
        Trade is not allowed if the 'trade at (%s)' is in the future or before 1 minute of 'now (%s)'.""", tradeAt, now);
    }
}
