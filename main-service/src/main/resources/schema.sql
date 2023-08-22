DROP TABLE IF EXISTS users, categories, locations, events, requests, compilations, compilations_events, comments;

CREATE TABLE IF NOT EXISTS users(
    id bigint generated by default as identity primary key,
    name varchar(255) NOT NULL,
    email varchar(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS categories(
    id bigint generated by default as identity primary key,
    name varchar(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS locations(
    id bigint generated by default as identity primary key,
    lat real NOT NULL,
    lon real NOT NULL
);

CREATE TABLE IF NOT EXISTS events(
    id bigint generated by default as identity primary key,
    annotation varchar(2000) NOT NULL,
    category_id bigint references categories(id),
    description varchar(7000) NOT NULL,
    event_date timestamp,
    location_id bigint references locations(id),
    paid boolean,
    participant_limit integer,
    request_moderation boolean,
    created_on timestamp,
    published_on timestamp,
    state varchar(255),
    user_id bigint references users(id),
    title varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS requests(
    id bigint generated by default as identity primary key,
    created timestamp,
    event_id bigint references events(id),
    requester_id bigint references users(id),
    status varchar(255)
);

CREATE TABLE IF NOT EXISTS compilations(
    id bigint generated by default as identity primary key,
    pinned boolean,
    title varchar(255)
);

CREATE TABLE IF NOT EXISTS compilations_events(
    event_id bigint references events(id),
    compilation_id bigint references compilations(id)
);

CREATE TABLE IF NOT EXISTS comments(
    id bigint generated by default as identity primary key,
    text varchar(2000),
    event_id bigint references events(id),
    user_id bigint references users(id),
    created timestamp
);

