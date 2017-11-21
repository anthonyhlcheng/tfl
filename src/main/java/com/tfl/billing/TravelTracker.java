package com.tfl.billing;

import com.oyster.*;
import com.tfl.external.Customer;
import com.tfl.external.CustomerDatabase;
import com.tfl.external.PaymentsSystem;

import java.math.BigDecimal;
import java.util.*;

public class TravelTracker implements ScanListener {

    private final List<JourneyEvent> eventLog = new ArrayList<JourneyEvent>();
    private final Set<UUID> currentlyTravelling = new HashSet<UUID>();
    private List<Journey> journeys = new ArrayList<Journey>();
    private ArrayList<Date[]> journeyTimes = new ArrayList<Date[]>();
    private ArrayList<Integer> duration = new ArrayList<Integer>();

    private void setJourneyTimes(){
        for(Journey journey: journeys){
            journeyTimes.add(new Date[] {journey.startTime(), journey.endTime()});
            duration.add(journey.durationSeconds());
        }
    }
    public void chargeAccounts() {
        CustomerDatabase customerDatabase = CustomerDatabase.getInstance();
        List<Customer> customers = customerDatabase.getCustomers();
        PaymentCalculator calculator = PaymentCalculator.getInstance();
        for (Customer customer : customers) {
            totalJourneysFor(customer);
            if(!journeys.isEmpty()) {
                setJourneyTimes();
                calculator.setJourneys(journeyTimes, duration);
                PaymentsSystem.getInstance().charge(customer, journeys, calculator.calculate());
            }
            journeys.clear();
        }
    }

    private void totalJourneysFor(Customer customer) {
        List<JourneyEvent> customerJourneyEvents = new ArrayList<JourneyEvent>();
        for (JourneyEvent journeyEvent : eventLog) {
            if (journeyEvent.cardId().equals(customer.cardId())) {
                customerJourneyEvents.add(journeyEvent);
            }
        }
        JourneyEvent start = null;
        for (JourneyEvent event : customerJourneyEvents) {
            if (event instanceof JourneyStart) {
                start = event;
            }
            if (event instanceof JourneyEnd && start != null) {
                journeys.add(new Journey(start, event));
                start = null;
            }
        }
    }

    public void connect(OysterCardReader... cardReaders) {
        for (OysterCardReader cardReader : cardReaders) {
            cardReader.register(this);
        }
    }

    @Override
    public void cardScanned(UUID cardId, UUID readerId) {
        if (currentlyTravelling.contains(cardId)) {
            eventLog.add(new JourneyEnd(cardId, readerId));
            currentlyTravelling.remove(cardId);
        } else {
            if (CustomerDatabase.getInstance().isRegisteredId(cardId)) {
                currentlyTravelling.add(cardId);
                eventLog.add(new JourneyStart(cardId, readerId));
            } else {
                throw new UnknownOysterCardException(cardId);
            }
        }
    }

}
