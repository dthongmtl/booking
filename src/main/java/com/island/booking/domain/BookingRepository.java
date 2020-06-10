package com.island.booking.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface BookingRepository {
	List<LocalDate> findBookedDays(LocalDate from, LocalDate to);
	UUID createBooking(Booking booking);
	boolean deleteBooking(UUID bookingId);
}
