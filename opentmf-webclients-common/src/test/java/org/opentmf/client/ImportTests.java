package org.opentmf.client;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.GeneralCodingRules;

/**
 * @author Gokhan Demir
 */
@AnalyzeClasses(
    packages = "org.opentmf.client"
)
class ImportTests {

  @ArchTest
  static final ArchRule noClasses_shouldUseJavaUtilLogging =
      GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING;

  @ArchTest
  static final ArchRule noClasses_shouldUseJodatime =
      GeneralCodingRules.NO_CLASSES_SHOULD_USE_JODATIME;

  @ArchTest
  static final ArchRule noClasses_shouldUseOrgApacheLoggingLog4j =
      noClasses().should(ArchitectureConditions.USE_ORG_APACHE_LOGGING_LOG4J);

  @ArchTest
  static final ArchRule noClasses_shouldUseOrgJbossLogging =
      noClasses().should(ArchitectureConditions.USE_ORG_JBOSS_LOGGING);

  @ArchTest
  static final ArchRule noClasses_shouldUseOrgTestcontainersShaded =
      noClasses().should(ArchitectureConditions.USE_ORG_TESTCONTAINERS_SHADED);

}
