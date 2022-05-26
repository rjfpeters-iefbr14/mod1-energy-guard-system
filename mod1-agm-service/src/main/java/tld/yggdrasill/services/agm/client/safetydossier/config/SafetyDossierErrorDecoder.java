package tld.yggdrasill.services.agm.client.safetydossier.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;
import org.zalando.problem.Problem;
import org.zalando.problem.jackson.ProblemModule;
import tld.yggdrasill.services.agm.client.safetydossier.config.exceptions.BadRequestException;
import tld.yggdrasill.services.agm.client.safetydossier.config.exceptions.SafetyDossierNotFoundException;

import java.io.IOException;
import java.io.InputStream;


//@Component
public class SafetyDossierErrorDecoder implements ErrorDecoder {
  ObjectMapper objectMapper = new ObjectMapper().registerModule(new ProblemModule());

  @Override
  public Exception decode(String methodKey, Response response) {
    objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);

    switch (response.status()) {
      case 400:
        return new BadRequestException(
          String.format("Status code = %d, , methodKey = %s", response.status(), methodKey));
      case 404:
        try {
          InputStream body = response.body().asInputStream();
          Problem problem = objectMapper.readValue(body, Problem.class);

          return new SafetyDossierNotFoundException(problem.toString());
        } catch (IOException e) {
          return new Exception(e.getMessage());
        }
      default:
        return new Exception(response.reason());
    }
  }
}
