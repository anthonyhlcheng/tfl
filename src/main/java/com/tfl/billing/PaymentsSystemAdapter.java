package com.tfl.billing;

import com.tfl.external.Customer;
import com.tfl.external.PaymentsSystem;

import java.math.BigDecimal;
import java.util.List;

public class PaymentsSystemAdapter implements PaymentsSystemInterface{
    private PaymentsSystem paymentsSystem = PaymentsSystem.getInstance();

    @Override
    public void charge(Customer customer, List<Journey> journeys, BigDecimal total) {
        paymentsSystem.charge(customer, journeys, total);
    }
}
