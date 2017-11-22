package com.tfl.billing;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Journey {

    private JourneyEvent start;
    private JourneyEvent end;

    public Journey(){

    }
    public Journey(JourneyEvent startEvent, JourneyEvent endEvent) {
        start = startEvent;
        end = endEvent;
    }

    public void createStartEvent(UUID cardId, UUID readerId){
        start = new JourneyStart(cardId, readerId);
    }
    public void createEndEvent(UUID cardId, UUID readerId){
        end = new JourneyEnd(cardId, readerId);
    }

    public UUID originId() {
        return start.readerId();
    }

    public UUID destinationId() {
        return end.readerId();
    }

    public String formattedStartTime() {
        return format(start.time());
    }

    public String formattedEndTime() {
        return format(end.time());
    }

    public Date startTime() {
        return new Date(start.time());
    }

    public Date endTime() {
        return new Date(end.time());
    }

    public int durationSeconds() {
        return (int) ((end.time() - start.time()) / 1000);
    }

    public String durationMinutes() {
        return "" + durationSeconds() / 60 + ":" + durationSeconds() % 60;
    }

    private String format(long time) {
        return SimpleDateFormat.getInstance().format(new Date(time));
    }
}
