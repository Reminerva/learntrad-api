package com.learntrad.microservices.shared.constant;

public class ApiBash {

    private ApiBash() {}

    public static final String CUSTOMER = "/api/customer";
    public static final String TRADE = "/api/trade";
    public static final String MARKET_DATA = "/api/market-data";
    public static final String TOP_UP = "/api/top-up";

    public static final String CREATE_CUSTOMER_SUCCESS = "Customer created successfully!";
    public static final String GET_CUSTOMER_SUCCESS = "Get customer success!";
    public static final String GET_ALL_CUSTOMERS_SUCCESS = "Get all customer success!";
    public static final String UPDATE_CUSTOMER_SUCCESS = "Customer updated successfully!";
    public static final String DELETE_CUSTOMER_SUCCESS = "Customer deleted successfully!";

    public static final String CREATE_TRADE_SUCCESS = "Trade created successfully!";
    public static final String GET_TRADE_SUCCESS = "Get trade information success!";
    public static final String GET_ALL_TRADES_SUCCESS = "Get all trade information success!";
    public static final String UPDATE_TRADE_SUCCESS = "Trade updated successfully!";
    public static final String DELETE_TRADE_SUCCESS = "Trade deleted successfully!";
    public static final String CANCEL_TRADE_SUCCESS = "Trade caceled successfully!";
    
    public static final String FETCH_TICK_SUCCESS = "Fetch data tick success!";
    public static final String FETCH_RANGE_SUCCESS = "Fetch data range success!";
    public static final String FETCH_BY_TIME_BUCKET_SUCCESS = "Fetch data by time bucket success!";
    
    public static final String CREATE_TOP_UP_SUCCESS = "Top up created successfully! Pay before expire!";
    public static final String GET_TOP_UP_SUCCESS = "Get top up information success!";
    public static final String GET_ALL_TOP_UPS_SUCCESS = "Get all top up information success!";
    public static final String TOP_UP_PAYMENT_SUCCESS = "Top up payment success!";
    
    public static final String MARKET_EXECUTION_TRADE_NOT_ALLOWED = "Market execution trade is not allowed for this api endpoint! use /api/trade/market-execute instead.";

}
