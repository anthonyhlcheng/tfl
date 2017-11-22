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
        calculator.setJourneys(null,null);
        BigDecimal total = calculator.calculate();
        assertThat(total, is(new BigDecimal(0)));
    }
    @Test
    public void singlePeakShortJourneyCharge(){
        ArrayList<Date[]> journeys = new ArrayList<Date[]>();
        ArrayList<Integer> journeyLength = new ArrayList<Integer>();
        Date peakTimeEntry = timeInMillis("2017/11/22 06:00:00");
        Date peakTimeExit = timeInMillis("2017/11/22 06:20:00");
        journeys.add(new Date[]{peakTimeEntry, peakTimeExit});
        journeyLength.add(20*60);
        calculator.setJourneys(journeys,journeyLength);
        assertThat(calculator.calculate().toString(), is("2.90"));
    }
    @Test
    public void singlePeakLongJourneyCharge(){
        ArrayList<Date[]> journeys = new ArrayList<Date[]>();
        ArrayList<Integer> journeyLength = new ArrayList<Integer>();
        Date peakTimeEntry = timeInMillis("2017/11/22 08:01:00");
        Date peakTimeExit = timeInMillis("2017/11/22 09:01:00");
        journeys.add(new Date[]{peakTimeEntry, peakTimeExit});
        journeyLength.add(60*60);
        calculator.setJourneys(journeys,journeyLength);
        assertThat(calculator.calculate().toString(), is("3.80"));
    }
    @Test
    public void singleOffPeakShortJourneyCharge(){
        ArrayList<Date[]> journeys = new ArrayList<Date[]>();
        ArrayList<Integer> journeyLength = new ArrayList<Integer>();
        Date entry = timeInMillis("2017/11/22 05:50:00");
        Date exit = timeInMillis("2017/11/22 05:59:00");
        journeys.add(new Date[]{entry, exit});
        journeyLength.add(20*60);
        calculator.setJourneys(journeys,journeyLength);
        assertThat(calculator.calculate().toString(), is("1.60"));
    }
    @Test
    public void singleOffPeakLongJourneyCharge(){
        ArrayList<Date[]> journeys = new ArrayList<Date[]>();
        ArrayList<Integer> journeyLength = new ArrayList<Integer>();
        Date entry = timeInMillis("2017/11/22 10:31:00");
        Date exit = timeInMillis("2017/11/22 11:31:00");
        journeys.add(new Date[]{entry, exit});
        journeyLength.add(60*60);
        calculator.setJourneys(journeys,journeyLength);
        assertThat(calculator.calculate().toString(), is("2.70"));
    }
    @Test
    public void offPeakCapping(){
        ArrayList<Date[]> journeys = new ArrayList<Date[]>();
        ArrayList<Integer> journeyLength = new ArrayList<Integer>();
        Date entry = timeInMillis("2017/11/22 10:31:00");
        Date exit = timeInMillis("2017/11/22 11:31:00");
        Date entry1 = timeInMillis("2017/11/22 11:31:00");
        Date exit1 = timeInMillis("2017/11/22 12:31:00");
        Date entry2 = timeInMillis("2017/11/22 12:31:00");
        Date exit2 = timeInMillis("2017/11/22 13:31:00");
        Date entry3 = timeInMillis("2017/11/22 13:31:00");
        Date exit3 = timeInMillis("2017/11/22 14:31:00");
        journeys.add(new Date[]{entry, exit});
        journeys.add(new Date[]{entry1, exit1});
        journeys.add(new Date[]{entry2, exit2});
        journeys.add(new Date[]{entry3, exit3});
        journeyLength.add(60*60);
        journeyLength.add(60*60);
        journeyLength.add(60*60);
        journeyLength.add(60*60);
        journeyLength.add(60*60);
        calculator.setJourneys(journeys,journeyLength);
        assertThat(calculator.calculate().toString(), is("7.00"));
    }
    @Test
    public void peakCapping(){
        ArrayList<Date[]> journeys = new ArrayList<Date[]>();
        ArrayList<Integer> journeyLength = new ArrayList<Integer>();
        Date entry = timeInMillis("2017/11/22 09:31:00");
        Date exit = timeInMillis("2017/11/22 09:35:00");
        Date entry1 = timeInMillis("2017/11/22 11:31:00");
        Date exit1 = timeInMillis("2017/11/22 12:31:00");
        Date entry2 = timeInMillis("2017/11/22 12:31:00");
        Date exit2 = timeInMillis("2017/11/22 13:31:00");
        Date entry3 = timeInMillis("2017/11/22 13:31:00");
        Date exit3 = timeInMillis("2017/11/22 14:31:00");
        journeys.add(new Date[]{entry, exit});
        journeys.add(new Date[]{entry1, exit1});
        journeys.add(new Date[]{entry2, exit2});
        journeys.add(new Date[]{entry3, exit3});
        journeyLength.add(60*60);
        journeyLength.add(60*60);
        journeyLength.add(60*60);
        journeyLength.add(60*60);
        journeyLength.add(60*60);
        calculator.setJourneys(journeys,journeyLength);
        assertThat(calculator.calculate().toString(), is("9.00"));
    }

}