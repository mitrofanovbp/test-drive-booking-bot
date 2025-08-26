SHELL := /bin/sh

.PHONY: up down logs rebuild db-up psql

up:
	docker compose up -d --build

down:
	docker compose down -v

logs:
	docker compose logs -f app

rebuild:
	docker compose build --no-cache app

db-up:
	docker compose up -d db

psql:
	docker compose exec -e PGPASSWORD=$${DB_PASSWORD:-postgres} db psql -U $${DB_USER:-postgres} -d $${DB_NAME:-testdrive_booking_bot}
