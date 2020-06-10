package com.island.booking.adapter.repository;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.island.booking.domain.Booking;
import com.island.booking.domain.BookingRepository;

@RequiredArgsConstructor
@Repository
public class BookingRepositoryJdbc implements BookingRepository {
	private final NamedParameterJdbcOperations jdbcOperations;

	@Override
	@Transactional(readOnly = true)
	public List<LocalDate> findBookedDays(LocalDate from, LocalDate to) {
		return jdbcOperations.query("SELECT booked_day FROM booked_days where booked_day between :from and :to order by booked_day asc", Map.of("from", from, "to", to),
				(rs, i) -> rs.getDate("booked_day").toLocalDate());
	}

	@Override
	@Transactional
	public UUID createBooking(Booking booking) {
		UUID uuid = UUID.randomUUID();
		jdbcOperations.update("insert into bookings (id, email, first_name, last_name) values (:id, :email, :firstname, :lastname)", Map.of(
				"id", uuid.toString(),
				"email", booking.getEmail(),
				"firstname", booking.getFirstName(),
				"lastname", booking.getLastName()));
		booking.getFrom().datesUntil(booking.getTo(), Period.ofDays(1)).forEach(date -> jdbcOperations.update("insert into booked_days (booked_day, booking_id) values (:date, :booking)", Map.of("date", date, "booking", uuid.toString())));
		return uuid;
	}

	@Override
	@Transactional
	public boolean deleteBooking(UUID bookingId) {
		return jdbcOperations.update("delete from bookings where id = :id", Map.of("id", bookingId.toString())) > 0;
	}
}
