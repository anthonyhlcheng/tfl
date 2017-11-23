package com.tfl.billing;

import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class CustomerTrackerTest {
    private UUID cardId = UUID.randomUUID();
    private UUID readerId = UUID.randomUUID();

    private Date timeInMillis(String date){
        return new Date(LocalDateTime.parse(date, DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss"))
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli());
    }
    @Test
    public void singlePeakShortJourneyCharge(){
        CustomerTracker tracker = new CustomerTracker();
        Date peakTimeEntry = timeInMillis("2017/11/22 06:00:00");
        Date peakTimeExit = timeInMillis("2017/11/22 06:20:00");
        tracker.addEvent(cardId, readerId, peakTimeEntry.getTime());
        tracker.addEvent(cardId, readerId, peakTimeExit.getTime());
        List<BigDecimal> fare = new ArrayList<>();
        fare.add(new BigDecimal(2.90));
        assertThat(tracker.getFares(), is(fare));
    }
    @Test
    public void singlePeakLongJourneyCharge(){
        CustomerTracker tracker = new CustomerTracker();
        Date peakTimeEntry = timeInMillis("2017/11/22 08:01:00");
        Date peakTimeExit = timeInMillis("2017/11/22 09:01:00");
        tracker.addEvent(cardId, readerId, peakTimeEntry.getTime());
        tracker.addEvent(cardId, readerId, peakTimeExit.getTime());
        List<BigDecimal> fare = new ArrayList<>();
        fare.add(new BigDecimal(3.80));
        assertThat(tracker.getFares(), is(fare));
    }
    @Test
    public void singleOffPeakShortJourneyCharge(){
        CustomerTracker tracker = new CustomerTracker();
        Date entry = timeInMillis("2017/11/22 05:50:00");
        Date exit = timeInMillis("2017/11/22 05:59:00");
        tracker.addEvent(cardId, readerId, entry.getTime());
        tracker.addEvent(cardId, readerId, exit.getTime());
        List<BigDecimal> fare = new ArrayList<>();
        fare.add(new BigDecimal(1.60));
        assertThat(tracker.getFares(), is(fare));
    }
    @Test
    public void singleOffPeakLongJourneyCharge(){
        CustomerTracker tracker = new CustomerTracker();
        Date entry = timeInMillis("2017/11/22 10:31:00");
        Date exit = timeInMillis("2017/11/22 11:31:00");
        tracker.addEvent(cardId, readerId, entry.getTime());
        tracker.addEvent(cardId, readerId, exit.getTime());
        List<BigDecimal> fare = new ArrayList<>();
        fare.add(new BigDecimal(2.70));
        assertThat(tracker.getFares(), is(fare));
    }
    @Test
    public void multiOffPeakTrips(){
        CustomerTracker tracker = new CustomerTracker();
        Date entry = timeInMillis("2017/11/22 10:31:00");
        Date exit = timeInMillis("2017/11/22 11:31:00");
        Date entry1 = timeInMillis("2017/11/22 11:31:00");
        Date exit1 = timeInMillis("2017/11/22 12:31:00");
        Date entry2 = timeInMillis("2017/11/22 12:31:00");
        Date exit2 = timeInMillis("2017/11/22 13:31:00");
        Date entry3 = timeInMillis("2017/11/22 13:31:00");
        Date exit3 = timeInMillis("2017/11/22 14:31:00");
        tracker.addEvent(cardId, readerId, entry.getTime());
        tracker.addEvent(cardId, readerId, exit.getTime());
        tracker.addEvent(cardId, readerId, entry1.getTime());
        tracker.addEvent(cardId, readerId, exit1.getTime());
        tracker.addEvent(cardId, readerId, entry2.getTime());
        tracker.addEvent(cardId, readerId, exit2.getTime());
        tracker.addEvent(cardId, readerId, entry3.getTime());
        tracker.addEvent(cardId, readerId, exit3.getTime());
        List<BigDecimal> fare = new ArrayList<>();
        fare.add(new BigDecimal(2.70));
        fare.add(new BigDecimal(2.70));
        fare.add(new BigDecimal(2.70));
        fare.add(new BigDecimal(2.70));
        assertThat(tracker.getFares(), is(fare));
        assertThat(tracker.checkForPeakJourney(), is(false));
    }
    @Test
    public void peakCapping(){
        CustomerTracker tracker = new CustomerTracker();
        Date entry = timeInMillis("2017/11/22 09:31:00");
        Date exit = timeInMillis("2017/11/22 09:35:00");
        Date entry1 = timeInMillis("2017/11/22 11:31:00");
        Date exit1 = timeInMillis("2017/11/22 12:31:00");
        Date entry2 = timeInMillis("2017/11/22 12:31:00");
        Date exit2 = timeInMillis("2017/11/22 13:31:00");
        Date entry3 = timeInMillis("2017/11/22 13:31:00");
        Date exit3 = timeInMillis("2017/11/22 14:31:00");
        tracker.addEvent(cardId, readerId, entry.getTime());
        tracker.addEvent(cardId, readerId, exit.getTime());
        tracker.addEvent(cardId, readerId, entry1.getTime());
        tracker.addEvent(cardId, readerId, exit1.getTime());
        tracker.addEvent(cardId, readerId, entry2.getTime());
        tracker.addEvent(cardId, readerId, exit2.getTime());
        tracker.addEvent(cardId, readerId, entry3.getTime());
        tracker.addEvent(cardId, readerId, exit3.getTime());
        List<BigDecimal> fare = new ArrayList<>();
        fare.add(new BigDecimal(2.90));
        fare.add(new BigDecimal(2.70));
        fare.add(new BigDecimal(2.70));
        fare.add(new BigDecimal(2.70));
        assertThat(tracker.getFares(), is(fare));
        assertThat(tracker.checkForPeakJourney(), is(true));
    }
}