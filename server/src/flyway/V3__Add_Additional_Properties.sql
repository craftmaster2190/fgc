CREATE TABLE public.device_info (
    created_at timestamp without time zone PRIMARY KEY,
    last_log_in timestamp without time zone,
    device_id uuid REFERENCES device(id),
    inet_address text,
    user_agent text
);

ALTER TABLE public.device
    ADD COLUMN banned_at timestamp without time zone,
    ADD COLUMN created_at timestamp without time zone;

ALTER TABLE public.appuser
    ADD COLUMN created_at timestamp without time zone;

ALTER TABLE public.family
    ADD COLUMN created_at timestamp without time zone;
