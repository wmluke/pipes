
clean:
	JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0.jdk/Contents/Home mvn clean

install:
	JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0.jdk/Contents/Home mvn install

test:
	JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0.jdk/Contents/Home mvn test

build:
	JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0.jdk/Contents/Home mvn clean install -Dmaven.test.skip=true

sources:
	JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0.jdk/Contents/Home mvn dependency:sources

.PHONY: clean install test build sources
