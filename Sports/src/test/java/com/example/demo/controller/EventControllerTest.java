package com.example.demo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.example.demo.service.EventService;

@WebMvcTest(EventController.class)
class EventControllerTest {
    @Autowired 
    private MockMvc mockMvc;
    @MockitoBean 
    private EventService eventService;

    @Test
    void testPostEvent_Returns200() throws Exception {
        mockMvc.perform(post("/events/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"eventID\":\"10\",\"status\":\"LIVE\"}"))
                .andExpect(status().isOk());
    }
}