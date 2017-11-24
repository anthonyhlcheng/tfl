package com.tfl.billing;

import java.math.BigDecimal;
import java.util.*;

class CustomerTracker{
    private Journey currentJourney;
    private List<Journey> customerJourney = new ArrayList<>();
    private List<TicketType> tickets = new ArrayList<>();
    private List<BigDecimal> fares = new ArrayList<>();
    private boolean currentlyTravelling = false;

    CustomerTracker(){}
    private boolean peak(Journey journey) {
        return peak(journey.startTime()) || peak(journey.endTime());
    }
    private boolean peak(Date time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return (hour >= 6 && hour <= 9) || (hour >= 17 && hour <= 19);
    }
    private void findTicketType(Journey journey){
        int journeyDuration = journey.durationSeconds();
        TicketType ticket;
        if(peak(journey)){
            ticket = new Peak(journeyDuration);
        }else{
            ticket = new OffPeak(journeyDuration);
        }
        tickets.add(ticket);
        fares.add(ticket.price());
    }
    void addEvent(UUID cardId, UUID readerId){
        if(!currentlyTravelling){
            //Create a new event since start of new journey
            currentJourney = new Journey();
            currentJourney.createStartEvent(cardId, readerId);
            currentlyTravelling = true;
        }else{
            currentJourney.createEndEvent(cardId, readerId);
            customerJourney.add(currentJourney);
            findTicketType(currentJourney);
            currentJourney = null;
            currentlyTravelling = false;
        }
    }
    void addEvent(UUID cardId, UUID readerId, long time){
        if(!currentlyTravelling){
            //Create a new event since start of new journey
            currentJourney = new Journey();
            currentJourney.createStartEvent(cardId, readerId, time);
            currentlyTravelling = true;
        }else{
            currentJourney.createEndEvent(cardId, readerId, time);
            customerJourney.add(currentJourney);
            findTicketType(currentJourney);
            currentJourney = null;
            currentlyTravelling = false;
        }
    }
    boolean checkForPeakJourney(){
        for(TicketType ticket:tickets){
            if(ticket instanceof Peak){
                return true;
            }
        }
        return false;
    }
    List<Journey> getJourneys(){
        return customerJourney;
    }

    List<BigDecimal> getFares(){
        return fares;
    }


}
