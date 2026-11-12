#!/bin/sh
# Crée une base par microservice (exécuté une seule fois, au premier démarrage du volume).
set -e
for db in keycloak delivery_db event_db partnership_db srv_db marketplace_db; do
  echo "Creating database $db"
  psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname postgres \
    -c "CREATE DATABASE $db OWNER $POSTGRES_USER;"
done
