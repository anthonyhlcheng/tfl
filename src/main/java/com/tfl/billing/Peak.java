package com.tfl.billing;

import java.math.BigDecimal;

public class Peak extends TicketType {
    static final BigDecimal PEAK_SHORT_JOURNEY_PRICE = new BigDecimal(2.90);
    static final BigDecimal PEAK_LONG_JOURNEY_PRICE = new BigDecimal(3.80);
    static final int TIME_THRESHOLD = 25*60;
    private int duration;
    public Peak(int duration){
        this.duration = duration;
    }
    @Override
    public BigDecimal price() {
        if(duration <= TIME_THRESHOLD){
                return PEAK_SHORT_JOURNEY_PRICE;
        }else{
                return PEAK_LONG_JOURNEY_PRICE;
        }
    }
}