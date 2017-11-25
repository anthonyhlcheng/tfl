package com.tfl.billing;

import java.math.BigDecimal;

public class OffPeak extends TicketType {
    private static final BigDecimal OFF_PEAK_SHORT_JOURNEY_PRICE = new BigDecimal(1.60);
    private static final BigDecimal OFF_PEAK_LONG_JOURNEY_PRICE = new BigDecimal(2.70);
    private static final int TIME_THRESHOLD = 25*60; //In seconds 25 mins
    private int duration;
    OffPeak(int duration){
        this.duration = duration;
    }
    @Override
    public BigDecimal price() {
        //Check if short or long journey and return appropriate price
        if(duration <= (TIME_THRESHOLD)){
            return OFF_PEAK_SHORT_JOURNEY_PRICE;
        }else{
            return OFF_PEAK_LONG_JOURNEY_PRICE;
        }
    }
}
