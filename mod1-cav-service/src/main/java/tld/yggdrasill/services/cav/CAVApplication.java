package tld.yggdrasill.services.cav;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
@EnableDiscoveryClient
@EnableFeignClients
public class CAVApplication {

    public static void main(String[] args) {
        SpringApplication.run(CAVApplication.class, args);
    }
}
