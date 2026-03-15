CREATE DATABASE IF NOT EXISTS campus_room_status;
DROP TABLE IF EXISTS events;
DROP TABLE IF EXISTS rooms;
DROP TABLE IF EXISTS buildings;

CREATE TABLE IF NOT EXISTS buildings (
    id SERIAL PRIMARY KEY,
    building_google_id VARCHAR(255) UNIQUE NOT NULL,
    building_name VARCHAR(255) NOT NULL,
    building_floors TEXT[],
    building_address JSONB
);

CREATE TABLE IF NOT EXISTS rooms (
    id SERIAL NOT NULL,
    room_google_id VARCHAR(255) NOT NULL,
    code VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    resource_email VARCHAR(255),
    resource_type VARCHAR(255),
    floor VARCHAR(255),
    capacity INT,
    building_id INT REFERENCES buildings(id),
    PRIMARY KEY(id),
    UNIQUE(room_google_id),
    UNIQUE(code)
);

CREATE TABLE IF NOT EXISTS events (
    id SERIAL NOT NULL,
    event_google_id VARCHAR(255) NOT NULL,
    room_id INT REFERENCES rooms(id),
    title VARCHAR(255),
    organizer_email VARCHAR(255),
    start_time TIMESTAMP WITH TIME ZONE NOT NULL,
    end_time TIMESTAMP WITH TIME ZONE NOT NULL,
    status VARCHAR(50),
    last_synced_at TIMESTAMP WITH TIME ZONE,
    PRIMARY KEY(id),
    UNIQUE(event_google_id)
);