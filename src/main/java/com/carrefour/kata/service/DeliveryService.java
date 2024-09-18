package com.carrefour.kata.service;


import com.carrefour.kata.exception.BookingFailureException;
import com.carrefour.kata.exception.TimeSlotUnavailableException;
import com.carrefour.kata.model.Delivery;
import com.carrefour.kata.model.DeliveryMethod;
import com.carrefour.kata.model.TimeSlot;
import com.carrefour.kata.repository.DeliveryRepository;
import com.carrefour.kata.repository.TimeSlotRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeliveryService {
    private final TimeSlotRepository timeSlotRepository;
    private final DeliveryRepository deliveryRepository;

    public DeliveryService(TimeSlotRepository timeSlotRepository, DeliveryRepository deliveryRepository) {
        this.timeSlotRepository = timeSlotRepository;
        this.deliveryRepository = deliveryRepository;
    }

    @Cacheable("availableMethods")
    public List<DeliveryMethod> getAvailableMethods() {
        return List.of(DeliveryMethod.values());
    }

    public List<TimeSlot> getAvailableTimeSlots(DeliveryMethod method) {
        List<TimeSlot> timeSlots = timeSlotRepository.findByMethodAndIsAvailableTrue(method);
        if (timeSlots.isEmpty()) {
            throw new TimeSlotUnavailableException("No available time slots for the selected delivery method.");
        }
        return timeSlots;
    }

    public Delivery bookDelivery(String customerName, DeliveryMethod method, Long timeSlotId) {
        TimeSlot timeSlot = timeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> new TimeSlotUnavailableException("The selected time slot is not available."));

        if (!timeSlot.isAvailable()) {
            throw new TimeSlotUnavailableException("The selected time slot is already booked.");
        }

        try {
            timeSlot.setAvailable(false);
            timeSlotRepository.save(timeSlot);

            Delivery delivery = Delivery.builder()
                    .method(method)
                    .timeSlot(timeSlot)
                    .customerName(customerName)
                    .status("Pending")
                    .build();

            return deliveryRepository.save(delivery);
        } catch (Exception e) {
            throw new BookingFailureException("Failed to book the delivery.");
        }
    }
}