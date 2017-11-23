package com.tfl.billing;

import com.oyster.OysterCard;
import com.tfl.external.Customer;
import com.tfl.external.CustomerDatabase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class TestDatabase{
    private static TestDatabase instance;
    private List<Customer> customers = new ArrayList<Customer>(){{
        this.add(new Customer("John Smith", new OysterCard("2026775e-5b6a-45af-a551-5600575ea1b0")));
    }};
    private TestDatabase(){}

    public static synchronized TestDatabase getInstance(){
        if (instance == null){
            instance = new TestDatabase();
        }
        return instance;
    }
    public List<Customer> getCustomers(){
        return customers;
    }
    public boolean isRegisteredId(UUID cardId){
        Iterator i$ = this.customers.iterator();

        Customer customer;
        do {
            if (!i$.hasNext()) {
                return false;
            }

            customer = (Customer)i$.next();
        } while(!customer.cardId().equals(cardId));

        return true;
    }
}
