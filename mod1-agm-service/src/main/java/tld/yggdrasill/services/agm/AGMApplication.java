package tld.yggdrasill.services.agm;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;

import java.util.Arrays;

@SpringBootApplication
@EnableKafka
@EnableProcessApplication
@Slf4j
@EnableDiscoveryClient
@EnableFeignClients
public class AGMApplication implements ApplicationRunner {

  public static void main(String[] args) {
    SpringApplication.run(AGMApplication.class, args);
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    log.info("Application started with command-line arguments: {}", Arrays.toString(args.getSourceArgs()));
    log.info("NonOptionArgs: {}", args.getNonOptionArgs());
    log.info("OptionNames: {}", args.getOptionNames());

    for (String name : args.getOptionNames()) {
      log.info("arg-" + name + "=" + args.getOptionValues(name));
    }
  }

  @Bean
  ProcessEnginePlugin disableTelemetry() {

    return new ProcessEnginePlugin() {
      @Override
      public void preInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
        processEngineConfiguration.setTelemetryReporterActivate(false);
        processEngineConfiguration.setInitializeTelemetry(false);
      }

      @Override
      public void postInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
      }

      @Override
      public void postProcessEngineBuild(ProcessEngine processEngine) {
      }
    };
  }
}
