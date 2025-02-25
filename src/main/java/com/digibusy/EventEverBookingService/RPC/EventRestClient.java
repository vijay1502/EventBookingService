package com.digibusy.EventEverBookingService.RPC;

import com.digibusy.EventEverBookingService.Model.EventResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
@Service
public class EventRestClient {
    private final RestClient restClient;

    public EventRestClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public EventResponse getEventById(Long id) {
        return restClient.get()
                .uri("/event/{id}", id)
                .retrieve()
                .body(EventResponse.class);
    }

    public List<EventResponse> getAllEvents() {
        return restClient.get()
                .uri("/event/allEvents")
                .retrieve()
                .body(List.class);
    }

    public void updateAvailableSeats(Long id, int seats) {
        restClient.put()
                .uri("/event/{id}/updateSeats", id)
                .body(seats)
                .retrieve();
    }
}
