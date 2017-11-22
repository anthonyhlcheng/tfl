package com.tfl.billing;

import com.oyster.OysterCard;
import com.tfl.external.Customer;
import sun.security.krb5.internal.Ticket;

import java.math.BigDecimal;
import java.util.*;

public class CustomerTracker{
    private UUID cardId;
    private Journey currentJourney;
    private List<Journey> customerJourney = new ArrayList<>();
    private List<TicketType> tickets = new ArrayList<>();
    private List<BigDecimal> fares = new ArrayList<>();
    private boolean currentlyTravelling = false;
    public CustomerTracker(UUID cardId){
        this.cardId = cardId;
    }
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
        if(peak(journey)){
            tickets.add(new Peak(journeyDuration));
            fares.add(tickets.get(tickets.size()-1).price());
        }else{
            tickets.add(new OffPeak(journeyDuration));
            fares.add(tickets.get(tickets.size()-1).price());
        }
    }
    public void addEvent(UUID cardId, UUID readerId){
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
    public boolean checkForPeakJourney(){
        for(TicketType ticket:tickets){
            if(ticket instanceof Peak){
                return true;
            }
        }
        return false;
    }
    public List<Journey> getJourneys(){
        return customerJourney;
    }

    public List<BigDecimal> getFares(){
        return fares;
    }


}
