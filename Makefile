GRADLEW := ./gradlew
GRADLE_FLAGS ?= --no-configuration-cache

.PHONY: runClient runServer test gradle stop

runClient:
	$(GRADLEW) runClient $(GRADLE_FLAGS)

runServer:
	$(GRADLEW) runServer $(GRADLE_FLAGS)

test:
	$(GRADLEW) test $(GRADLE_FLAGS)

gradle:
	$(GRADLEW) $(TASK) $(GRADLE_FLAGS)

stop:
	$(GRADLEW) --stop
