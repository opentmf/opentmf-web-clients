package com.pia.client;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.core.domain.properties.HasType.Functions.GET_RAW_TYPE;
import static com.tngtech.archunit.lang.conditions.ArchConditions.dependOnClassesThat;
import static com.tngtech.archunit.lang.conditions.ArchConditions.setFieldWhere;

import com.tngtech.archunit.core.domain.AccessTarget.FieldAccessTarget;
import com.tngtech.archunit.core.domain.JavaAccess.Functions.Get;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaFieldAccess;
import com.tngtech.archunit.lang.ArchCondition;

/**
 * @author Mustafa Ulu
 */
class ArchitectureConditions {

  static final ArchCondition<JavaClass> USE_ORG_APACHE_LOGGING_LOG4J =
      setFieldWhere(resideInAPackage("org.apache.logging.log4j..")
          .onResultOf(Get.<JavaFieldAccess, FieldAccessTarget>target().then(GET_RAW_TYPE)))
          .as("use org.apache.logging.log4j");

  static final ArchCondition<JavaClass> USE_ORG_JBOSS_LOGGING =
      setFieldWhere(resideInAPackage("org.jboss.logging..")
          .onResultOf(Get.<JavaFieldAccess, FieldAccessTarget>target().then(GET_RAW_TYPE)))
          .as("use org.jboss.logging");

  static final ArchCondition<JavaClass> USE_ORG_TESTCONTAINERS_SHADED =
      dependOnClassesThat(resideInAPackage("org.testcontainers.shaded.."))
          .as("use org.testcontainers.shaded");

}
