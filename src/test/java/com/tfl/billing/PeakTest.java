package com.tfl.billing;

import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class PeakTest {
    static final BigDecimal PEAK_SHORT_JOURNEY_PRICE = new BigDecimal(2.90);
    static final BigDecimal PEAK_LONG_JOURNEY_PRICE = new BigDecimal(3.80);
    @Test
    public void twentySixMinsIsALongJourney(){
        Peak peak = new Peak(26*60);
        assertThat(peak.price(), is(PEAK_LONG_JOURNEY_PRICE));
    }
    @Test
    public void twentyFourMinsIsAShortJourney(){
        Peak peak = new Peak(24*60);
        assertThat(peak.price(), is(PEAK_SHORT_JOURNEY_PRICE));
    }
}