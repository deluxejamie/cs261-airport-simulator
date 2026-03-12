CREATE TABLE IF NOT EXISTS simulations (
    id VARCHAR(36) PRIMARY KEY,
    status VARCHAR(20) NOT NULL,
    config TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS simulation_results (
    simulation_id VARCHAR(36) PRIMARY KEY,
    max_take_off_queue INTEGER,
    avg_take_off_wait REAL,
    max_hold_queue INTEGER,
    avg_hold_time REAL,
    total_cancellations INTEGER,
    total_diversions INTEGER,
    avg_arrival_delay REAL,
    avg_departure_delay REAL,
    config_data TEXT,
    FOREIGN KEY (simulation_id) REFERENCES simulations(id)
);

CREATE TABLE IF NOT EXISTS event_log_entries (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    simulation_id VARCHAR(36) NOT NULL,
    event_type VARCHAR(50),
    sim_timestamp INTEGER,
    attributes TEXT,
    FOREIGN KEY (simulation_id) REFERENCES simulations(id)
);