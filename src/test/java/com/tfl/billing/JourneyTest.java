package com.tfl.billing;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.UUID;

import java.util.Date;

import java.lang.String;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class JourneyTest {
    //Create the JourneyStart and JourneyEnd points
    private UUID cardId = UUID.randomUUID();
    private UUID startStationReaderId = UUID.randomUUID();
    private UUID endStationReaderId = UUID.randomUUID();
    private long entryTime = System.currentTimeMillis();
    private long exitTime = entryTime + (30*60*1000);
    Date entryTimeDate = new Date(entryTime);
    Date exitTimeDate = new Date(exitTime);

    @Test
    public void areJourneyDetailsCorrect(){
        JourneyEvent start = new JourneyStart(cardId, startStationReaderId, entryTime);
        JourneyEvent end = new JourneyEnd(cardId, endStationReaderId, exitTime);
        Journey journey = new Journey(start, end);
        assertThat(journey.originId(), is(startStationReaderId));
        assertThat(journey.destinationId(), is(endStationReaderId));
        assertThat(journey.startTime(), is(entryTimeDate));
        assertThat(journey.endTime(), is(exitTimeDate));
    }
    @Test
    public void emptyJourneyGivesCorrectDetails(){
        Journey journey = new Journey();
        journey.createStartEvent(cardId, startStationReaderId, entryTime);
        journey.createEndEvent(cardId, endStationReaderId, exitTime);
        assertThat(journey.originId(), is(startStationReaderId));
        assertThat(journey.destinationId(), is(endStationReaderId));
        assertThat(journey.startTime(), is(entryTimeDate));
        assertThat(journey.endTime(), is(exitTimeDate));
    }
    @Test
    public void emptyJourney2GivesCorrectDetails(){
        Journey journey = new Journey();
        journey.createStartEvent(cardId, startStationReaderId);
        journey.createEndEvent(cardId, endStationReaderId);
        assertThat(journey.originId(), is(startStationReaderId));
        assertThat(journey.destinationId(), is(endStationReaderId));
    }
    @Test
    public void checkTimeCalculations(){
        JourneyEvent start = new JourneyStart(cardId, startStationReaderId, entryTime);
        JourneyEvent end = new JourneyEnd(cardId, endStationReaderId, exitTime);
        Journey journey = new Journey(start, end);
        assertThat(journey.startTime(), is(entryTimeDate));
        assertThat(journey.endTime(), is(exitTimeDate));
        assertThat(journey.durationMinutes(), is("30:0"));
    }

    @Test
    public void checkTimeFormatting(){
        JourneyEvent start = new JourneyStart(cardId, startStationReaderId, entryTime);
        JourneyEvent end = new JourneyEnd(cardId, endStationReaderId, exitTime);
        Journey journey = new Journey(start, end);
        assertThat(journey.formattedStartTime(), is(SimpleDateFormat.getInstance().format(entryTime)));
        assertThat(journey.formattedEndTime(), is(SimpleDateFormat.getInstance().format(exitTime)));
    }

}