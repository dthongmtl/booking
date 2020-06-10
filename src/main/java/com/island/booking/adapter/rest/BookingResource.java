package com.island.booking.adapter.rest;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Data;

@Data
public class BookingResource {
	private UUID id;
	private String email;
	private String firstName;
	private String lastName;
	private LocalDate from;
	private LocalDate to;
}
