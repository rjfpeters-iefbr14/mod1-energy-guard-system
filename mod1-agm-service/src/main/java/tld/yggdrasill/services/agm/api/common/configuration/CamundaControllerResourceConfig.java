package tld.yggdrasill.services.agm.api.common.configuration;

import org.camunda.bpm.spring.boot.starter.rest.CamundaJerseyResourceConfig;
import org.springframework.stereotype.Component;

import javax.ws.rs.ApplicationPath;

@Component
@ApplicationPath("/resources")
public class CamundaControllerResourceConfig extends CamundaJerseyResourceConfig {

}
