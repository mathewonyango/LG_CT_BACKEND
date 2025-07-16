--
-- PostgreSQL database dump
--

-- Dumped from database version 17.5
-- Dumped by pg_dump version 17.5

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Data for Name: counties; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.counties (id, code, name) VALUES (1, '001', 'Nairobi');
INSERT INTO public.counties (id, code, name) VALUES (2, '003', 'Nakuru');


--
-- Data for Name: sub_counties; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.sub_counties (id, name, county_id) VALUES (1, 'Nyando', 1);


--
-- Data for Name: wards; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.wards (id, name, sub_county_id) VALUES (1, 'Kobura', 1);
INSERT INTO public.wards (id, name, sub_county_id) VALUES (2, 'Working ward', 1);


--
-- Data for Name: facilities; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.facilities (id, facility_code, name, type, ward_id) VALUES (1, NULL, 'Okana Dispensary', 'Dispensary', 1);
INSERT INTO public.facilities (id, facility_code, name, type, ward_id) VALUES (2, NULL, 'Test Two', 'Dispensary', NULL);
INSERT INTO public.facilities (id, facility_code, name, type, ward_id) VALUES (3, NULL, 'DOES THIS WORK', 'tEST', NULL);


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.users (id, created_at, email, last_login, password_hash, reset_token, reset_token_expiry, role, username, version, phone_number) VALUES (3, '2025-06-25 00:59:12.607856', 'mathewsagumbah3@gmail.com', '2025-06-25 00:59:12.607856', '$2a$10$DhadbAZQ.FnimrwFf8IWUOWwg.K87R5.H1nGKbiWClQ1d6wMQPzVe', NULL, NULL, 'USER', 'Mathews3', NULL, NULL);
INSERT INTO public.users (id, created_at, email, last_login, password_hash, reset_token, reset_token_expiry, role, username, version, phone_number) VALUES (13, '2025-07-11 20:15:20.947663', 'CHAT002@gmail.com', '2025-07-12 23:38:16.287477', '$2a$10$gfM0gCDkOVSAs352FaHXm.Msq6H6LeDxJYstE5wETbrKvmH4ob.cC', NULL, NULL, 'CHA', 'CHA02', 1, NULL);
INSERT INTO public.users (id, created_at, email, last_login, password_hash, reset_token, reset_token_expiry, role, username, version, phone_number) VALUES (2, '2025-06-25 00:58:07.326061', 'mathewsagumbah1@gmail.com', NULL, '$2a$10$DhadbAZQ.FnimrwFf8IWUOWwg.K87R5.H1nGKbiWClQ1d6wMQPzVe', NULL, NULL, 'ADMIN', 'Mathews2', NULL, NULL);
INSERT INTO public.users (id, created_at, email, last_login, password_hash, reset_token, reset_token_expiry, role, username, version, phone_number) VALUES (4, '2025-06-28 19:55:23.79797', 'matehwsagumbah@gmail.com', '2025-06-30 15:47:51.816395', '$2a$10$fk/dhA7J.hFGMIdzepP/vOu/dHDBdT0mLKJelCkorEAj0yHCeWwBC', NULL, NULL, 'USER', 'Ruth', 4, NULL);
INSERT INTO public.users (id, created_at, email, last_login, password_hash, reset_token, reset_token_expiry, role, username, version, phone_number) VALUES (6, '2025-06-30 18:49:13.931371', 'test@gmail.com', '2025-06-30 18:49:43.92787', '$2a$10$k1eSnVDomtrVKBKL2elv1u86g7y7CVHDj0Pnvcym/9HSEtUXrqzA2', NULL, NULL, 'USER', 'test', 1, NULL);
INSERT INTO public.users (id, created_at, email, last_login, password_hash, reset_token, reset_token_expiry, role, username, version, phone_number) VALUES (8, '2025-06-30 18:50:57.694418', 'test2@mail.com', '2025-06-30 18:50:57.694418', '$2a$10$4TByLz6C0BTlsaKI52KvxuIadbOav3hdKI6QPsnvLmIfV/ZsPkPJm', NULL, NULL, 'ADMIN', 'test2', 0, NULL);
INSERT INTO public.users (id, created_at, email, last_login, password_hash, reset_token, reset_token_expiry, role, username, version, phone_number) VALUES (11, '2025-07-11 17:02:59.012455', 'CHATest@gmail.com', '2025-07-11 17:02:59.012455', '$2a$10$zf4hPS7TiRSj8Hrp8Ib62emqp4yPrP9TiTva6E8QN65ZD26sKeFsm', NULL, NULL, 'CHA', 'CHA TEST', 0, NULL);
INSERT INTO public.users (id, created_at, email, last_login, password_hash, reset_token, reset_token_expiry, role, username, version, phone_number) VALUES (14, '2025-07-11 20:15:49.868457', 'CHP01@gmail.com', '2025-07-11 20:15:49.868457', '$2a$10$DsuZ4QNzKDywq10m.gDdpOewskO3lCIIDLdc/9vPkP7GnBnTTgPbm', NULL, NULL, 'CHP', 'CHP01', 0, NULL);
INSERT INTO public.users (id, created_at, email, last_login, password_hash, reset_token, reset_token_expiry, role, username, version, phone_number) VALUES (15, '2025-07-11 20:16:05.871444', 'CHP02@gmail.com', '2025-07-11 20:16:05.871444', '$2a$10$37/5IGfE59FJ06q5zY.3wOeoHtERKIQB/pkqsPoHaUbiL77ajf9LC', NULL, NULL, 'CHP', 'CHP02', 0, NULL);
INSERT INTO public.users (id, created_at, email, last_login, password_hash, reset_token, reset_token_expiry, role, username, version, phone_number) VALUES (16, '2025-07-11 20:16:19.776118', 'CHP03@gmail.com', '2025-07-11 20:16:19.776118', '$2a$10$JX8V2AqouFrh1SYPl1vWgOWjjGPUq8bVNZ3CI2S0FKzjQnQPbFf.S', NULL, NULL, 'CHP', 'CHP03', 0, NULL);
INSERT INTO public.users (id, created_at, email, last_login, password_hash, reset_token, reset_token_expiry, role, username, version, phone_number) VALUES (17, '2025-07-11 20:16:34.562392', 'CHP04@gmail.com', '2025-07-11 20:16:34.562392', '$2a$10$IYTd74Nkwc3o4pPtxYha2OF0iXtumS3AIkkKiV7me8rRkch59l2g.', NULL, NULL, 'CHP', 'CHP04', 0, NULL);
INSERT INTO public.users (id, created_at, email, last_login, password_hash, reset_token, reset_token_expiry, role, username, version, phone_number) VALUES (18, '2025-07-11 20:16:45.712428', 'CHP05@gmail.com', '2025-07-11 20:16:45.712428', '$2a$10$5lBSf8J3C9VJ.5X7.7.6C.qAmLof7uISYCDtGrDhx/HrIU0OnKxhm', NULL, NULL, 'CHP', 'CHP05', 0, NULL);
INSERT INTO public.users (id, created_at, email, last_login, password_hash, reset_token, reset_token_expiry, role, username, version, phone_number) VALUES (19, '2025-07-11 20:16:56.723375', 'CHP06@gmail.com', '2025-07-11 20:16:56.723375', '$2a$10$8q8bCt1M82m5pKGzgnI.C.kvd/V903/AHVNJp9uZ1hyRp1N9QwnIa', NULL, NULL, 'CHP', 'CHP06', 0, NULL);
INSERT INTO public.users (id, created_at, email, last_login, password_hash, reset_token, reset_token_expiry, role, username, version, phone_number) VALUES (20, '2025-07-11 20:17:08.28279', 'CHP07@gmail.com', '2025-07-11 20:17:08.28279', '$2a$10$0KLCbS8AxQ.LL3dNo/7ZBuFDpW1KBQ6xVfcbTG7ED7uVDsad7xP7m', NULL, NULL, 'CHP', 'CHP07', 0, NULL);
INSERT INTO public.users (id, created_at, email, last_login, password_hash, reset_token, reset_token_expiry, role, username, version, phone_number) VALUES (21, '2025-07-13 23:32:15.085728', 'manager@gmail.com', '2025-07-13 23:45:47.526957', '$2a$10$hp1aJJ.XbPJ/lxNs.VO7E.EQVu1ri/sEFtH3jvEEILAtnkUJ6W1Je', NULL, NULL, 'MANAGER', 'Manager', 1, NULL);
INSERT INTO public.users (id, created_at, email, last_login, password_hash, reset_token, reset_token_expiry, role, username, version, phone_number) VALUES (22, '2025-07-14 02:39:48.291718', 'managr@gmail.com', '2025-07-14 02:39:48.291718', '$2a$10$7mg3RNTTDPUoJPLTwMl4WOOXrUXcZvZDhP99sCbMXbiwwkLbkH20i', NULL, NULL, 'MANAGER', 'manager', 0, NULL);
INSERT INTO public.users (id, created_at, email, last_login, password_hash, reset_token, reset_token_expiry, role, username, version, phone_number) VALUES (12, '2025-07-11 20:15:08.670656', 'CHAT001@gmail.com', '2025-07-14 02:59:46.996443', '$2a$10$J3bj3/TJKcuF4LuyUUHQS./IhXlln8JzpTak.cALCiNFvMzh2AYWO', NULL, NULL, 'CHA', 'CHA01', 17, NULL);
INSERT INTO public.users (id, created_at, email, last_login, password_hash, reset_token, reset_token_expiry, role, username, version, phone_number) VALUES (1, '2025-06-25 00:00:59.976878', 'mathewsagumbah@gmail.com', '2025-07-14 03:23:52.732046', '$2a$10$DhadbAZQ.FnimrwFf8IWUOWwg.K87R5.H1nGKbiWClQ1d6wMQPzVe', NULL, NULL, 'ADMIN', 'mathews', 44, NULL);
INSERT INTO public.users (id, created_at, email, last_login, password_hash, reset_token, reset_token_expiry, role, username, version, phone_number) VALUES (23, '2025-07-14 21:45:17.42885', 'CHPExcel@gmail.com', '2025-07-14 21:45:17.42885', '$2a$10$ng3XxUuZ2JMXN1MMmJDsdOVtvJM9kO1SrKxpMLD9RDLBIj2wNDYEa', NULL, NULL, 'CHP', 'CHP Excel', 0, NULL);
INSERT INTO public.users (id, created_at, email, last_login, password_hash, reset_token, reset_token_expiry, role, username, version, phone_number) VALUES (24, '2025-07-14 21:56:39.0683', 'NotMandatory@gmail.com', '2025-07-14 21:56:39.0683', '$2a$10$E.6oO./m59.Q/ooVJkgmZu7xhYLx5LBfGX.iks3TMatsXjJQXQb1S', NULL, NULL, 'CHP', 'chpWithPhoneNumber', 0, '0702622569');
INSERT INTO public.users (id, created_at, email, last_login, password_hash, reset_token, reset_token_expiry, role, username, version, phone_number) VALUES (25, '2025-07-16 19:52:58.372117', 'admin@gmail.com', '2025-07-16 19:52:58.372117', '$2a$10$pLB0DVQ7tf7PyyESsz1AHunbImQQITi2IRbJxRf7F6b2H2ofGIaxW', NULL, NULL, 'CHP', 'John Do', 0, '254701234560');


--
-- Data for Name: community_units; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.community_units (id, cha_name, community_unit_name, created_at, created_by_id, total_chps_counted, total_chps, county_id, created_by, link_facility_id, sub_county_id, ward_id) VALUES (1, 'Mathews Onyango lELA mIX', 'Lela', '2025-06-25 12:18:35.400764', 1, 4, 8, 1, NULL, 1, 1, 1);
INSERT INTO public.community_units (id, cha_name, community_unit_name, created_at, created_by_id, total_chps_counted, total_chps, county_id, created_by, link_facility_id, sub_county_id, ward_id) VALUES (2, 'Mathews Onyango', 'Lela mIX', '2025-06-25 12:20:53.584151', 1, 5, 5, 1, NULL, 1, 1, 1);
INSERT INTO public.community_units (id, cha_name, community_unit_name, created_at, created_by_id, total_chps_counted, total_chps, county_id, created_by, link_facility_id, sub_county_id, ward_id) VALUES (3, 'Mathews Onyango', 'Lela', '2025-06-25 12:40:43.530786', 1, 7, 8, 1, NULL, 1, 1, 1);
INSERT INTO public.community_units (id, cha_name, community_unit_name, created_at, created_by_id, total_chps_counted, total_chps, county_id, created_by, link_facility_id, sub_county_id, ward_id) VALUES (4, 'Test', 'Test', '2025-06-25 13:31:35.611815', 3, 2, 1, 1, NULL, 1, 1, 1);
INSERT INTO public.community_units (id, cha_name, community_unit_name, created_at, created_by_id, total_chps_counted, total_chps, county_id, created_by, link_facility_id, sub_county_id, ward_id) VALUES (5, 'YU', 'YU', '2025-06-25 21:17:52.283021', 1, 2, 2, 1, NULL, 1, 1, 1);
INSERT INTO public.community_units (id, cha_name, community_unit_name, created_at, created_by_id, total_chps_counted, total_chps, county_id, created_by, link_facility_id, sub_county_id, ward_id) VALUES (6, 'YU3', 'Yu4', '2025-06-25 21:19:30.756998', 1, 2, 4, 1, NULL, 1, 1, 1);
INSERT INTO public.community_units (id, cha_name, community_unit_name, created_at, created_by_id, total_chps_counted, total_chps, county_id, created_by, link_facility_id, sub_county_id, ward_id) VALUES (7, 'With Array', 'With Array', '2025-06-28 16:22:54.906322', 1, 1, 2, 1, NULL, 2, 1, 2);
INSERT INTO public.community_units (id, cha_name, community_unit_name, created_at, created_by_id, total_chps_counted, total_chps, county_id, created_by, link_facility_id, sub_county_id, ward_id) VALUES (8, 'Ken Obura', 'Lela mIX', '2025-06-28 17:15:36.165044', 1, 2, 3, 1, NULL, 3, 1, 1);
INSERT INTO public.community_units (id, cha_name, community_unit_name, created_at, created_by_id, total_chps_counted, total_chps, county_id, created_by, link_facility_id, sub_county_id, ward_id) VALUES (9, '', 'Test for selecting CHPs', '2025-07-11 23:09:56.254005', 1, NULL, 0, 1, NULL, 2, 1, 1);
INSERT INTO public.community_units (id, cha_name, community_unit_name, created_at, created_by_id, total_chps_counted, total_chps, county_id, created_by, link_facility_id, sub_county_id, ward_id) VALUES (10, '', 'Test for selecting CHPs', '2025-07-11 23:10:10.264586', 1, NULL, 0, 1, NULL, 2, 1, 1);
INSERT INTO public.community_units (id, cha_name, community_unit_name, created_at, created_by_id, total_chps_counted, total_chps, county_id, created_by, link_facility_id, sub_county_id, ward_id) VALUES (11, '', 'Test2', '2025-07-11 23:11:26.888826', 1, NULL, 0, 1, NULL, 2, 1, 1);
INSERT INTO public.community_units (id, cha_name, community_unit_name, created_at, created_by_id, total_chps_counted, total_chps, county_id, created_by, link_facility_id, sub_county_id, ward_id) VALUES (12, NULL, 'Test001', '2025-07-11 23:16:42.543143', 1, NULL, NULL, 1, NULL, 2, 1, 1);
INSERT INTO public.community_units (id, cha_name, community_unit_name, created_at, created_by_id, total_chps_counted, total_chps, county_id, created_by, link_facility_id, sub_county_id, ward_id) VALUES (13, NULL, 'TestB', '2025-07-12 15:03:03.883786', 1, NULL, NULL, 1, NULL, 2, 1, 1);
INSERT INTO public.community_units (id, cha_name, community_unit_name, created_at, created_by_id, total_chps_counted, total_chps, county_id, created_by, link_facility_id, sub_county_id, ward_id) VALUES (14, NULL, 'Test', '2025-07-13 22:37:01.225473', 1, NULL, NULL, 1, NULL, 2, 1, 1);
INSERT INTO public.community_units (id, cha_name, community_unit_name, created_at, created_by_id, total_chps_counted, total_chps, county_id, created_by, link_facility_id, sub_county_id, ward_id) VALUES (15, NULL, 'Test', '2025-07-13 22:37:01.701114', 1, NULL, NULL, 1, NULL, 2, 1, 1);
INSERT INTO public.community_units (id, cha_name, community_unit_name, created_at, created_by_id, total_chps_counted, total_chps, county_id, created_by, link_facility_id, sub_county_id, ward_id) VALUES (16, NULL, 'Test', '2025-07-13 22:50:08.682481', 1, NULL, NULL, 1, NULL, 2, 1, 1);
INSERT INTO public.community_units (id, cha_name, community_unit_name, created_at, created_by_id, total_chps_counted, total_chps, county_id, created_by, link_facility_id, sub_county_id, ward_id) VALUES (17, NULL, 'This works?', '2025-07-13 22:53:35.774847', 1, NULL, NULL, 1, NULL, 2, 1, 1);
INSERT INTO public.community_units (id, cha_name, community_unit_name, created_at, created_by_id, total_chps_counted, total_chps, county_id, created_by, link_facility_id, sub_county_id, ward_id) VALUES (18, NULL, 'Chuodho', '2025-07-13 23:04:46.81232', 1, NULL, NULL, 1, NULL, 2, 1, 1);
INSERT INTO public.community_units (id, cha_name, community_unit_name, created_at, created_by_id, total_chps_counted, total_chps, county_id, created_by, link_facility_id, sub_county_id, ward_id) VALUES (19, NULL, 'Chuodho', '2025-07-13 23:06:21.251617', 1, NULL, NULL, 1, NULL, 2, 1, 1);
INSERT INTO public.community_units (id, cha_name, community_unit_name, created_at, created_by_id, total_chps_counted, total_chps, county_id, created_by, link_facility_id, sub_county_id, ward_id) VALUES (20, NULL, 'Test', '2025-07-13 23:06:42.285434', 1, NULL, NULL, 1, NULL, 3, 1, 1);
INSERT INTO public.community_units (id, cha_name, community_unit_name, created_at, created_by_id, total_chps_counted, total_chps, county_id, created_by, link_facility_id, sub_county_id, ward_id) VALUES (21, NULL, 'Chuodho', '2025-07-13 23:09:04.09273', 1, NULL, NULL, 1, NULL, 3, 1, 2);
INSERT INTO public.community_units (id, cha_name, community_unit_name, created_at, created_by_id, total_chps_counted, total_chps, county_id, created_by, link_facility_id, sub_county_id, ward_id) VALUES (22, NULL, 'Ken Obura Dispe', '2025-07-13 23:09:31.028814', 1, NULL, NULL, 1, NULL, 2, 1, 1);


--
-- Data for Name: cha_chp_mapping; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.cha_chp_mapping (id, cha_id, chp_id, community_unit_id) VALUES (1, 1, 1, 1);
INSERT INTO public.cha_chp_mapping (id, cha_id, chp_id, community_unit_id) VALUES (2, 12, 14, 1);
INSERT INTO public.cha_chp_mapping (id, cha_id, chp_id, community_unit_id) VALUES (3, 12, 16, 1);
INSERT INTO public.cha_chp_mapping (id, cha_id, chp_id, community_unit_id) VALUES (4, 12, 17, 1);
INSERT INTO public.cha_chp_mapping (id, cha_id, chp_id, community_unit_id) VALUES (6, 12, 14, 11);
INSERT INTO public.cha_chp_mapping (id, cha_id, chp_id, community_unit_id) VALUES (7, 11, 20, 11);
INSERT INTO public.cha_chp_mapping (id, cha_id, chp_id, community_unit_id) VALUES (8, 13, 18, 11);
INSERT INTO public.cha_chp_mapping (id, cha_id, chp_id, community_unit_id) VALUES (9, 11, 19, 11);
INSERT INTO public.cha_chp_mapping (id, cha_id, chp_id, community_unit_id) VALUES (10, 12, 19, 11);
INSERT INTO public.cha_chp_mapping (id, cha_id, chp_id, community_unit_id) VALUES (11, 11, 14, 11);
INSERT INTO public.cha_chp_mapping (id, cha_id, chp_id, community_unit_id) VALUES (12, 12, 20, 11);
INSERT INTO public.cha_chp_mapping (id, cha_id, chp_id, community_unit_id) VALUES (13, 13, 15, 13);


--
-- Data for Name: commodity_categories; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.commodity_categories (id, description, name) VALUES (1, 'Malarial drugs', 'Malarial Drugs');


--
-- Data for Name: commodities; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.commodities (id, description, name, unit_of_measure, category_id) VALUES (1, 'Malarial drugs', 'AL6', 'Satchets', 1);


--
-- Data for Name: commodity_records; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.commodity_records (id, closing_balance, consumption_period, created_at, chp_id, earliest_expiry_date, excess_quantity_returned, last_restock_date, quantity_consumed, quantity_damaged, quantity_expired, quantity_issued, quantity_to_order, record_date, stock_on_hand, stock_out_date, commodity_id, community_unit_id, chp) VALUES (1, -11, 6, '2025-06-25 12:21:16.477059', NULL, '2025-06-24 00:00:00', 5, '2025-06-24 00:00:00', 3, 8, 8, 5, 16, '2025-06-25 12:21:16.477059', 8, '2025-06-18 00:00:00', 1, 2, NULL);
INSERT INTO public.commodity_records (id, closing_balance, consumption_period, created_at, chp_id, earliest_expiry_date, excess_quantity_returned, last_restock_date, quantity_consumed, quantity_damaged, quantity_expired, quantity_issued, quantity_to_order, record_date, stock_on_hand, stock_out_date, commodity_id, community_unit_id, chp) VALUES (2, -31, 0, '2025-06-25 12:44:00.456973', NULL, '2025-06-29 00:00:00', 5, '2025-06-24 00:00:00', 6, 2, 23, 4, 40, '2025-06-25 12:44:00.456973', 1, '2025-06-24 00:00:00', 1, 1, NULL);
INSERT INTO public.commodity_records (id, closing_balance, consumption_period, created_at, chp_id, earliest_expiry_date, excess_quantity_returned, last_restock_date, quantity_consumed, quantity_damaged, quantity_expired, quantity_issued, quantity_to_order, record_date, stock_on_hand, stock_out_date, commodity_id, community_unit_id, chp) VALUES (3, -139, 9, '2025-06-25 12:50:22.22697', NULL, '2025-06-30 00:00:00', 91, '2025-06-24 00:00:00', 5, 7, 45, 6, 147, '2025-06-25 12:50:22.22697', 3, '2025-07-03 00:00:00', 1, 3, NULL);
INSERT INTO public.commodity_records (id, closing_balance, consumption_period, created_at, chp_id, earliest_expiry_date, excess_quantity_returned, last_restock_date, quantity_consumed, quantity_damaged, quantity_expired, quantity_issued, quantity_to_order, record_date, stock_on_hand, stock_out_date, commodity_id, community_unit_id, chp) VALUES (4, -9, 16, '2025-06-25 21:18:40.093376', NULL, '2025-06-26 00:00:00', 9, '2025-07-10 00:00:00', 3, 3, 2, 4, 14, '2025-06-25 21:18:40.093376', 4, '2025-06-24 00:00:00', 1, 5, NULL);
INSERT INTO public.commodity_records (id, closing_balance, consumption_period, created_at, chp_id, earliest_expiry_date, excess_quantity_returned, last_restock_date, quantity_consumed, quantity_damaged, quantity_expired, quantity_issued, quantity_to_order, record_date, stock_on_hand, stock_out_date, commodity_id, community_unit_id, chp) VALUES (5, -74, 1, '2025-06-28 16:23:45.391344', NULL, '2025-07-05 00:00:00', 37, '2025-07-11 00:00:00', 37, 37, 37, 37, 130, '2025-06-28 16:23:45.391344', 37, '2025-07-12 00:00:00', 1, 7, NULL);
INSERT INTO public.commodity_records (id, closing_balance, consumption_period, created_at, chp_id, earliest_expiry_date, excess_quantity_returned, last_restock_date, quantity_consumed, quantity_damaged, quantity_expired, quantity_issued, quantity_to_order, record_date, stock_on_hand, stock_out_date, commodity_id, community_unit_id, chp) VALUES (6, -76, 12, '2025-06-28 17:16:35.178195', NULL, '2025-06-26 00:00:00', 21, '2025-06-28 00:00:00', 23, 34, 41, 20, 111, '2025-06-28 17:16:35.178195', 23, '2025-07-10 00:00:00', 1, 8, NULL);
INSERT INTO public.commodity_records (id, closing_balance, consumption_period, created_at, chp_id, earliest_expiry_date, excess_quantity_returned, last_restock_date, quantity_consumed, quantity_damaged, quantity_expired, quantity_issued, quantity_to_order, record_date, stock_on_hand, stock_out_date, commodity_id, community_unit_id, chp) VALUES (7, -10, 2, '2025-07-11 23:17:18.4192', NULL, '2025-07-10 00:00:00', 10, '2025-07-31 00:00:00', 10, 20, 10, 20, 25, '2025-07-11 23:17:18.4192', 20, '2025-07-29 00:00:00', 1, 12, NULL);
INSERT INTO public.commodity_records (id, closing_balance, consumption_period, created_at, chp_id, earliest_expiry_date, excess_quantity_returned, last_restock_date, quantity_consumed, quantity_damaged, quantity_expired, quantity_issued, quantity_to_order, record_date, stock_on_hand, stock_out_date, commodity_id, community_unit_id, chp) VALUES (9, -21, 1, '2025-07-11 23:50:19.128028', NULL, '2025-07-16 00:00:00', 12, '2025-07-24 00:00:00', 12, 10, 1, 2, 39, '2025-07-11 23:50:19.128028', 12, '2025-07-23 00:00:00', 1, 10, NULL);
INSERT INTO public.commodity_records (id, closing_balance, consumption_period, created_at, chp_id, earliest_expiry_date, excess_quantity_returned, last_restock_date, quantity_consumed, quantity_damaged, quantity_expired, quantity_issued, quantity_to_order, record_date, stock_on_hand, stock_out_date, commodity_id, community_unit_id, chp) VALUES (10, -12, 1, '2025-07-11 23:52:32.572742', NULL, '2025-07-09 00:00:00', 3, '2025-07-30 00:00:00', 5, 3, 10, 5, 20, '2025-07-11 23:52:32.572742', 4, '2025-07-29 00:00:00', 1, 10, NULL);
INSERT INTO public.commodity_records (id, closing_balance, consumption_period, created_at, chp_id, earliest_expiry_date, excess_quantity_returned, last_restock_date, quantity_consumed, quantity_damaged, quantity_expired, quantity_issued, quantity_to_order, record_date, stock_on_hand, stock_out_date, commodity_id, community_unit_id, chp) VALUES (11, 7, 15, '2025-07-11 23:59:30.900927', NULL, '2025-07-02 00:00:00', 0, '2025-07-23 00:00:00', 2, 3, 12, 21, 0, '2025-07-11 23:59:30.900927', 3, '2025-07-08 00:00:00', 1, 3, NULL);
INSERT INTO public.commodity_records (id, closing_balance, consumption_period, created_at, chp_id, earliest_expiry_date, excess_quantity_returned, last_restock_date, quantity_consumed, quantity_damaged, quantity_expired, quantity_issued, quantity_to_order, record_date, stock_on_hand, stock_out_date, commodity_id, community_unit_id, chp) VALUES (14, -203, 5, '2025-07-12 01:01:16.000099', 14, '2025-07-22 00:00:00', 102, '2025-07-24 00:00:00', 102, 102, 101, 102, 356, '2025-07-12 01:01:16.000099', 102, '2025-07-29 00:00:00', 1, 11, NULL);
INSERT INTO public.commodity_records (id, closing_balance, consumption_period, created_at, chp_id, earliest_expiry_date, excess_quantity_returned, last_restock_date, quantity_consumed, quantity_damaged, quantity_expired, quantity_issued, quantity_to_order, record_date, stock_on_hand, stock_out_date, commodity_id, community_unit_id, chp) VALUES (8, 10, 10, '2025-07-11 23:48:36.265796', 14, '2025-07-11 20:48:01.155', 1, '2025-07-11 20:48:01.156', 10, 1, 1, 1, 10, '2025-07-11 23:48:36.265796', 1, '2025-07-11 20:48:01.156', 1, 1, NULL);
INSERT INTO public.commodity_records (id, closing_balance, consumption_period, created_at, chp_id, earliest_expiry_date, excess_quantity_returned, last_restock_date, quantity_consumed, quantity_damaged, quantity_expired, quantity_issued, quantity_to_order, record_date, stock_on_hand, stock_out_date, commodity_id, community_unit_id, chp) VALUES (13, 10, 10, '2025-07-12 00:53:58.675175', 14, '2025-07-11 21:50:28.703', 10, '2025-07-11 21:50:28.703', 10, 10, 10, 10, 10, '2025-07-12 00:53:58.675175', 10, '2025-07-11 21:50:28.703', 1, 1, NULL);
INSERT INTO public.commodity_records (id, closing_balance, consumption_period, created_at, chp_id, earliest_expiry_date, excess_quantity_returned, last_restock_date, quantity_consumed, quantity_damaged, quantity_expired, quantity_issued, quantity_to_order, record_date, stock_on_hand, stock_out_date, commodity_id, community_unit_id, chp) VALUES (15, -3, 7, '2025-07-12 15:00:21.263334', 14, '2025-07-08 00:00:00', 2, '2025-07-16 00:00:00', 1, 12, 12, 12, 5, '2025-07-12 15:00:21.263334', 12, '2025-07-09 00:00:00', 1, 1, NULL);
INSERT INTO public.commodity_records (id, closing_balance, consumption_period, created_at, chp_id, earliest_expiry_date, excess_quantity_returned, last_restock_date, quantity_consumed, quantity_damaged, quantity_expired, quantity_issued, quantity_to_order, record_date, stock_on_hand, stock_out_date, commodity_id, community_unit_id, chp) VALUES (16, -47, 2, '2025-07-13 00:12:16.077722', 11, '2025-07-07 00:00:00', 2, '2025-07-29 00:00:00', 45, 107, 107, 107, 115, '2025-07-13 00:12:16.077722', 107, '2025-07-31 00:00:00', 1, 11, NULL);
INSERT INTO public.commodity_records (id, closing_balance, consumption_period, created_at, chp_id, earliest_expiry_date, excess_quantity_returned, last_restock_date, quantity_consumed, quantity_damaged, quantity_expired, quantity_issued, quantity_to_order, record_date, stock_on_hand, stock_out_date, commodity_id, community_unit_id, chp) VALUES (17, 442, 1, '2025-07-13 00:15:21.348832', 11, '2025-07-14 00:00:00', 34, '2025-07-30 00:00:00', 34, 34, 23, 334, 0, '2025-07-13 00:15:21.348832', 233, '2025-07-29 00:00:00', 1, 11, NULL);
INSERT INTO public.commodity_records (id, closing_balance, consumption_period, created_at, chp_id, earliest_expiry_date, excess_quantity_returned, last_restock_date, quantity_consumed, quantity_damaged, quantity_expired, quantity_issued, quantity_to_order, record_date, stock_on_hand, stock_out_date, commodity_id, community_unit_id, chp) VALUES (12, -68, 0, '2025-07-12 00:06:31.332471', 17, '2025-07-08 00:00:00', 34, '2025-07-15 00:00:00', 34, 34, 34, 34, 119, '2025-07-12 00:06:31.332471', 34, '2025-07-15 00:00:00', 1, 3, NULL);
INSERT INTO public.commodity_records (id, closing_balance, consumption_period, created_at, chp_id, earliest_expiry_date, excess_quantity_returned, last_restock_date, quantity_consumed, quantity_damaged, quantity_expired, quantity_issued, quantity_to_order, record_date, stock_on_hand, stock_out_date, commodity_id, community_unit_id, chp) VALUES (18, -978, 1, '2025-07-13 12:47:18.369782', 11, '2025-07-22 00:00:00', 89, '2025-07-22 00:00:00', 889, 89, 89, 89, 2312, '2025-07-13 12:47:18.369782', 89, '2025-07-23 00:00:00', 1, 11, NULL);
INSERT INTO public.commodity_records (id, closing_balance, consumption_period, created_at, chp_id, earliest_expiry_date, excess_quantity_returned, last_restock_date, quantity_consumed, quantity_damaged, quantity_expired, quantity_issued, quantity_to_order, record_date, stock_on_hand, stock_out_date, commodity_id, community_unit_id, chp) VALUES (19, 6229, 0, '2025-07-13 12:48:03.73709', 11, '2025-07-14 00:00:00', 890, '2025-07-23 00:00:00', 890, 890, 890, 8899, 0, '2025-07-13 12:48:03.739111', 890, '2025-07-23 00:00:00', 1, 11, NULL);
INSERT INTO public.commodity_records (id, closing_balance, consumption_period, created_at, chp_id, earliest_expiry_date, excess_quantity_returned, last_restock_date, quantity_consumed, quantity_damaged, quantity_expired, quantity_issued, quantity_to_order, record_date, stock_on_hand, stock_out_date, commodity_id, community_unit_id, chp) VALUES (20, -178, 9, '2025-07-13 13:08:00.974851', 11, '2025-07-22 00:00:00', 89, '2025-07-31 00:00:00', 89, 89, 89, 89, 312, '2025-07-13 13:08:00.974851', 89, '2025-07-22 00:00:00', 1, 11, NULL);
INSERT INTO public.commodity_records (id, closing_balance, consumption_period, created_at, chp_id, earliest_expiry_date, excess_quantity_returned, last_restock_date, quantity_consumed, quantity_damaged, quantity_expired, quantity_issued, quantity_to_order, record_date, stock_on_hand, stock_out_date, commodity_id, community_unit_id, chp) VALUES (21, -367, 6, '2025-07-13 13:29:02.867076', 16, '2025-07-21 00:00:00', 34, '2025-07-23 00:00:00', 344, 34, 23, 34, 883, '2025-07-13 13:29:02.867076', 34, '2025-07-29 00:00:00', 1, 12, NULL);


--
-- Data for Name: commodity_stock_history; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.commodity_stock_history (id, change_type, new_balance, notes, previous_balance, quantity_changed, record_date, commodity_id, community_unit_id, recorded_by) VALUES (1, NULL, -11, NULL, NULL, NULL, '2025-06-25 12:21:16.485666', 1, 2, NULL);
INSERT INTO public.commodity_stock_history (id, change_type, new_balance, notes, previous_balance, quantity_changed, record_date, commodity_id, community_unit_id, recorded_by) VALUES (2, NULL, -31, NULL, NULL, NULL, '2025-06-25 12:44:00.462249', 1, 1, NULL);
INSERT INTO public.commodity_stock_history (id, change_type, new_balance, notes, previous_balance, quantity_changed, record_date, commodity_id, community_unit_id, recorded_by) VALUES (3, NULL, -139, NULL, NULL, NULL, '2025-06-25 12:50:22.230994', 1, 3, NULL);
INSERT INTO public.commodity_stock_history (id, change_type, new_balance, notes, previous_balance, quantity_changed, record_date, commodity_id, community_unit_id, recorded_by) VALUES (4, NULL, -9, NULL, NULL, NULL, '2025-06-25 21:18:40.111053', 1, 5, NULL);
INSERT INTO public.commodity_stock_history (id, change_type, new_balance, notes, previous_balance, quantity_changed, record_date, commodity_id, community_unit_id, recorded_by) VALUES (5, NULL, -74, NULL, NULL, NULL, '2025-06-28 16:23:45.391344', 1, 7, NULL);
INSERT INTO public.commodity_stock_history (id, change_type, new_balance, notes, previous_balance, quantity_changed, record_date, commodity_id, community_unit_id, recorded_by) VALUES (6, NULL, -76, NULL, NULL, NULL, '2025-06-28 17:16:35.194917', 1, 8, NULL);
INSERT INTO public.commodity_stock_history (id, change_type, new_balance, notes, previous_balance, quantity_changed, record_date, commodity_id, community_unit_id, recorded_by) VALUES (7, NULL, -10, NULL, NULL, NULL, '2025-07-11 23:17:18.426097', 1, 12, NULL);
INSERT INTO public.commodity_stock_history (id, change_type, new_balance, notes, previous_balance, quantity_changed, record_date, commodity_id, community_unit_id, recorded_by) VALUES (8, NULL, 10, NULL, NULL, NULL, '2025-07-11 23:48:36.286554', 1, 1, NULL);
INSERT INTO public.commodity_stock_history (id, change_type, new_balance, notes, previous_balance, quantity_changed, record_date, commodity_id, community_unit_id, recorded_by) VALUES (9, NULL, -21, NULL, NULL, NULL, '2025-07-11 23:50:19.128028', 1, 10, NULL);
INSERT INTO public.commodity_stock_history (id, change_type, new_balance, notes, previous_balance, quantity_changed, record_date, commodity_id, community_unit_id, recorded_by) VALUES (10, NULL, -12, NULL, NULL, NULL, '2025-07-11 23:52:32.577857', 1, 10, NULL);
INSERT INTO public.commodity_stock_history (id, change_type, new_balance, notes, previous_balance, quantity_changed, record_date, commodity_id, community_unit_id, recorded_by) VALUES (11, NULL, 7, NULL, NULL, NULL, '2025-07-11 23:59:30.911144', 1, 3, NULL);
INSERT INTO public.commodity_stock_history (id, change_type, new_balance, notes, previous_balance, quantity_changed, record_date, commodity_id, community_unit_id, recorded_by) VALUES (12, NULL, -68, NULL, NULL, NULL, '2025-07-12 00:06:31.358439', 1, 3, NULL);
INSERT INTO public.commodity_stock_history (id, change_type, new_balance, notes, previous_balance, quantity_changed, record_date, commodity_id, community_unit_id, recorded_by) VALUES (13, NULL, 10, NULL, NULL, NULL, '2025-07-12 00:53:59.097365', 1, 1, NULL);
INSERT INTO public.commodity_stock_history (id, change_type, new_balance, notes, previous_balance, quantity_changed, record_date, commodity_id, community_unit_id, recorded_by) VALUES (14, NULL, -203, NULL, NULL, NULL, '2025-07-12 01:01:16.101799', 1, 11, NULL);
INSERT INTO public.commodity_stock_history (id, change_type, new_balance, notes, previous_balance, quantity_changed, record_date, commodity_id, community_unit_id, recorded_by) VALUES (15, NULL, -3, NULL, NULL, NULL, '2025-07-12 15:00:21.365031', 1, 1, NULL);
INSERT INTO public.commodity_stock_history (id, change_type, new_balance, notes, previous_balance, quantity_changed, record_date, commodity_id, community_unit_id, recorded_by) VALUES (16, NULL, -47, NULL, NULL, NULL, '2025-07-13 00:12:16.132158', 1, 11, NULL);
INSERT INTO public.commodity_stock_history (id, change_type, new_balance, notes, previous_balance, quantity_changed, record_date, commodity_id, community_unit_id, recorded_by) VALUES (17, NULL, 442, NULL, NULL, NULL, '2025-07-13 00:15:21.364019', 1, 11, NULL);
INSERT INTO public.commodity_stock_history (id, change_type, new_balance, notes, previous_balance, quantity_changed, record_date, commodity_id, community_unit_id, recorded_by) VALUES (18, NULL, -978, NULL, NULL, NULL, '2025-07-13 12:47:18.386028', 1, 11, NULL);
INSERT INTO public.commodity_stock_history (id, change_type, new_balance, notes, previous_balance, quantity_changed, record_date, commodity_id, community_unit_id, recorded_by) VALUES (19, NULL, 6229, NULL, NULL, NULL, '2025-07-13 12:48:03.745366', 1, 11, NULL);
INSERT INTO public.commodity_stock_history (id, change_type, new_balance, notes, previous_balance, quantity_changed, record_date, commodity_id, community_unit_id, recorded_by) VALUES (20, NULL, -178, NULL, NULL, NULL, '2025-07-13 13:08:00.991869', 1, 11, NULL);
INSERT INTO public.commodity_stock_history (id, change_type, new_balance, notes, previous_balance, quantity_changed, record_date, commodity_id, community_unit_id, recorded_by) VALUES (21, NULL, -367, NULL, NULL, NULL, '2025-07-13 13:29:02.876834', 1, 12, NULL);


--
-- Data for Name: facility_wards; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.facility_wards (facility_id, ward_id) VALUES (2, 1);
INSERT INTO public.facility_wards (facility_id, ward_id) VALUES (2, 2);
INSERT INTO public.facility_wards (facility_id, ward_id) VALUES (3, 1);
INSERT INTO public.facility_wards (facility_id, ward_id) VALUES (3, 2);


--
-- Name: cha_chp_mapping_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.cha_chp_mapping_id_seq', 13, true);


--
-- Name: commodities_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.commodities_id_seq', 1, true);


--
-- Name: commodity_categories_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.commodity_categories_id_seq', 1, true);


--
-- Name: commodity_records_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.commodity_records_id_seq', 21, true);


--
-- Name: commodity_stock_history_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.commodity_stock_history_id_seq', 21, true);


--
-- Name: community_units_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.community_units_id_seq', 22, true);


--
-- Name: counties_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.counties_id_seq', 2, true);


--
-- Name: facilities_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.facilities_id_seq', 3, true);


--
-- Name: sub_counties_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.sub_counties_id_seq', 1, true);


--
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.users_id_seq', 25, true);


--
-- Name: wards_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.wards_id_seq', 2, true);


--
-- PostgreSQL database dump complete
--

