package com.tfl.billing;

import com.tfl.external.Customer;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

public class TravelTrackerTest {
    @Test
    public class PaymentsSystem{
        PaymentsSystem instance;
        public PaymentsSystem getInstance(){
            if(instance == null){
                instance = new PaymentsSystem();
            }
            return instance;
        }
        private PaymentsSystem(){}
        public BigDecimal charge(Customer customer, List<Journey> journeys, BigDecimal total){
            return total;
        }
    }
}

