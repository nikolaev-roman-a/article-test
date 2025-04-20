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

docker-up:
	docker-compose up --build
