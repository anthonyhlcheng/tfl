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

    //Check if a journey is made during peak times using a Journey object or time
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
        //Check what type of Journey is made and add the TicketType and price to the list
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
        //If the person is not travelling, create a new journey and create a start event
        if(!currentlyTravelling){
            //Create a new event since start of new journey
            currentJourney = new Journey();
            currentJourney.createStartEvent(cardId, readerId);
            currentlyTravelling = true;
        }else{
            //Person has finished their journey. Create an end event, find the ticket type and reset travellingvariables
            currentJourney.createEndEvent(cardId, readerId);
            customerJourney.add(currentJourney);
            findTicketType(currentJourney);
            currentJourney = null;
            currentlyTravelling = false;
        }
    }
    //This should only be used for testing purposes - replica of addEvent above
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
    //Checks through the TicketType list to see if a peak journey is made
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
