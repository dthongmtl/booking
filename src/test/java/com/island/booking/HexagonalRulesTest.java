package com.island.booking;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "com.island.booking")
public class HexagonalRulesTest {
	@ArchTest
	static final ArchRule domain_should_not_depends_on_infra =
			noClasses().that().resideInAPackage("..domain..")
					.should().accessClassesThat().resideInAPackage("org.springframework");

	@ArchTest
	static final ArchRule domain_should_not_depends_on_adapters =
			noClasses().that().resideInAPackage("..domain..")
					.should().accessClassesThat().resideInAPackage("..adapter..");

	@ArchTest
	static final ArchRule rest_adapter_should_not_depends_repository_adapter =
			noClasses().that().resideInAPackage("..rest..")
					.should().accessClassesThat().resideInAPackage("..repository..");
}
