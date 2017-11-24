package com.tfl.billing;

import java.math.BigDecimal;
import java.util.*;

class PaymentCalculator {

    private static final BigDecimal OFF_PEAK_CAP = new BigDecimal(7.00);
    private static final BigDecimal PEAK_CAP = new BigDecimal(9.00);
    private boolean peakJourneyTaken = false;
    private List<BigDecimal> fares;

    PaymentCalculator(List<BigDecimal> fares){
        this.fares = fares;
    }

    private BigDecimal applyCap(BigDecimal charge){
        if(peakJourneyTaken){
            if(charge.compareTo(PEAK_CAP) > 0){
                return PEAK_CAP;
            }else{
                return charge;
            }
        }else if(charge.compareTo(OFF_PEAK_CAP) > 0){
            return OFF_PEAK_CAP;
        }else {
            return charge;
        }
    }
    void didCustomerRideDuringPeakHours(boolean peak){
        this.peakJourneyTaken = peak;
    }
    private BigDecimal roundToNearestPenny(BigDecimal poundsAndPence) {
        return poundsAndPence.setScale(2, BigDecimal.ROUND_HALF_UP);
    }
    BigDecimal calculate(){
        BigDecimal customerTotal = new BigDecimal(0);
        if(fares != null) {
            for (BigDecimal fare : fares) {
                customerTotal = customerTotal.add(fare);
            }
            customerTotal = applyCap(customerTotal);
        }
        return roundToNearestPenny(customerTotal);
    }
}
