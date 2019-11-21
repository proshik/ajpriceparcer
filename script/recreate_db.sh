#!/bin/bash

export PGPASSWORD=postgres
psql -U postgres -h localhost -p 5432 postgres -c "SELECT pg_terminate_backend(pg_stat_activity.pid) FROM pg_stat_activity WHERE pg_stat_activity.datname = 'apple_prices_db' AND pid <> pg_backend_pid();"
psql -U postgres -h localhost -p 5432 postgres -c "drop database IF EXISTS apple_prices_db;"
psql -U postgres -h localhost -p 5432 postgres -c "create database apple_prices_db;"
