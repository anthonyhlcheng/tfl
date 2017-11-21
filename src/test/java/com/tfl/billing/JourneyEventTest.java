package com.tfl.billing;

import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class JourneyEventTest {

    private final UUID cardID = UUID.randomUUID();
    private final UUID readerID = UUID.randomUUID();
    private final long entryTime = System.currentTimeMillis();
    private final long exitTime = entryTime + (30*60*1000);

    @Test
    public void areJourneyDetailsCorrect(){
        JourneyEvent start = new JourneyStart(cardID, readerID, entryTime);
        JourneyEvent end = new JourneyEnd(cardID, readerID, exitTime);
        assertThat(start.cardId(), is(cardID));
        assertThat(start.readerId(), is(readerID));
        assertThat(start.time(), is(entryTime));
        assertThat(end.cardId(), is(cardID));
        assertThat(end.readerId(), is(readerID));
        assertThat(end.time(), is(exitTime));
    }

}