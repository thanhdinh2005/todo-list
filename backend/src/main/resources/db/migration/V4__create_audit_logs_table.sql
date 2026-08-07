CREATE TABLE audit_logs
(
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    entity_name  VARCHAR(100) NOT NULL,
    entity_id    VARCHAR(100) NOT NULL,
    action       VARCHAR(20)  NOT NULL,
    performed_by VARCHAR(100),
    performed_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    old_value    TEXT,
    new_value    TEXT
);

CREATE INDEX idx_audit_logs_entity ON audit_logs (entity_name, entity_id);
CREATE INDEX idx_audit_logs_performed_at ON audit_logs (performed_at);
