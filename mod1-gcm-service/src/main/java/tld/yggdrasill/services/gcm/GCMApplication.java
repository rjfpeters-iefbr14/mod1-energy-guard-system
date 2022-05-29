package tld.yggdrasill.services.gcm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;

import java.util.Arrays;

@Slf4j
@SpringBootApplication(exclude = {ErrorMvcAutoConfiguration.class})
public class GCMApplication implements ApplicationRunner {
  public static void main(String... args) throws Exception {
    SpringApplication.run(GCMApplication.class, args);
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
}
