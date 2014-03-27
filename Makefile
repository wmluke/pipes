
clean:
	JAVA_HOME=$(JAVA8_HOME) mvn clean

install:
	JAVA_HOME=$(JAVA8_HOME) mvn install

test:
	JAVA_HOME=$(JAVA8_HOME) mvn test

build:
	JAVA_HOME=$(JAVA8_HOME) mvn clean install -Dmaven.test.skip=true

run:
	cd examples; JAVA_HOME=$(JAVA8_HOME) mvn exec:java -Dexec.mainClass="JettyApp" -Dexec.args="8888"

deploy:
	JAVA_HOME=$(JAVA8_HOME) mvn clean deploy

sources:
	JAVA_HOME=$(JAVA8_HOME) mvn dependency:sources

jdkinfo:
	JAVA_HOME=$(JAVA8_HOME) mvn --version

.PHONY: clean install test build run deploy sources jdkinfo
