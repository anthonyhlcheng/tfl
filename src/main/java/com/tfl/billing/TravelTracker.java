package com.tfl.billing;

import com.oyster.*;
import com.tfl.external.Customer;
import com.tfl.external.CustomerDatabase;
import com.tfl.external.PaymentsSystem;

import java.util.*;

public class TravelTracker implements ScanListener {

    private final Hashtable<UUID, CustomerTracker> customerTrackerHashTable= new Hashtable<>();
    private CustomerDatabase databases = CustomerDatabase.getInstance();
    private PaymentsSystem payments = PaymentsSystem.getInstance();

    public void chargeAccounts() {
        List<Customer> customers = databases.getCustomers();
        for (Customer customer : customers) {
            if(customerTrackerHashTable.containsKey(customer.cardId())){
                CustomerTracker tracker = customerTrackerHashTable.get(customer.cardId());
                if(!tracker.getJourneys().isEmpty()){
                    PaymentCalculator calculator = new PaymentCalculator(tracker.getFares());
                    calculator.didCustomerRideDuringPeakHours(tracker.checkForPeakJourney());
                    payments.charge(customer, tracker.getJourneys(), calculator.calculate());
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
