package com.pia.client;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.CompositeArchRule;
import com.tngtech.archunit.library.DependencyRules;
import com.tngtech.archunit.library.GeneralCodingRules;
import com.tngtech.archunit.library.ProxyRules;
import org.springframework.cache.annotation.Cacheable;

/**
 *
 * @author Gokhhan Demir
 */
@AnalyzeClasses(packages = "com.pia.client", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTests {

    @ArchTest
    static final ArchRule implement_general_coding_practices = CompositeArchRule.of(
            GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS)
            .and(GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS)
            .and(GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING)
            .and(GeneralCodingRules.NO_CLASSES_SHOULD_USE_JODATIME)
            .and(GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION);

  @ArchTest
  static final ArchRule noClasses_shouldDependOnUpperPackages =
      DependencyRules.NO_CLASSES_SHOULD_DEPEND_UPPER_PACKAGES;

  @ArchTest
  static final ArchRule noClasses_shouldDirectlyCallOtherMethodsDeclaredInTheSameClassThatAreAnnotatedWithCacheable =
      ProxyRules.no_classes_should_directly_call_other_methods_declared_in_the_same_class_that_are_annotated_with(
          Cacheable.class);

}
