--
-- PostgreSQL database dump
--

-- Dumped from database version 13.3
-- Dumped by pg_dump version 13.3

-- Started on 2021-09-09 15:08:02

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

--
-- TOC entry 205 (class 1259 OID 17183)
-- Name: actors; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.actors (
                               actor_id integer NOT NULL,
                               name character varying(30) NOT NULL
);


ALTER TABLE public.actors OWNER TO postgres;

--
-- TOC entry 204 (class 1259 OID 17181)
-- Name: actors_actor_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.actors ALTER COLUMN actor_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.actors_actor_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 217 (class 1259 OID 17314)
-- Name: actors_films; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.actors_films (
                                     actor_film_id bigint NOT NULL,
                                     actor_id integer NOT NULL,
                                     film_id bigint NOT NULL
);


ALTER TABLE public.actors_films OWNER TO postgres;

--
-- TOC entry 216 (class 1259 OID 17312)
-- Name: actors_films_actor_film_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.actors_films ALTER COLUMN actor_film_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.actors_films_actor_film_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 201 (class 1259 OID 17155)
-- Name: admins; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.admins (
                               admin_id integer NOT NULL,
                               code character varying(20) NOT NULL
);


ALTER TABLE public.admins OWNER TO postgres;

--
-- TOC entry 200 (class 1259 OID 17153)
-- Name: admins_admin_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.admins ALTER COLUMN admin_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.admins_admin_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 209 (class 1259 OID 17202)
-- Name: countries; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.countries (
                                  country_id integer NOT NULL,
                                  name character varying(30) NOT NULL
);


ALTER TABLE public.countries OWNER TO postgres;

--
-- TOC entry 208 (class 1259 OID 17200)
-- Name: countries_country_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.countries ALTER COLUMN country_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.countries_country_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 219 (class 1259 OID 17364)
-- Name: countries_films; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.countries_films (
                                        country_film_id bigint NOT NULL,
                                        country_id integer NOT NULL,
                                        film_id bigint NOT NULL
);


ALTER TABLE public.countries_films OWNER TO postgres;

--
-- TOC entry 211 (class 1259 OID 17214)
-- Name: directors; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.directors (
                                  director_id integer NOT NULL,
                                  name character varying(30) NOT NULL
);


ALTER TABLE public.directors OWNER TO postgres;

--
-- TOC entry 210 (class 1259 OID 17212)
-- Name: directors_director_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.directors ALTER COLUMN director_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.directors_director_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 218 (class 1259 OID 17348)
-- Name: directors_films; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.directors_films (
                                        director_film_id bigint NOT NULL,
                                        director_id integer NOT NULL,
                                        film_id bigint NOT NULL
);


ALTER TABLE public.directors_films OWNER TO postgres;

--
-- TOC entry 215 (class 1259 OID 17228)
-- Name: films; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.films (
                              film_id bigint NOT NULL,
                              name character varying(40) NOT NULL,
                              age_limit character varying(20) NOT NULL,
                              short_description text NOT NULL,
                              timing character varying(20) NOT NULL,
                              image text NOT NULL,
                              release_date character varying(20),
                              awards text NOT NULL,
                              language_id integer NOT NULL
);


ALTER TABLE public.films OWNER TO postgres;

--
-- TOC entry 214 (class 1259 OID 17226)
-- Name: films_film_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.films ALTER COLUMN film_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.films_film_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 207 (class 1259 OID 17190)
-- Name: genres; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.genres (
                               genre_id integer NOT NULL,
                               name character varying(30) NOT NULL
);


ALTER TABLE public.genres OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 17379)
-- Name: genres_films; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.genres_films (
                                     genre_film_id bigint NOT NULL,
                                     genre_id integer NOT NULL,
                                     film_id bigint NOT NULL
);


ALTER TABLE public.genres_films OWNER TO postgres;

--
-- TOC entry 206 (class 1259 OID 17188)
-- Name: genres_genre_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.genres ALTER COLUMN genre_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.genres_genre_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 213 (class 1259 OID 17221)
-- Name: languages; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.languages (
                                  language_id integer NOT NULL,
                                  name character varying(30) NOT NULL
);


ALTER TABLE public.languages OWNER TO postgres;

--
-- TOC entry 212 (class 1259 OID 17219)
-- Name: languages_language_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.languages ALTER COLUMN language_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.languages_language_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 203 (class 1259 OID 17162)
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
                              user_id bigint NOT NULL,
                              code character varying(30) NOT NULL
);


ALTER TABLE public.users OWNER TO postgres;

--
-- TOC entry 202 (class 1259 OID 17160)
-- Name: users_user_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.users ALTER COLUMN user_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.users_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 2928 (class 2606 OID 17318)
-- Name: actors_films actors_films_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.actors_films
    ADD CONSTRAINT actors_films_pkey PRIMARY KEY (actor_film_id);


--
-- TOC entry 2916 (class 2606 OID 17187)
-- Name: actors actors_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.actors
    ADD CONSTRAINT actors_pkey PRIMARY KEY (actor_id);


--
-- TOC entry 2912 (class 2606 OID 17159)
-- Name: admins admins_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admins
    ADD CONSTRAINT admins_pkey PRIMARY KEY (admin_id);


--
-- TOC entry 2932 (class 2606 OID 17368)
-- Name: countries_films countries_films_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.countries_films
    ADD CONSTRAINT countries_films_pkey PRIMARY KEY (country_film_id);


--
-- TOC entry 2920 (class 2606 OID 17206)
-- Name: countries countries_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.countries
    ADD CONSTRAINT countries_pkey PRIMARY KEY (country_id);


--
-- TOC entry 2930 (class 2606 OID 17352)
-- Name: directors_films directors_films_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.directors_films
    ADD CONSTRAINT directors_films_pkey PRIMARY KEY (film_id);


--
-- TOC entry 2922 (class 2606 OID 17218)
-- Name: directors directors_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.directors
    ADD CONSTRAINT directors_pkey PRIMARY KEY (director_id);


--
-- TOC entry 2926 (class 2606 OID 17235)
-- Name: films films_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.films
    ADD CONSTRAINT films_pkey PRIMARY KEY (film_id);


--
-- TOC entry 2934 (class 2606 OID 17383)
-- Name: genres_films genres_films_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.genres_films
    ADD CONSTRAINT genres_films_pkey PRIMARY KEY (genre_film_id);


--
-- TOC entry 2918 (class 2606 OID 17194)
-- Name: genres genres_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.genres
    ADD CONSTRAINT genres_pkey PRIMARY KEY (genre_id);


--
-- TOC entry 2924 (class 2606 OID 17225)
-- Name: languages languages_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.languages
    ADD CONSTRAINT languages_pkey PRIMARY KEY (language_id);


--
-- TOC entry 2914 (class 2606 OID 17169)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (user_id);


--
-- TOC entry 2940 (class 2606 OID 17369)
-- Name: countries_films country_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.countries_films
    ADD CONSTRAINT country_id FOREIGN KEY (country_id) REFERENCES public.countries(country_id);


--
-- TOC entry 2941 (class 2606 OID 17374)
-- Name: countries_films film_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.countries_films
    ADD CONSTRAINT film_id FOREIGN KEY (film_id) REFERENCES public.films(film_id);


--
-- TOC entry 2935 (class 2606 OID 17329)
-- Name: films fk2_languageId; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.films
    ADD CONSTRAINT "fk2_languageId" FOREIGN KEY (language_id) REFERENCES public.languages(language_id) NOT VALID;


--
-- TOC entry 2936 (class 2606 OID 17319)
-- Name: actors_films fk_actor_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.actors_films
    ADD CONSTRAINT fk_actor_id FOREIGN KEY (actor_id) REFERENCES public.actors(actor_id);


--
-- TOC entry 2938 (class 2606 OID 17353)
-- Name: directors_films fk_director_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.directors_films
    ADD CONSTRAINT fk_director_id FOREIGN KEY (director_id) REFERENCES public.directors(director_id);


--
-- TOC entry 2937 (class 2606 OID 17324)
-- Name: actors_films fk_film_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.actors_films
    ADD CONSTRAINT fk_film_id FOREIGN KEY (film_id) REFERENCES public.films(film_id);


--
-- TOC entry 2939 (class 2606 OID 17358)
-- Name: directors_films fk_film_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.directors_films
    ADD CONSTRAINT fk_film_id FOREIGN KEY (film_id) REFERENCES public.films(film_id);


--
-- TOC entry 2943 (class 2606 OID 17389)
-- Name: genres_films fk_film_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.genres_films
    ADD CONSTRAINT fk_film_id FOREIGN KEY (film_id) REFERENCES public.films(film_id);


--
-- TOC entry 2942 (class 2606 OID 17384)
-- Name: genres_films fk_genre_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.genres_films
    ADD CONSTRAINT fk_genre_id FOREIGN KEY (genre_id) REFERENCES public.genres(genre_id);


-- Completed on 2021-09-09 15:08:02
