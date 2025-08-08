FROM postgres:16
# (16 is stable; adjust if you must match something else)
ENV POSTGRES_USER=postgres \
    POSTGRES_PASSWORD=postgres \
    POSTGRES_DB=ostock_dev

COPY init.sql /docker-entrypoint-initdb.d/1-init.sql
COPY data.sql /docker-entrypoint-initdb.d/2-data.sql
