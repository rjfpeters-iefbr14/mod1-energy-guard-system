package tld.yggdrasill.services.agm.client.contingency.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.zalando.problem.Problem;
import org.zalando.problem.jackson.ProblemModule;
import tld.yggdrasill.services.agm.client.contingency.config.exceptions.BadRequestException;
import tld.yggdrasill.services.agm.client.contingency.config.exceptions.ContingencyNotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;


//@Component
public class ContingencyErrorDecoder implements ErrorDecoder {
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

          return new ContingencyNotFoundException(problem.toString());
        } catch (IOException e) {
          return new Exception(e.getMessage());
        }
      default:
        return new Exception(response.reason());
    }
  }
}
