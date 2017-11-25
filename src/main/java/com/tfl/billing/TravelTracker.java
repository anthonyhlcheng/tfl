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
        //Get each Customer tracker and get the list of fares to pass onto PaymentCalculator
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
        //Connect all associated station card readers
        for (OysterCardReader cardReader : cardReaders) {
            cardReader.register(this);
        }
    }

    @Override
    public void cardScanned(UUID cardId, UUID readerId) {
        //Check for the card in the database
        boolean checkCard = databases.isRegisteredId(cardId);
        if (!checkCard) {
            throw new UnknownOysterCardException(cardId);
        }
        //Create or retrieve a customerTracker and add the current journey event to it
        if(!customerTrackerHashTable.containsKey(cardId)){
            customerTrackerHashTable.put(cardId, new CustomerTracker());
        }
        CustomerTracker tracker = customerTrackerHashTable.get(cardId);
        tracker.addEvent(cardId, readerId);
    }

    //Retrieve Customer Tracker (for testing only)
    public CustomerTracker getCustomerTracker(UUID cardId){
        return customerTrackerHashTable.get(cardId);
    }

    //Changing of Database and PaymentSystem made possible
    public void setCustomerDatabase(CustomerDatabase db){
        this.databases = db;
    }
    public void setPaymentSystem(PaymentsSystem ps){
        this.payments = ps;
    }
}
