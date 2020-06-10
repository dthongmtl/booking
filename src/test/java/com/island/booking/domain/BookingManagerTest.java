package com.island.booking.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DuplicateKeyException;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class BookingManagerTest {
	@Mock
	BookingRepository repository;

	@Captor
	ArgumentCaptor<LocalDate> fromCaptor;
	@Captor
	ArgumentCaptor<LocalDate> toCaptor;

	@InjectMocks
	BookingManagerImpl bookingManager;

	@Test
	public void test_findAvailablePeriods_default_to_one_month() {
		bookingManager.findAvailablePeriods(Optional.empty(), Optional.empty());
		verify(repository).findBookedDays(fromCaptor.capture(), toCaptor.capture());
		assertThat(fromCaptor.getValue()).isToday();
		assertThat(toCaptor.getValue()).isEqualTo(LocalDate.now().plusMonths(1));
	}

	@Test
	public void test_findAvailablePeriods_all_free() {
		doReturn(Lists.emptyList()).when(repository).findBookedDays(any(LocalDate.class), any(LocalDate.class));

		List<LocalDate> results = bookingManager.findAvailablePeriods(Optional.of(LocalDate.now()), Optional.of(LocalDate.now().plusWeeks(1)));

		assertThat(results).hasSize(7).containsSequence(LocalDate.now().datesUntil(LocalDate.now().plusWeeks(1)).collect(Collectors.toList()));
	}

	@Test
	public void test_findAvailablePeriods_all_busy() {
		doReturn(LocalDate.now().datesUntil(LocalDate.now().plusWeeks(1)).collect(Collectors.toList())).when(repository).findBookedDays(any(LocalDate.class), any(LocalDate.class));

		List<LocalDate> results = bookingManager.findAvailablePeriods(Optional.of(LocalDate.now()), Optional.of(LocalDate.now().plusWeeks(1)));

		assertThat(results).isEmpty();
	}

	@Test
	public void test_findAvailablePeriods() {
		doReturn(LocalDate.now().datesUntil(LocalDate.now().plusDays(3)).collect(Collectors.toList())).when(repository).findBookedDays(any(LocalDate.class), any(LocalDate.class));

		List<LocalDate> results = bookingManager.findAvailablePeriods(Optional.of(LocalDate.now()), Optional.of(LocalDate.now().plusWeeks(1)));

		assertThat(results).hasSize(4).containsSequence(LocalDate.now().plusDays(3).datesUntil(LocalDate.now().plusWeeks(1)).collect(Collectors.toList()));
	}

	@Test
	public void test_bookPeriod_notAvailable() {
		Booking booking = Booking.builder().email("a@b.com").firstName("John").lastName("Doe").from(LocalDate.now().plusDays(1)).to(LocalDate.now().plusDays(2)).build();
		doThrow(new DuplicateKeyException("Duplicated key")).when(repository).createBooking(booking);

		Optional<UUID> uuid = bookingManager.bookPeriod(booking);

		assertThat(uuid).isEmpty();
	}

	@Test
	public void test_bookPeriod_available() {
		Booking booking = Booking.builder().email("a@b.com").firstName("John").lastName("Doe").from(LocalDate.now().plusDays(1)).to(LocalDate.now().plusDays(2)).build();
		UUID generatedId = UUID.randomUUID();
		doReturn(generatedId).when(repository).createBooking(booking);

		Optional<UUID> uuid = bookingManager.bookPeriod(booking);

		assertThat(uuid).isPresent().hasValue(generatedId);
	}

	@Test
	public void test_cancelBooking_valid_booking() {
		UUID id = UUID.randomUUID();
		doReturn(true).when(repository).deleteBooking(id);
		assertThat(bookingManager.cancelBooking(id)).isTrue();
	}

	@Test
	public void test_cancelBooking_invalid_booking() {
		UUID id = UUID.randomUUID();
		doReturn(false).when(repository).deleteBooking(id);
		assertThat(bookingManager.cancelBooking(id)).isFalse();
	}

	@Test
	public void test_cancelBooking_repository_error() {
		UUID id = UUID.randomUUID();
		doThrow(new DataAccessResourceFailureException("Error")).when(repository).deleteBooking(id);
		assertThat(bookingManager.cancelBooking(id)).isFalse();
	}
}
