package com.example.demo.service;

import com.example.demo.dao.EventRepo;
import com.example.demo.model.Event;
import com.example.demo.model.EventResponse;
import com.example.demo.model.EventType;
import com.example.demo.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.time.Duration;
import java.util.concurrent.ScheduledFuture;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    // 1. Spy allows the real ConcurrentHashMap in EventRepo to function
    @Spy
    private EventRepo eventrepo = new EventRepo(); 

    @Mock
    private ThreadPoolTaskScheduler taskScheduler;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @Mock
    private APIClientService apiClientService;

    // 2. Mock the Future to prevent NPE when putting into activeTimers map
    @Mock
    private ScheduledFuture<Object> scheduledFuture;

    @InjectMocks
    private EventService eventService;

    @BeforeEach
    void setup() {
        // Explicitly wiring the spy to ensure the service uses the one with the real map
        ReflectionTestUtils.setField(eventService, "eventrepo", eventrepo);
    }

    @Test
    @DisplayName("Scenario 1: Save LIVE -> Start Timer successfully")
    void testSaveStatus_Live_StartsTimer() {
        // Arrange
        Event event = new Event();
        event.setEventID("10");
        event.setStatus(Status.LIVE);

        // 3. doReturn fixes the "cannot resolve thenReturn" generic type issue
        doReturn(scheduledFuture).when(taskScheduler)
            .scheduleAtFixedRate(any(Runnable.class), eq(Duration.ofSeconds(10)));

        // Act
        eventService.saveStatus(event);

        // Assert
        // Verify it was saved in your In-Memory Storage
        assertNotNull(eventrepo.findById("10"), "Event should exist in the repository map");
        
        // Verify the scheduler was actually triggered
        verify(taskScheduler, times(1)).scheduleAtFixedRate(any(Runnable.class), eq(Duration.ofSeconds(10)));
        
        System.out.println("Test passed: Event saved and Timer scheduled without NPE.");
    }
    
    @Test
    @DisplayName("Scenario 2: API Down -> Background task handles error")
    void testBackgroundTimer_ApiDown() {
        Event event = new Event();
        event.setEventID("10");
        event.setStatus(Status.LIVE);

        // Capture the Runnable that is sent to the scheduler
        org.mockito.ArgumentCaptor<Runnable> runnableCaptor = org.mockito.ArgumentCaptor.forClass(Runnable.class);
        doReturn(scheduledFuture).when(taskScheduler).scheduleAtFixedRate(runnableCaptor.capture(), any(Duration.class));

        eventService.saveStatus(event);

        // Simulate API failure when the timer "ticks"
        when(apiClientService.fetchEventData("10")).thenThrow(new RuntimeException("Foreign API Timeout"));

        // Manually trigger the background task
        assertDoesNotThrow(() -> runnableCaptor.getValue().run());

        // Verify Kafka was NEVER called because API failed
        verify(kafkaProducerService, never()).publishEventUpdate(anyString(), any());
        System.out.println("Test passed: Background task survived API failure.");
    }
    
    @Test
    @DisplayName("Scenario 3: Kafka Down -> Verify retry attempt")
    void testBackgroundTimer_KafkaDown() {
        Event event = new Event();
        event.setEventID("10");
        event.setStatus(Status.LIVE);

        org.mockito.ArgumentCaptor<Runnable> runnableCaptor = org.mockito.ArgumentCaptor.forClass(Runnable.class);
        doReturn(scheduledFuture).when(taskScheduler).scheduleAtFixedRate(runnableCaptor.capture(), any(Duration.class));

        eventService.saveStatus(event);

        // Mock API success but Kafka failure
        when(apiClientService.fetchEventData("10")).thenReturn(new EventResponse("10", EventType.EMERGENCY_ALARM));
        doThrow(new RuntimeException("Kafka Broker Down")).when(kafkaProducerService).publishEventUpdate(anyString(), any());

        // Trigger the background task
        runnableCaptor.getValue().run();

        // Verify Kafka was at least attempted
        verify(kafkaProducerService, times(1)).publishEventUpdate(eq("10"), any());
        System.out.println("Test passed: Logic attempted Kafka publish despite broker being down.");
    }
    
    @Test
    @DisplayName("Scenario 5: Stop Timer -> Verify task cancellation")
    void testSaveStatus_NonLive_StopsExistingTimer() {
        // 1. Setup a LIVE event first to get a timer in the map
        Event event = new Event();
        event.setEventID("10");
        event.setStatus(Status.LIVE);

        doReturn(scheduledFuture).when(taskScheduler)
            .scheduleAtFixedRate(any(Runnable.class), any(Duration.class));

        eventService.saveStatus(event); // Timer starts

        // 2. Now change status to COMPLETED
        event.setStatus(Status.NON_LIVE);
        eventService.saveStatus(event);

        // 3. Assert: The task should have been cancelled
        verify(scheduledFuture, times(1)).cancel(false);
        System.out.println("Test passed: Timer successfully cancelled for NON_LIVE status.");
    }
    
    
    
}