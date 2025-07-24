package com.learntrad.microservices.shared.constant;

public class DbBash {

    private DbBash() {}

    public static final String CUSTOMER_TABLE = "m_customer";
    public static final String XAUUSD_TABLE = "m_xauusd";
    public static final String TRADE_TABLE = "t_trade";
    public static final String TRADE_PROCESSED_TABLE = "m_trade_processed";
    public static final String TRADE_PROCESSED_USER_TABLE = "m_trade_processed_user";
    public static final String TOP_UP_TABLE = "t_top_up";

    public static final String CUSTOMER_NOT_FOUND = "Customer not found!";
    public static final String CUSTOMER_HAS_BEEN_DELETED = "Customer has been deleted!";

    public static final String TRADE_NOT_FOUND = "Trade not found!";
    public static final String TRADE_IS_NOT_PENDING_OR_RUNNING = "Trade is not pending or running!";

    public static final String LATEST_TICK_NOT_FOUND = "Latest tick not found!";

    public static final String TOP_UP_NOT_FOUND = "Top up not found!";

    public static final String TRADE_PROCESSED_USER_NOT_FOUND = "Trade processed user not found!";
}
