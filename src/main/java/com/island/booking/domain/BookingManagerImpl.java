package com.island.booking.domain;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BookingManagerImpl implements BookingManager {
	final BookingRepository bookingRepository;

	@Override
	public List<LocalDate> findAvailablePeriods(Optional<LocalDate> inputFrom, Optional<LocalDate> inputTo) {
		LocalDate from = inputFrom.orElse(LocalDate.now());
		LocalDate to = inputTo.orElse(from.plusMonths(1));
		List<LocalDate> bookedDays = bookingRepository.findBookedDays(from, to);
		return from.datesUntil(to, Period.ofDays(1)).filter(date -> !bookedDays.contains(date)).collect(Collectors.toList());
	}

	@Override
	public Optional<UUID> bookPeriod(Booking booking) {
		try {
			return Optional.of(bookingRepository.createBooking(booking));
		} catch (RuntimeException rte) {
			return Optional.empty();
		}
	}

	@Override
	public boolean cancelBooking(UUID bookingId) {
		try {
			return bookingRepository.deleteBooking(bookingId);
		} catch (RuntimeException th) {
			return false;
		}
	}
}
