
clean:
	mvn clean

install:
	mvn install

test:
	mvn test

site:
	mvn site

build:
	mvn clean install -Dmaven.test.skip=true

run:
	cd examples; mvn exec:java -Dexec.mainClass="JettyApp" -Dexec.args="8888"

start:
	java -jar examples/target/pipes-examples-0.1.0-SNAPSHOT.jar

deploy:
	mvn clean deploy

sources:
	mvn dependency:sources

jdkinfo:
	mvn --version

.PHONY: clean install test site build run start deploy sources jdkinfo
