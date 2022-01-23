package tld.yggdrasill.services.dsa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;

import java.util.Arrays;

//zalando-problem-spring-web -> Spring Boot, make sure you disable the ErrorMvcAutoConfiguration

@Slf4j
@EnableKafka
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication(exclude = {ErrorMvcAutoConfiguration.class})
public class DSAApplication implements ApplicationRunner {
    public static void main(String... args) throws Exception {
        SpringApplication.run(DSAApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("Application started with command-line arguments: {}", Arrays.toString(args.getSourceArgs()));
        log.info("NonOptionArgs: {}", args.getNonOptionArgs());
        log.info("OptionNames: {}", args.getOptionNames());

        for (String name : args.getOptionNames()) {
            log.info("arg-" + name + "=" + args.getOptionValues(name));
        }
    }
}
