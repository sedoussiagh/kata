package com.carrefour.kata.controller;

import com.carrefour.kata.model.Delivery;
import com.carrefour.kata.model.DeliveryMethod;
import com.carrefour.kata.model.TimeSlot;
import com.carrefour.kata.service.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/delivery")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @Operation(summary = "Get available delivery methods", description = "Returns a list of available delivery methods such as DRIVE, DELIVERY, DELIVERY_TODAY, DELIVERY_ASAP.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of delivery methods"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/delivery-methods")
    public CollectionModel<DeliveryMethod> getDeliveryMethods() {
        List<DeliveryMethod> methods = deliveryService.getAvailableMethods();

        // Add self link to the response
        Link selfLink = linkTo(methodOn(DeliveryController.class).getDeliveryMethods()).withSelfRel();

        // Return the methods wrapped in an EntityModel with the link
        return CollectionModel.of(methods, selfLink);
    }

    @Operation(summary = "Get available time slots", description = "Returns a list of available time slots for a given delivery method. The slots depend on the delivery type and availability.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of time slots"),
            @ApiResponse(responseCode = "400", description = "Invalid delivery method provided"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/time-slots")
    public CollectionModel<TimeSlot> getAvailableTimeSlots(
            @Parameter(description = "The delivery method to retrieve time slots for", example = "DELIVERY")
            @RequestParam DeliveryMethod method) {
        List<TimeSlot> timeSlots = deliveryService.getAvailableTimeSlots(method);

        // Add self link and a link to book a delivery
        Link selfLink = linkTo(methodOn(DeliveryController.class).getAvailableTimeSlots(method)).withSelfRel();
        List<EntityModel<TimeSlot>> timeSlotResources = timeSlots.stream()
                .map(slot -> EntityModel.of(slot,
                        linkTo(methodOn(DeliveryController.class).bookDelivery("", method, slot.getId())).withRel("book-delivery")
                ))
                .toList();

        return CollectionModel.of(timeSlots, selfLink);    }

    @Operation(summary = "Book a delivery", description = "Books a delivery for a customer with a specified delivery method and time slot.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully booked the delivery",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Delivery.class))),
            @ApiResponse(responseCode = "400", description = "Invalid time slot or method provided"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/book")
    public EntityModel<Delivery> bookDelivery(
            @Parameter(description = "Customer name for the delivery", example = "John Doe")
            @RequestParam String customerName,

            @Parameter(description = "The delivery method", example = "DELIVERY_ASAP")
            @RequestParam DeliveryMethod method,

            @Parameter(description = "The ID of the time slot to book", example = "1")
            @RequestParam Long timeSlotId) {
        Delivery delivery = deliveryService.bookDelivery(customerName, method, timeSlotId);

        // Add self link and link to get the delivery status
        Link selfLink = linkTo(methodOn(DeliveryController.class).bookDelivery(customerName, method, timeSlotId)).withSelfRel();
        Link statusLink = linkTo(methodOn(DeliveryController.class).getDeliveryMethods()).withRel("get-delivery-status");

        return EntityModel.of(delivery, selfLink, statusLink);
    }
}
