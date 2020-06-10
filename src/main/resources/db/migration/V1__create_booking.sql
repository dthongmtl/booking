create table bookings (
    id varchar(36) primary key,
    email varchar(100) not null,
    first_name varchar(100) not null,
    last_name varchar(100) not null
);

create table booked_days (
    booked_day date primary key,
    booking_id varchar(36) not null references bookings(id) on delete cascade
);
