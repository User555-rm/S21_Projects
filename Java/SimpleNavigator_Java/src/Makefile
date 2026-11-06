JAVA_RELEASE ?= 17
ifdef JAVA_HOME
ifeq ($(OS),Windows_NT)
JAVAC = "$(JAVA_HOME)/bin/javac.exe" --release $(JAVA_RELEASE) -encoding UTF-8
JAR = "$(JAVA_HOME)/bin/jar.exe"
JAVA = "$(JAVA_HOME)/bin/java.exe"
else
JAVAC = "$(JAVA_HOME)/bin/javac" --release $(JAVA_RELEASE) -encoding UTF-8
JAR = "$(JAVA_HOME)/bin/jar"
JAVA = "$(JAVA_HOME)/bin/java"
endif
else
JAVAC = javac --release $(JAVA_RELEASE) -encoding UTF-8
JAR = jar
JAVA = java
endif

SRC_DIR = main/java
TEST_DIR = test/java
LIB_DIR = lib
BUILD_DIR = build

GRAPH_PKG = s21_graph
ALGORITHMS_PKG = s21_graph_algorithms
DATA_STRUCTURES_PKG = data_structures
CONSOLE_PKG = console

GRAPH_SRC = $(SRC_DIR)/$(GRAPH_PKG)/Graph.java
ALGORITHMS_SRC = $(SRC_DIR)/$(ALGORITHMS_PKG)/GraphAlgorithms.java \
                 $(SRC_DIR)/$(ALGORITHMS_PKG)/TsmResult.java
DATA_STRUCTURES_SRC = $(SRC_DIR)/$(DATA_STRUCTURES_PKG)/Stack.java \
                      $(SRC_DIR)/$(DATA_STRUCTURES_PKG)/Queue.java
CONSOLE_SRC = $(SRC_DIR)/$(CONSOLE_PKG)/Main.java

ALL_SRC = $(GRAPH_SRC) $(ALGORITHMS_SRC) $(DATA_STRUCTURES_SRC) $(CONSOLE_SRC)

GRAPH_JAR = $(LIB_DIR)/s21_graph.jar
ALGORITHMS_JAR = $(LIB_DIR)/s21_graph_algorithms.jar
CONSOLE_JAR = $(LIB_DIR)/s21_navigator.jar

JUNIT_VERSION = 1.10.0
JUNIT_JAR = $(LIB_DIR)/junit-platform-console-standalone-$(JUNIT_VERSION).jar
JUNIT_URL = https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/$(JUNIT_VERSION)/junit-platform-console-standalone-$(JUNIT_VERSION).jar
TEST_SRC = $(TEST_DIR)/GraphTest.java \
           $(TEST_DIR)/GraphAlgorithmsTest.java
TEST_CLASSES = $(BUILD_DIR)/test

JACOCO_VERSION = 0.8.12
JACOCO_JAR = $(LIB_DIR)/jacocoagent.jar
JACOCO_CLI_JAR = $(LIB_DIR)/jacococli.jar
JACOCO_ZIP = $(LIB_DIR)/jacoco.zip
JACOCO_UNPACK_DIR = $(LIB_DIR)/jacoco-unpack
JACOCO_EXEC = $(BUILD_DIR)/jacoco.exec
COVERAGE_DIR = coverage-report

ifeq ($(OS),Windows_NT)
CP_SEP = ;
MKDIR_P = if not exist "$(subst /,\,$(1))" mkdir "$(subst /,\,$(1))"
RM_RF = if exist "$(subst /,\,$(1))" rmdir /S /Q "$(subst /,\,$(1))"
RM_F = if exist "$(subst /,\,$(1))" del /Q "$(subst /,\,$(1))"
DOWNLOAD = curl -L -o "$(subst /,\,$(1))" "$(2)"
JACOCO_EXTRACT = powershell -NoProfile -ExecutionPolicy Bypass -Command "Expand-Archive -Force '$(subst /,\,$(JACOCO_ZIP))' '$(subst /,\,$(JACOCO_UNPACK_DIR))'; Copy-Item '$(subst /,\,$(JACOCO_UNPACK_DIR)/lib/jacocoagent.jar)' '$(subst /,\,$(JACOCO_JAR))' -Force; Copy-Item '$(subst /,\,$(JACOCO_UNPACK_DIR)/lib/jacococli.jar)' '$(subst /,\,$(JACOCO_CLI_JAR))' -Force; Remove-Item '$(subst /,\,$(JACOCO_UNPACK_DIR))' -Recurse -Force"
else
CP_SEP = :
MKDIR_P = mkdir -p $(1)
RM_RF = rm -rf $(1)
RM_F = rm -f $(1)
DOWNLOAD = curl -L -o "$(1)" "$(2)"
JACOCO_EXTRACT = unzip -o -j "$(JACOCO_ZIP)" "lib/jacocoagent.jar" -d "$(LIB_DIR)" && unzip -o -j "$(JACOCO_ZIP)" "lib/jacococli.jar" -d "$(LIB_DIR)"
endif

.PHONY: all clean test coverage style clang s21_graph s21_graph_algorithms console run

all: s21_graph s21_graph_algorithms console

s21_graph: $(GRAPH_JAR)

$(GRAPH_JAR): $(GRAPH_SRC)
	@$(call MKDIR_P,$(LIB_DIR))
	@$(call MKDIR_P,$(BUILD_DIR))
	$(JAVAC) -d $(BUILD_DIR) $(GRAPH_SRC)
	$(JAR) cf $(GRAPH_JAR) -C $(BUILD_DIR) $(GRAPH_PKG)
	@echo Built s21_graph.jar successfully

s21_graph_algorithms: $(ALGORITHMS_JAR)

$(ALGORITHMS_JAR): $(ALGORITHMS_SRC) $(DATA_STRUCTURES_SRC) $(GRAPH_JAR)
	@$(call MKDIR_P,$(LIB_DIR))
	@$(call MKDIR_P,$(BUILD_DIR))
	$(JAVAC) -d $(BUILD_DIR) -cp "$(GRAPH_JAR)" $(ALGORITHMS_SRC) $(DATA_STRUCTURES_SRC)
	$(JAR) cf $(ALGORITHMS_JAR) -C $(BUILD_DIR) $(ALGORITHMS_PKG) -C $(BUILD_DIR) $(DATA_STRUCTURES_PKG)
	@echo Built s21_graph_algorithms.jar successfully

console: $(CONSOLE_JAR)

$(CONSOLE_JAR): $(CONSOLE_SRC) $(GRAPH_JAR) $(ALGORITHMS_JAR)
	@$(call MKDIR_P,$(LIB_DIR))
	@$(call MKDIR_P,$(BUILD_DIR))
	$(JAVAC) -d $(BUILD_DIR) -cp "$(GRAPH_JAR)$(CP_SEP)$(ALGORITHMS_JAR)" $(CONSOLE_SRC)
	$(JAR) cfe $(CONSOLE_JAR) $(CONSOLE_PKG).Main -C $(BUILD_DIR) $(CONSOLE_PKG)
	@echo Built s21_navigator.jar successfully

run: console
	$(JAVA) -cp "$(CONSOLE_JAR)$(CP_SEP)$(GRAPH_JAR)$(CP_SEP)$(ALGORITHMS_JAR)" $(CONSOLE_PKG).Main

$(JUNIT_JAR):
	@$(call MKDIR_P,$(LIB_DIR))
	@echo JUnit jar not found. Downloading...
	$(call DOWNLOAD,$(JUNIT_JAR),$(JUNIT_URL))

test: $(GRAPH_JAR) $(ALGORITHMS_JAR) $(JUNIT_JAR)
	@$(call MKDIR_P,$(TEST_CLASSES))
	$(JAVAC) -d $(TEST_CLASSES) -cp "$(GRAPH_JAR)$(CP_SEP)$(ALGORITHMS_JAR)$(CP_SEP)$(JUNIT_JAR)" $(TEST_SRC)
	$(JAVA) -jar $(JUNIT_JAR) execute --class-path "$(TEST_CLASSES)$(CP_SEP)$(GRAPH_JAR)$(CP_SEP)$(ALGORITHMS_JAR)" --scan-class-path

$(JACOCO_JAR) $(JACOCO_CLI_JAR):
	@$(call MKDIR_P,$(LIB_DIR))
	@echo Downloading JaCoCo $(JACOCO_VERSION)...
	$(call DOWNLOAD,$(JACOCO_ZIP),https://github.com/jacoco/jacoco/releases/download/v$(JACOCO_VERSION)/jacoco-$(JACOCO_VERSION).zip)
	$(JACOCO_EXTRACT)
	$(call RM_F,$(JACOCO_ZIP))

coverage: $(GRAPH_JAR) $(ALGORITHMS_JAR) $(JUNIT_JAR) $(JACOCO_JAR) $(JACOCO_CLI_JAR)
	@$(call MKDIR_P,$(TEST_CLASSES))
	$(JAVAC) -d $(TEST_CLASSES) -cp "$(GRAPH_JAR)$(CP_SEP)$(ALGORITHMS_JAR)$(CP_SEP)$(JUNIT_JAR)" $(TEST_SRC)
	$(JAVA) -javaagent:$(JACOCO_JAR)=destfile=$(JACOCO_EXEC) -jar $(JUNIT_JAR) execute --class-path "$(TEST_CLASSES)$(CP_SEP)$(GRAPH_JAR)$(CP_SEP)$(ALGORITHMS_JAR)" --scan-class-path
	@$(call MKDIR_P,$(COVERAGE_DIR))
	$(JAVA) -jar $(JACOCO_CLI_JAR) report $(JACOCO_EXEC) --classfiles $(BUILD_DIR)/$(GRAPH_PKG) --classfiles $(BUILD_DIR)/$(ALGORITHMS_PKG) --classfiles $(BUILD_DIR)/$(DATA_STRUCTURES_PKG) --sourcefiles $(SRC_DIR)/$(GRAPH_PKG) --sourcefiles $(SRC_DIR)/$(ALGORITHMS_PKG) --sourcefiles $(SRC_DIR)/$(DATA_STRUCTURES_PKG) --html $(COVERAGE_DIR) --name "SimpleNavigator Coverage Report"
	@echo Coverage report generated: $(COVERAGE_DIR)/index.html

style:
	@echo Checking Java code style with clang-format...
	clang-format -n --Werror $(ALL_SRC) $(TEST_SRC)
	@echo Style check passed!

clang:
	@echo Formatting Java code with clang-format...
	clang-format -i $(ALL_SRC) $(TEST_SRC)
	@echo Formatting complete!

clean:
	@$(call RM_RF,$(BUILD_DIR))
	@$(call RM_F,$(LIB_DIR)/*.jar)
	@$(call RM_F,$(JACOCO_ZIP))
	@$(call RM_RF,$(JACOCO_UNPACK_DIR))
	@$(call RM_RF,$(COVERAGE_DIR))
	@echo Clean complete
