package tld.yggdrasill.services.dsa.client.contingency.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;
import tld.yggdrasill.services.dsa.client.contingency.config.exceptions.BadRequestException;
import tld.yggdrasill.services.dsa.client.contingency.config.exceptions.NotFoundException;

@Component
public class ContingencyErrorDecoder implements ErrorDecoder {
  @Override
  public Exception decode(String methodKey, Response response) {

    return switch (response.status()) {
      case 400 -> new BadRequestException(
        String.format("Status code = %d, , methodKey = %s", response.status(), methodKey));
      case 404 -> new NotFoundException(
        String.format(
          "Error took place when using Feign client to send HTTP Request. Status code = %d, , methodKey = %s",
          response.status(),methodKey));
      default -> new Exception(response.reason());
    };
  }
}
