package com.tfl.billing;
import com.oyster.OysterCardReader;
import com.oyster.ScanListener;
import com.tfl.external.CustomerDatabase;
import com.tfl.external.PaymentsSystem;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TravelTrackerTest{
    private CustomerDatabase db = mock(CustomerDatabase.class);
    private PaymentsSystem system = mock(PaymentsSystem.class);
    private UUID card = UUID.fromString("2026775e-5b6a-45af-a551-5600575ea1b0");
    private UUID readerId = UUID.randomUUID();
    private UUID readerId2 = UUID.randomUUID();

    private Date timeInMillis(String date){
        return new Date(LocalDateTime.parse(date, DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss"))
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli());
    }
    @Test
    public void tapInandOut() {
        TravelTracker tracker = new TravelTracker();
        TestDatabase test = TestDatabase.getInstance();
        tracker.setCustomerDatabase(db);
        when(db.getCustomers()).thenReturn(test.getCustomers());
        when(db.isRegisteredId(any(UUID.class))).thenReturn(test.isRegisteredId(card));
        tracker.cardScanned(card, readerId);
        tracker.cardScanned(card, readerId2);
        CustomerTracker track = tracker.getCustomerTracker(card);
        List<Journey> journeys = track.getJourneys();
        Journey j = journeys.get(0);
        assertEquals(j.originId(), readerId);
        assertEquals(j.destinationId(), readerId2);
    }

    @Test
    public void areAccountsCharged(){
        TravelTracker tracker = new TravelTracker();
        TestDatabase test = TestDatabase.getInstance();
        tracker.setCustomerDatabase(db);
        when(db.getCustomers()).thenReturn(test.getCustomers());
        when(db.isRegisteredId(card)).thenReturn(test.isRegisteredId(card));
        tracker.cardScanned(card, readerId);
        tracker.cardScanned(card, readerId2);
        CustomerTracker track = tracker.getCustomerTracker(card);
        List<Journey> journeys = track.getJourneys();
        Journey j = journeys.get(0);
        BigDecimal fare = track.getFares().get(0).setScale(2, BigDecimal.ROUND_HALF_UP);
        tracker.setPaymentSystem(system);
        tracker.chargeAccounts();
        verify(system, times(1)).charge(test.getCustomers().get(0), journeys, fare);
    }

    @Test
    public void checkCardReaderConnection(){
        TravelTracker tracker = new TravelTracker();
        OysterCardReader reader1 = mock(OysterCardReader.class);
        OysterCardReader reader2 = mock(OysterCardReader.class);
        tracker.connect(reader1, reader2);
        verify(reader1, times(1)).register(any(ScanListener.class));
        verify(reader2, times(1)).register(any(ScanListener.class));
    }
}


