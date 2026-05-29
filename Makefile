JAVA_HOME ?= /usr/lib/jvm/java-21-openjdk
GRADLEW := ./gradlew
GRADLE_ENV := JAVA_HOME="$(JAVA_HOME)"
GRADLE_FLAGS ?= --no-configuration-cache

.PHONY: runClient runServer test gradle stop

runClient:
	$(GRADLE_ENV) $(GRADLEW) runClient $(GRADLE_FLAGS)

runServer:
	$(GRADLE_ENV) $(GRADLEW) runServer $(GRADLE_FLAGS)

test:
	$(GRADLE_ENV) $(GRADLEW) test $(GRADLE_FLAGS)

gradle:
	$(GRADLE_ENV) $(GRADLEW) $(TASK) $(GRADLE_FLAGS)

stop:
	$(GRADLE_ENV) $(GRADLEW) --stop
