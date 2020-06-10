package com.island.booking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.island.booking.domain.BookingManager;
import com.island.booking.domain.BookingManagerImpl;
import com.island.booking.domain.BookingRepository;

@SpringBootApplication
@EnableTransactionManagement
public class BookingApplication {
	public static void main(String[] args) {
		SpringApplication.run(BookingApplication.class, args);
	}

	@Bean
	public BookingManager bookingManager(BookingRepository repository) {
		return new BookingManagerImpl(repository);
	}
}
