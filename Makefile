ifneq ("$(wildcard .env)","")
	include .env
endif

.EXPORT_ALL_VARIABLES:

clean:
	rm -rf target

run:
	clj -M:dev

repl:
	clj -M:dev:cider

test:
	clj -M:test

uberjar:
	clj -T:build all

docker-build:
	docker-compose up --build

docker-up:
	docker-compose up
