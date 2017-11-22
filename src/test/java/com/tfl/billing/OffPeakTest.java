package com.tfl.billing;

import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class OffPeakTest {
    static final BigDecimal OFF_PEAK_SHORT_JOURNEY_PRICE = new BigDecimal(1.60);
    static final BigDecimal OFF_PEAK_LONG_JOURNEY_PRICE = new BigDecimal(2.70);
    @Test
    public void twentySixMinsIsALongJourney(){
        OffPeak offPeak = new OffPeak(26*60);
        assertThat(offPeak.price(), is(OFF_PEAK_LONG_JOURNEY_PRICE));
    }
    @Test
    public void twentyFourMinsIsAShortJourney(){
        OffPeak offPeak = new OffPeak(24*60);
        assertThat(offPeak.price(), is(OFF_PEAK_SHORT_JOURNEY_PRICE));
    }
}