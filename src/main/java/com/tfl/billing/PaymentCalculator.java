package com.tfl.billing;

import java.math.BigDecimal;
import java.util.*;

public class PaymentCalculator {

    private static PaymentCalculator instance;
    private ArrayList<Date[]> journeys;
    private ArrayList<Integer> journeyLength;
    private static final BigDecimal OFF_PEAK_CAP = new BigDecimal(7.00);
    private static final BigDecimal PEAK_CAP = new BigDecimal(9.00);

    private PaymentCalculator(){}

    public static synchronized PaymentCalculator getInstance(){
        if (instance == null){
            instance = new PaymentCalculator();
        }
        return instance;
    }
    public void setJourneys(ArrayList<Date[]> journeys, ArrayList<Integer> journeyLength){
        this.journeys = journeys;
        this.journeyLength = journeyLength;
    }

    private boolean peak(Date start, Date end) {
        return peak(start) || peak(end);
    }
    private boolean peak(Date time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return (hour >= 6 && hour <= 9) || (hour >= 17 && hour <= 19);
    }
    private BigDecimal applyCap(BigDecimal charge){
        Iterator<Date[]> journeyIterator = journeys.iterator();
        while(journeyIterator.hasNext()){
            Date[] journey = journeyIterator.next();
            if(peak(journey[0], journey[1])){
                if(charge.compareTo(PEAK_CAP) > 0) {
                    return PEAK_CAP;
                }else{
                    return charge;
                }
            }
        }
        return OFF_PEAK_CAP;
    }
    private BigDecimal roundToNearestPenny(BigDecimal poundsAndPence) {
        return poundsAndPence.setScale(2, BigDecimal.ROUND_HALF_UP);
    }
    public BigDecimal calculate(){
        BigDecimal customerTotal = new BigDecimal(0);
        Iterator<Date[]> journeyIterator = journeys.iterator();
        Iterator<Integer> journeyDuration = journeyLength.iterator();
        while(journeyDuration.hasNext() && journeyIterator.hasNext()){
            Date[] journey = journeyIterator.next();
            Integer duration = journeyDuration.next();
            if (peak(journey[0], journey[1])) {
                customerTotal = customerTotal.add(new Peak(duration).price());
            }else{
                customerTotal = customerTotal.add(new OffPeak(duration).price());
            }
        }
        if(customerTotal.compareTo(OFF_PEAK_CAP) <= 0){
            return roundToNearestPenny(customerTotal);
        }else {
            return roundToNearestPenny(applyCap(customerTotal));
        }
    }
}
