
CREATE TABLE tasks (
    id           UUID            NOT NULL DEFAULT gen_random_uuid(),
    title        VARCHAR(255)    NOT NULL,
    description  TEXT,
    completed    BOOLEAN         NOT NULL DEFAULT false,
    due_date     TIMESTAMPTZ,
    owner_id     UUID            NOT NULL,
    category_id  UUID,
    created_at   TIMESTAMPTZ     NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ     NOT NULL DEFAULT now(),

    CONSTRAINT pk_tasks PRIMARY KEY (id),
    CONSTRAINT fk_tasks_owner FOREIGN KEY (owner_id)
        REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_tasks_category FOREIGN KEY (category_id)
        REFERENCES categories (id) ON DELETE CASCADE
);

CREATE INDEX idx_tasks_owner ON tasks (owner_id);
CREATE INDEX idx_tasks_owner_completed ON tasks (owner_id, completed);
CREATE INDEX idx_tasks_due_date ON tasks (due_date);
CREATE INDEX idx_tasks_category ON tasks (category_id);
CREATE INDEX idx_tasks_overdue ON tasks (owner_id, due_date) WHERE completed = false;
