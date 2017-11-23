package com.tfl.billing;

import org.junit.Test;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class PaymentCalculatorTest {
    PaymentCalculator calculator = PaymentCalculator.getInstance();
    private Date timeInMillis(String date){
        return new Date(LocalDateTime.parse(date, DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss"))
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli());
    }
    @Test
    public void noJourneysMeansNoCharge(){
        BigDecimal total = calculator.calculate(null);
        assertThat(total.toString(), is("0.00"));
    }
    @Test
    public void peakJourneyButNoCapping(){
        List<BigDecimal> fares = new ArrayList<>();
        fares.add(new BigDecimal(2.90));
        fares.add(new BigDecimal(3.80));
        calculator.didCustomerRideDuringPeakHours(true);
        BigDecimal total = calculator.calculate(fares);
        assertThat(total.toString(), is("6.70"));
    }
    @Test
    public void peakJourneyCappingRequired(){
        List<BigDecimal> fares = new ArrayList<>();
        fares.add(new BigDecimal(2.90));
        fares.add(new BigDecimal(3.80));
        fares.add(new BigDecimal(3.80));
        calculator.didCustomerRideDuringPeakHours(true);
        BigDecimal total = calculator.calculate(fares);
        assertThat(total.toString(), is("9.00"));
    }
    @Test
    public void offPeakJourneyButNoCapping(){
        List<BigDecimal> fares = new ArrayList<>();
        fares.add(new BigDecimal(2.70));
        fares.add(new BigDecimal(2.70));
        calculator.didCustomerRideDuringPeakHours(false);
        BigDecimal total = calculator.calculate(fares);
        assertThat(total.toString(), is("5.40"));
    }
    @Test
    public void offPeakJourneyCappingRequired(){
        List<BigDecimal> fares = new ArrayList<>();
        fares.add(new BigDecimal(2.70));
        fares.add(new BigDecimal(2.70));
        fares.add(new BigDecimal(1.60));
        fares.add(new BigDecimal(1.60));
        calculator.didCustomerRideDuringPeakHours(false);
        BigDecimal total = calculator.calculate(fares);
        assertThat(total.toString(), is("7.00"));
    }


}