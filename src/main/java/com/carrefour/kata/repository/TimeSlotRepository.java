package com.carrefour.kata.repository;

import com.carrefour.kata.model.DeliveryMethod;
import com.carrefour.kata.model.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
    List<TimeSlot> findByMethodAndIsAvailableTrue(DeliveryMethod method);
}