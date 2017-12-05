package com.tfl.billing;

import com.tfl.external.Customer;
import com.tfl.external.CustomerDatabase;

import java.util.List;
import java.util.UUID;

public class CustomerDatabaseAdapter implements CustomerDatabaseInterface {

    private CustomerDatabase customerDatabase = CustomerDatabase.getInstance();

    @Override
    public List<Customer> getCustomers() {
        return customerDatabase.getCustomers();
    }

    @Override
    public boolean isRegisteredId(UUID cardId) {
        return customerDatabase.isRegisteredId(cardId);
    }
}
