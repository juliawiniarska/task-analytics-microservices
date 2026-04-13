-- V1: Utworzenie tabeli zadań
-- Migracja Flyway dla usługi operacyjnej

CREATE TABLE tasks (
    id              BIGSERIAL       PRIMARY KEY,
    title           VARCHAR(255)    NOT NULL,
    description     TEXT,
    status          VARCHAR(20)     NOT NULL DEFAULT 'TODO',
    priority        VARCHAR(10)     NOT NULL DEFAULT 'MEDIUM',
    due_date        DATE,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at    TIMESTAMP
);

-- Indeks na statusie (częste filtrowanie)
CREATE INDEX idx_tasks_status ON tasks(status);

-- Indeks na dacie utworzenia (sortowanie)
CREATE INDEX idx_tasks_created_at ON tasks(created_at DESC);

-- Komentarz do tabeli
COMMENT ON TABLE tasks IS 'Tabela zadań usługi operacyjnej';