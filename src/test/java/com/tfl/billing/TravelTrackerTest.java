package com.tfl.billing;
import com.oyster.OysterCard;
import com.oyster.OysterCardReader;
import com.oyster.ScanListener;
import com.tfl.external.Customer;
import com.tfl.external.CustomerDatabase;
import com.tfl.external.PaymentsSystem;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.TestCase.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TravelTrackerTest{

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();


    private CustomerDatabaseInterface db = context.mock(CustomerDatabaseInterface.class);
    private PaymentsSystemInterface payment = context.mock(PaymentsSystemInterface.class);
    private TravelTracker tracker = new TravelTracker(db,payment);
    private String cardString = "2026775e-5b6a-45af-a551-5600575ea1b0";
    private UUID card = UUID.fromString(cardString);
    private UUID readerId = UUID.randomUUID();
    private UUID readerId2 = UUID.randomUUID();




    private Date timeInMillis(String date){
        return new Date(LocalDateTime.parse(date, DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss"))
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli());
    }
    @Test
    public void checkTapInandOut() {
        List<Customer> customers = new ArrayList<>();
        customers.add(new Customer("John Smith", new OysterCard()));

       context.checking(new Expectations(){{
           exactly(2).of(db).isRegisteredId(customers.get(0).cardId()); will(returnValue(true));
        }});
        tracker.cardScanned(customers.get(0).cardId(), readerId);
        tracker.cardScanned(customers.get(0).cardId(), readerId2);
        CustomerTracker customerTracker = tracker.getCustomerTracker(customers.get(0).cardId());
        assertNotNull(customerTracker.getJourneys().get(0));
        assertEquals(customerTracker.getJourneys().size(), 1);
    }

    @Test
    public void checkIfTravelTrackerSuccessfullyHandlesMultipleOysters(){
        List<Customer> customers = new ArrayList<>();
        customers.add(new Customer("John Smith", new OysterCard()));
        customers.add(new Customer("John Johnson", new OysterCard()));

        context.checking(new Expectations(){{
            exactly(2).of(db).isRegisteredId(customers.get(0).cardId()); will(returnValue(true));
            exactly(2).of(db).isRegisteredId(customers.get(1).cardId()); will(returnValue(true));
        }});
        tracker.cardScanned(customers.get(0).cardId(), readerId);
        tracker.cardScanned(customers.get(1).cardId(), readerId2);
        tracker.cardScanned(customers.get(0).cardId(), readerId2);
        tracker.cardScanned(customers.get(1).cardId(), readerId);
        CustomerTracker customerTracker = tracker.getCustomerTracker(customers.get(0).cardId());
        CustomerTracker customerTracker2 = tracker.getCustomerTracker(customers.get(1).cardId());
        assertNotNull(customerTracker.getJourneys().get(0));
        assertEquals(customerTracker.getJourneys().size(), 1);
        assertNotNull(customerTracker2.getJourneys().get(0));
        assertEquals(customerTracker2.getJourneys().size(), 1);
    }
    @Test
    public void checkIfInvalidOysterCardThenThrowsError(){
        UUID falseOyster = UUID.randomUUID();
        context.checking(new Expectations(){{
            exactly(1).of(db).isRegisteredId(falseOyster); will(returnValue(false));
        }});

        try{
            tracker.cardScanned(falseOyster, readerId);
        }catch(UnknownOysterCardException e){
            assertEquals(e.getMessage(), "Oyster Card does not correspond to a known customer. Id: " + falseOyster);
        }catch(Exception e){
            assert(false);
        }
    }

    @Test
    public void areAccountsCharged(){
        List<Customer> customers = new ArrayList<>();
        customers.add(new Customer("John Smith", new OysterCard(cardString)));
        List<Journey> journeys = new ArrayList<>();
        Journey journey = new Journey();
        journey.createStartEvent(card,readerId,timeInMillis("2017/11/22 06:00:00").getTime());
        journey.createEndEvent(card,readerId2, timeInMillis("2017/11/22 06:10:00").getTime());
        journeys.add(journey);
        List<TicketType> tickets = new ArrayList<>();
        tickets.add(new Peak(10 * 60));
        List<BigDecimal> fares = new ArrayList<>();
        BigDecimal fare = new BigDecimal(2.90);
        fares.add(fare);
        CustomerTracker track = new CustomerTracker(journeys, tickets, fares);
        context.checking(new Expectations(){{
            exactly(1).of(db).isRegisteredId(card); will(returnValue(true));
            exactly(1).of(db).getCustomers(); will(returnValue(customers));
            exactly(1).of(payment).charge(customers.get(0), journeys, fare.setScale(2, BigDecimal.ROUND_HALF_UP));
        }});
        tracker.cardScanned(card, readerId, track);
        tracker.chargeAccounts();
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


