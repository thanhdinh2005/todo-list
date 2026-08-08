CREATE TABLE categories (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    name VARCHAR(255) NOT NULL,
    color_code VARCHAR(50) NOT NULL,
    user_id UUID NOT NULL,
    CONSTRAINT fk_cat_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_categories_user_id ON categories(user_id);
