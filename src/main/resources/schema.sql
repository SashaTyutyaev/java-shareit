delete from bookings cascade;
delete from comments cascade;
delete from requests cascade;
delete from items cascade;
delete from users cascade;

alter sequence users_id_seq restart with 1;
alter sequence items_id_seq restart with 1;
alter sequence requests_id_seq restart with 1;
alter sequence bookings_id_seq restart with 1;
alter sequence comments_id_seq restart with 1;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL primary key ,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL unique
);

create table if not exists requests (
    id bigint generated by default as identity not null primary key ,
    description varchar(512) ,
    requestor_id bigint references users(id) on delete cascade not null
);

CREATE TABLE IF NOT EXISTS items (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL primary key,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(512) NOT NULL,
    is_available bool not null,
    owner_id BIGINT references users(id) on delete cascade ,
    request_id bigint references requests(id) on delete cascade
);

create table if not exists comments (
    id bigint generated by default as identity not null primary key ,
    text varchar(512) not null,
    item_id bigint references items(id) on delete cascade not null,
    author_id bigint references users(id) on delete cascade not null
);

create table if not exists bookings (
    id bigint generated by default as identity not null primary key ,
    start_date timestamp without time zone not null,
    end_date timestamp without time zone not null,
    item_id bigint references items(id) on delete cascade not null,
    booker_id bigint references users(id) on delete cascade not null,
    status varchar(10) not null
);

