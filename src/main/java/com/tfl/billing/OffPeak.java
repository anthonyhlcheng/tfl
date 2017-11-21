package com.tfl.billing;

import java.math.BigDecimal;

public class OffPeak extends TicketType {
    static final BigDecimal OFF_PEAK_SHORT_JOURNEY_PRICE = new BigDecimal(1.60);
    static final BigDecimal OFF_PEAK_LONG_JOURNEY_PRICE = new BigDecimal(2.70);
    static final int TIME_THRESHOLD = 25*60;
    private BigDecimal finalPrice;
    private int duration;
    public OffPeak(int duration){
        this.duration = duration;
    }
    @Override
    public BigDecimal price() {
        if(duration <= (TIME_THRESHOLD)){
            return OFF_PEAK_SHORT_JOURNEY_PRICE;
        }else{
            return OFF_PEAK_LONG_JOURNEY_PRICE;
        }
    }
}
