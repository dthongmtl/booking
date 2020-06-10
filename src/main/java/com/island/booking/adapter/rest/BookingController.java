package com.island.booking.adapter.rest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.island.booking.domain.Booking;
import com.island.booking.domain.BookingManager;

@RestController
@RequiredArgsConstructor
public class BookingController {
	private final BookingManager manager;

	@GetMapping(value = "schedule", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<LocalDate> getSchedule(@RequestParam("from") Optional<LocalDate> from, @RequestParam("to") Optional<LocalDate> to) {
		return manager.findAvailablePeriods(from, to);
	}

	@PostMapping(value = "bookings", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity create(@RequestBody BookingResource resource) {
		Booking booking = Booking.builder().email(resource.getEmail()).firstName(resource.getFirstName()).lastName(resource.getLastName()).from(resource.getFrom()).to(resource.getTo()).build();
		List<String> reasons = booking.isValid();
		if (reasons.isEmpty()) {
			Optional<UUID> uuid = manager.bookPeriod(booking);
			return uuid.map(v -> {
				resource.setId(v);
				return ResponseEntity.ok(resource);
			}).map(ResponseEntity.class::cast)
					.orElse(ResponseEntity.status(HttpStatus.CONFLICT).body("Cannot Booked this period"));
		} else {
			return ResponseEntity.badRequest().body("Booking is not valid: " + reasons.stream().collect(Collectors.joining(", ")));
		}
	}

	@DeleteMapping(value = "bookings/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> delete(@PathVariable("id") String id) {
		if (manager.cancelBooking(UUID.fromString(id))) {
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}
}
