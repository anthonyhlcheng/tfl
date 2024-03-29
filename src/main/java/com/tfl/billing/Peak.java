package com.tfl.billing;

import java.math.BigDecimal;

public class Peak implements TicketType {
    private static final BigDecimal PEAK_SHORT_JOURNEY_PRICE = new BigDecimal(2.90);
    private static final BigDecimal PEAK_LONG_JOURNEY_PRICE = new BigDecimal(3.80);
    private static final int TIME_THRESHOLD = 25*60; //In seconds 25 mins
    private int duration;
    Peak(int duration){
        this.duration = duration;
    }
    @Override
    public BigDecimal price() {
        //Check if short or long journey and return appropriate price
        if(duration <= TIME_THRESHOLD){
                return PEAK_SHORT_JOURNEY_PRICE;
        }else{
                return PEAK_LONG_JOURNEY_PRICE;
        }
    }
}
