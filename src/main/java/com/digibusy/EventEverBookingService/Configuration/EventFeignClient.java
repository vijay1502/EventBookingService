package com.digibusy.EventEverBookingService.Configuration;

import com.digibusy.EventEverBookingService.Model.EventResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "EventEverEventService",url = "http://localhost:8081",  configuration = FeignClientConfiguration.class)
public interface EventFeignClient {
//    @RequestMapping(method = RequestMethod.GET, value = "/event/event/{id}")
//    Object getEventById(@PathVariable("id") long id);
//    @RequestMapping(method = RequestMethod.PUT, value = "/event/{id}/updateSeats")
//    void updateAvailableSeats(@PathVariable("id") long id, @RequestParam int seats);
}
