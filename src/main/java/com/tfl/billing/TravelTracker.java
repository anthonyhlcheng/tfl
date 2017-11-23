package com.tfl.billing;

import com.oyster.*;
import com.tfl.external.Customer;
import com.tfl.external.CustomerDatabase;
import com.tfl.external.PaymentsSystem;

import java.math.BigDecimal;
import java.util.*;

public class TravelTracker implements ScanListener {

    private final Hashtable<UUID, CustomerTracker> customerTrackerHashTable= new Hashtable<>();
    private CustomerDatabase databases = CustomerDatabase.getInstance();
    private PaymentsSystem payments = PaymentsSystem.getInstance();
    private PaymentCalculator calculator = PaymentCalculator.getInstance();

    public void chargeAccounts() {
        List<Customer> customers = databases.getCustomers();
        for (Customer customer : customers) {
            if(customerTrackerHashTable.containsKey(customer.cardId())){
                CustomerTracker tracker = customerTrackerHashTable.get(customer.cardId());
                List<Journey> journeys = tracker.getJourneys();
                if(!journeys.isEmpty()){
                    calculator.didCustomerRideDuringPeakHours(tracker.checkForPeakJourney());
                    payments.charge(customer, journeys, calculator.calculate(tracker.getFares()));
                }
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
        boolean checkCard = databases.isRegisteredId(cardId);
        if (!checkCard) {
            throw new UnknownOysterCardException(cardId);
        }
        if(!customerTrackerHashTable.containsKey(cardId)){
            customerTrackerHashTable.put(cardId, new CustomerTracker());
        }
        CustomerTracker tracker = customerTrackerHashTable.get(cardId);
        tracker.addEvent(cardId, readerId);
    }

    public CustomerTracker getCustomerTracker(UUID cardId){
        return customerTrackerHashTable.get(cardId);
    }

    public void setCustomerDatabase(CustomerDatabase db){
        this.databases = db;
    }
    public void setPaymentSystem(PaymentsSystem ps){
        this.payments = ps;
    }
}
