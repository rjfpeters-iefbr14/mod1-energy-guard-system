package tld.yggdrasill.services.pcm;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

public class CleanArchitectureTest {

  @Test
  public void testPackages() {
    JavaClasses importedClasses = new ClassFileImporter()
     .importPackages(PCMApplication.class.getPackage().toString());

    classes().that()
      .resideInAPackage("..core..")
      .should()
      .onlyBeAccessed()
      .byAnyPackage("..api..", "..client..")
      .check(importedClasses);

    classes().that()
      .resideInAnyPackage("..api..", "..client..")
      .should()
      .onlyDependOnClassesThat()
      .resideInAPackage("..core..")
      .check(importedClasses);

    layeredArchitecture()
      .layer("Api").definedBy("..api..")
      .layer("Core").definedBy("..core..")
      .layer("Client").definedBy("..client..")

      .whereLayer("Api").mayNotBeAccessedByAnyLayer()
      .whereLayer("Core").mayOnlyBeAccessedByLayers("Api", "Client")
      .whereLayer("Client").mayNotBeAccessedByAnyLayer();

    classes()
      .that()
      .haveNameMatching("..Controller")
      .should()
      .beAnnotatedWith(RestController.class)
      .check(importedClasses);

    classes()
      .that()
      .haveNameMatching("..Properties")
      .should()
      .beAnnotatedWith(ConfigurationProperties.class)
      .check(importedClasses);

    classes()
      .that()
      .haveNameMatching("..ClientConfiguration")
      .should()
      .beAnnotatedWith(Configuration.class)
      .check(importedClasses);

    classes()
      .that()
      .haveNameMatching("..Service")
      .should()
      .beAnnotatedWith(Service.class)
      .check(importedClasses);
  }
}
