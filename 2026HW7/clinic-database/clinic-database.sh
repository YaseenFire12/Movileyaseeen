#!/bin/bash
set -e
psql -v ON_ERROR_STOP=1 -v password="$DATABASE_PASSWORD" \
    --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE USER clinicuser PASSWORD :'password';
    CREATE DATABASE clinic WITH OWNER clinicuser;
    GRANT ALL PRIVILEGES ON DATABASE clinic TO clinicuser;
    \c clinic postgres
    GRANT ALL ON SCHEMA public TO clinicuser;
EOSQL
