package com.island.booking.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingManager {
	List<LocalDate> findAvailablePeriods(Optional<LocalDate> from, Optional<LocalDate> to);
	Optional<UUID> bookPeriod(Booking booking);
	boolean cancelBooking(UUID bookingId);
}
