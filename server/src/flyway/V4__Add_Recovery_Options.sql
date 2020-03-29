CREATE TABLE recovery_code (
    code text PRIMARY KEY,
    created_at timestamp without time zone,
    user_id uuid
);

CREATE TABLE recovery_request (
    created_at timestamp without time zone PRIMARY KEY,
    device_id uuid,
    user_id uuid,
    user_comment text
);

ALTER TABLE "public"."device_info" ADD COLUMN "fingerprint" text;
ALTER TABLE "public"."device_info" ADD COLUMN "browser_fingerprint" text;

