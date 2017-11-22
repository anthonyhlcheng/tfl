package com.tfl.billing;

import com.oyster.*;
import com.tfl.external.Customer;
import com.tfl.external.CustomerDatabase;
import com.tfl.external.PaymentsSystem;

import java.math.BigDecimal;
import java.util.*;

public class TravelTracker implements ScanListener {

    private final Hashtable<UUID, CustomerTracker> customerTrackerHashTable= new Hashtable<>();
    private PaymentsSystem paymentsSystem = PaymentsSystem.getInstance();
    private PaymentCalculator calculator = PaymentCalculator.getInstance();

    public void chargeAccounts() {
        CustomerDatabase customerDatabase = CustomerDatabase.getInstance();
        List<Customer> customers = customerDatabase.getCustomers();
        for (Customer customer : customers) {
            if(customerTrackerHashTable.containsKey(customer.cardId())){
                CustomerTracker tracker = customerTrackerHashTable.get(customer.cardId());
                List<Journey> journeys = tracker.getJourneys();
                if(!journeys.isEmpty()){
                    paymentsSystem.charge(customer, journeys, calculator.calculate(tracker.getFares(), tracker.checkForPeakJourney()));
                }
            }
        }
    }

    public void changePaymentSystem(PaymentsSystem system){
        this.paymentsSystem = system;
    }

    public void connect(OysterCardReader... cardReaders) {
        for (OysterCardReader cardReader : cardReaders) {
            cardReader.register(this);
        }
    }

    @Override
    public void cardScanned(UUID cardId, UUID readerId) {
        if (!CustomerDatabase.getInstance().isRegisteredId(cardId)) {
            throw new UnknownOysterCardException(cardId);
        }

        if(!customerTrackerHashTable.containsKey(cardId)){
            customerTrackerHashTable.put(cardId, new CustomerTracker(cardId));
        }
        CustomerTracker tracker = customerTrackerHashTable.get(cardId);
        tracker.addEvent(cardId, readerId);
    }

}
