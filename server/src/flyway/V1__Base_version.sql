--
-- PostgreSQL database dump
--

-- Dumped from database version 12.2
-- Dumped by pg_dump version 12.2
/*
SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;
*/
--
-- Name: answer; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.answer (
    question_id bigint NOT NULL,
    user_id uuid NOT NULL,
    values_persisted text
);


--
-- Name: appuser; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.appuser (
    id uuid NOT NULL,
    family_id uuid,
    is_admin boolean,
    name text,
    profile_image bytea
);


--
-- Name: appuser_devices; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.appuser_devices (
    users_id uuid NOT NULL,
    devices_id uuid NOT NULL
);


--
-- Name: chat; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.chat (
    id timestamp without time zone NOT NULL,
    user_id uuid NOT NULL,
    value text
);


--
-- Name: device; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.device (
    id uuid NOT NULL
);


--
-- Name: family; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.family (
    id uuid NOT NULL,
    name text
);


--
-- Name: question; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.question (
    id bigint NOT NULL,
    correct_answers_persisted text,
    enabled boolean NOT NULL,
    likely_correct_count bigint,
    point_value bigint
);


--
-- Name: score; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.score (
    user_or_family_id uuid NOT NULL,
    score bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL
);


--
-- Name: spring_session; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.spring_session (
    primary_id character(36) NOT NULL,
    session_id character(36) NOT NULL,
    creation_time bigint NOT NULL,
    last_access_time bigint NOT NULL,
    max_inactive_interval integer NOT NULL,
    expiry_time bigint NOT NULL,
    principal_name character varying(100)
);


--
-- Name: spring_session_attributes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.spring_session_attributes (
    session_primary_id character(36) NOT NULL,
    attribute_name character varying(200) NOT NULL,
    attribute_bytes bytea NOT NULL
);


--
-- Name: answer answer_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.answer
    ADD CONSTRAINT answer_pkey PRIMARY KEY (question_id, user_id);


--
-- Name: appuser_devices appuser_devices_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.appuser_devices
    ADD CONSTRAINT appuser_devices_pkey PRIMARY KEY (users_id, devices_id);


--
-- Name: appuser appuser_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.appuser
    ADD CONSTRAINT appuser_pkey PRIMARY KEY (id);


--
-- Name: chat chat_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.chat
    ADD CONSTRAINT chat_pkey PRIMARY KEY (id);


--
-- Name: device device_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.device
    ADD CONSTRAINT device_pkey PRIMARY KEY (id);


--
-- Name: family family_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.family
    ADD CONSTRAINT family_pkey PRIMARY KEY (id);


--
-- Name: question question_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.question
    ADD CONSTRAINT question_pkey PRIMARY KEY (id);


--
-- Name: score score_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.score
    ADD CONSTRAINT score_pkey PRIMARY KEY (user_or_family_id);


--
-- Name: spring_session_attributes spring_session_attributes_pk; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.spring_session_attributes
    ADD CONSTRAINT spring_session_attributes_pk PRIMARY KEY (session_primary_id, attribute_name);


--
-- Name: spring_session spring_session_pk; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.spring_session
    ADD CONSTRAINT spring_session_pk PRIMARY KEY (primary_id);


--
-- Name: spring_session_ix1; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX spring_session_ix1 ON public.spring_session USING btree (session_id);


--
-- Name: spring_session_ix2; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX spring_session_ix2 ON public.spring_session USING btree (expiry_time);


--
-- Name: spring_session_ix3; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX spring_session_ix3 ON public.spring_session USING btree (principal_name);


--
-- Name: appuser fk16sk9xt8j8jf9b9o2ys1x606t; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.appuser
    ADD CONSTRAINT fk16sk9xt8j8jf9b9o2ys1x606t FOREIGN KEY (family_id) REFERENCES public.family(id);


--
-- Name: answer fk8frr4bcabmmeyyu60qt7iiblo; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.answer
    ADD CONSTRAINT fk8frr4bcabmmeyyu60qt7iiblo FOREIGN KEY (question_id) REFERENCES public.question(id);


--
-- Name: appuser_devices fk8hgqex4nkugs554rjy2fcdqfq; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.appuser_devices
    ADD CONSTRAINT fk8hgqex4nkugs554rjy2fcdqfq FOREIGN KEY (users_id) REFERENCES public.appuser(id);


--
-- Name: appuser_devices fkb1lsy6j5ci3tqlmf7lul3xeh2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.appuser_devices
    ADD CONSTRAINT fkb1lsy6j5ci3tqlmf7lul3xeh2 FOREIGN KEY (devices_id) REFERENCES public.device(id);


--
-- Name: chat fkqltaxgauykb7k94opiasjk9di; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.chat
    ADD CONSTRAINT fkqltaxgauykb7k94opiasjk9di FOREIGN KEY (user_id) REFERENCES public.appuser(id);


--
-- Name: spring_session_attributes spring_session_attributes_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.spring_session_attributes
    ADD CONSTRAINT spring_session_attributes_fk FOREIGN KEY (session_primary_id) REFERENCES public.spring_session(primary_id) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

