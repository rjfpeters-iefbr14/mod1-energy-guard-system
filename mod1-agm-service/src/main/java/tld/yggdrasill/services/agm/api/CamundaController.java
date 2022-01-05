package tld.yggdrasill.services.agm.api;

import org.camunda.bpm.spring.boot.starter.rest.CamundaJerseyResourceConfig;
import org.springframework.stereotype.Component;

import javax.ws.rs.ApplicationPath;

@Component
@ApplicationPath("/resources")
public class CamundaController extends CamundaJerseyResourceConfig {

  @Override
  protected void registerAdditionalResources() {
    register(AGMController.class);
  }
}
