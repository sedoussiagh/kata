package com.carrefour.kata.service;

import com.carrefour.kata.exception.TimeSlotUnavailableException;
import com.carrefour.kata.model.Delivery;
import com.carrefour.kata.model.DeliveryMethod;
import com.carrefour.kata.model.TimeSlot;
import com.carrefour.kata.repository.DeliveryRepository;
import com.carrefour.kata.repository.TimeSlotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeliveryServiceTest {

    @Mock
    private TimeSlotRepository timeSlotRepository;

    @Mock
    private DeliveryRepository deliveryRepository;

    @InjectMocks
    private DeliveryService deliveryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnAvailableTimeSlots() {
        TimeSlot timeSlot1 = new TimeSlot(1L, DeliveryMethod.DELIVERY, LocalDateTime.now(), LocalDateTime.now().plusHours(2), true);
        TimeSlot timeSlot2 = new TimeSlot(2L, DeliveryMethod.DELIVERY, LocalDateTime.now().plusHours(3), LocalDateTime.now().plusHours(5), true);
        when(timeSlotRepository.findByMethodAndIsAvailableTrue(DeliveryMethod.DELIVERY)).thenReturn(Arrays.asList(timeSlot1, timeSlot2));

        var availableSlots = deliveryService.getAvailableTimeSlots(DeliveryMethod.DELIVERY);

        assertEquals(2, availableSlots.size());
        verify(timeSlotRepository, times(1)).findByMethodAndIsAvailableTrue(DeliveryMethod.DELIVERY);
    }

    @Test
    void shouldThrowExceptionWhenNoAvailableSlots() {
        when(timeSlotRepository.findByMethodAndIsAvailableTrue(DeliveryMethod.DELIVERY)).thenReturn(Arrays.asList());

        assertThrows(TimeSlotUnavailableException.class, () -> deliveryService.getAvailableTimeSlots(DeliveryMethod.DELIVERY));
        verify(timeSlotRepository, times(1)).findByMethodAndIsAvailableTrue(DeliveryMethod.DELIVERY);
    }

    @Test
    void shouldBookDeliverySuccessfully() {
        TimeSlot timeSlot = new TimeSlot(1L, DeliveryMethod.DELIVERY, LocalDateTime.now(), LocalDateTime.now().plusHours(2), true);
        when(timeSlotRepository.findById(1L)).thenReturn(Optional.of(timeSlot));
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(new Delivery());

        Delivery delivery = deliveryService.bookDelivery("John Doe", DeliveryMethod.DELIVERY, 1L);

        assertNotNull(delivery);
        verify(timeSlotRepository, times(1)).findById(1L);
        verify(timeSlotRepository, times(1)).save(timeSlot);
        verify(deliveryRepository, times(1)).save(any(Delivery.class));
    }

    @Test
    void shouldThrowExceptionWhenTimeSlotNotAvailable() {
        TimeSlot timeSlot = new TimeSlot(1L, DeliveryMethod.DELIVERY, LocalDateTime.now(), LocalDateTime.now().plusHours(2), false);
        when(timeSlotRepository.findById(1L)).thenReturn(Optional.of(timeSlot));

        assertThrows(TimeSlotUnavailableException.class, () -> deliveryService.bookDelivery("John Doe", DeliveryMethod.DELIVERY, 1L));
        verify(timeSlotRepository, times(1)).findById(1L);
    }
}
