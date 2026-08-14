CREATE TABLE categories (
    id          UUID            NOT NULL DEFAULT gen_random_uuid(),
    name        VARCHAR(100)    NOT NULL,
    color_code  VARCHAR(7)      NOT NULL,
    owner_id    UUID            NOT NULL,
    created_at  TIMESTAMPTZ     NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ     NOT NULL DEFAULT now(),

    CONSTRAINT pk_categories PRIMARY KEY (id),
    CONSTRAINT fk_categories_owner FOREIGN KEY (owner_id)
        REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT uk_category_owner_name UNIQUE (owner_id, name),
    CONSTRAINT ck_categories_color_code CHECK (color_code ~ '^#[0-9A-Fa-f]{6}$')
);

CREATE INDEX idx_categories_owner ON categories (owner_id);

