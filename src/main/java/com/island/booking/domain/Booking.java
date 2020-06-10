package com.island.booking.domain;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.util.StringUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
	private UUID id;
	private String email;
	private String firstName;
	private String lastName;
	private LocalDate from;
	private LocalDate to;

	public List<String> isValid() {
		List<String> reasons = new ArrayList<>();
		if (StringUtils.isEmpty(email)) {
			reasons.add("email is required");
		}
		if (StringUtils.isEmpty(firstName)) {
			reasons.add("firstName is required");
		}
		if (StringUtils.isEmpty(lastName)) {
			reasons.add("lastName is required");
		}
		if (Objects.isNull(from)) {
			reasons.add("from is required");
		} else {
			if (from.isAfter(LocalDate.now())) {
				reasons.add("from is not in the future");
			}
		}
		if (Objects.isNull(to)) {
			reasons.add("from is required");
		}
		if (!Objects.isNull(from) && !Objects.isNull(to)) {
			if (!to.isAfter(from)) {
				reasons.add("[from, to[ is not a valid period");
			}
			if (from.until(to, ChronoUnit.DAYS) > 3) {
				reasons.add("booking period cannot exceed 3 days");
			}
		}
		return reasons;
	}
}
