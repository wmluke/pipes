
clean:
	JAVA_HOME=$(JAVA8_HOME) mvn clean

install:
	JAVA_HOME=$(JAVA8_HOME) mvn install

test:
	JAVA_HOME=$(JAVA8_HOME) mvn test

build:
	JAVA_HOME=$(JAVA8_HOME) mvn clean install -Dmaven.test.skip=true

sources:
	JAVA_HOME=$(JAVA8_HOME) mvn dependency:sources

.PHONY: clean install test build sources
