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
    private JourneyEvent start = new JourneyStart(cardId, startStationReaderId, entryTime);
    private long exitTime = entryTime + (30*60*1000);
    private JourneyEvent end = new JourneyEnd(cardId, endStationReaderId, exitTime);
    private Journey journey = new Journey(start, end);
    private Date entryTimeDate = new Date(entryTime);
    private Date exitTimeDate = new Date(exitTime);

    @Test
    public void areJourneyDetailsCorrect(){
        assertThat(journey.originId(), is(startStationReaderId));
        assertThat(journey.destinationId(), is(endStationReaderId));
        assertThat(journey.startTime(), is(entryTimeDate));
        assertThat(journey.endTime(), is(exitTimeDate));
    }
    @Test
    public void checkTimeCalculations(){
        //int differenceInSeconds = (int)(exitTime - entryTime) / 60 / 1000;
        //String differenceInMinutes = "" + differenceInSeconds / 60 + ":" + differenceInSeconds % 60;
        assertThat(journey.startTime(), is(entryTimeDate));
        assertThat(journey.endTime(), is(exitTimeDate));
        assertThat(journey.durationMinutes(), is("30:0"));
    }

    @Test
    public void checkTimeFormatting(){
        assertThat(journey.formattedStartTime(), is(SimpleDateFormat.getInstance().format(entryTime)));
        assertThat(journey.formattedEndTime(), is(SimpleDateFormat.getInstance().format(exitTime)));
    }

}