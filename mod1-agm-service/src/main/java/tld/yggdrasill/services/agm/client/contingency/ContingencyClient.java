package tld.yggdrasill.services.agm.client.contingency;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import tld.yggdrasill.services.agm.client.contingency.config.ContingencyClientConfiguration;
import tld.yggdrasill.services.agm.client.contingency.config.exceptions.BadRequestException;
import tld.yggdrasill.services.agm.client.contingency.config.exceptions.ContingencyNotFoundException;
import tld.yggdrasill.services.agm.client.contingency.model.ActuatorHealthResponse;
import tld.yggdrasill.services.agm.client.contingency.model.ContingencyResponse;

@FeignClient(name = "contingency-service",
	url = "${app.client.contingency-service.url}",
	configuration = ContingencyClientConfiguration.class
)
public interface ContingencyClient {
	@GetMapping("/manage/health")
	ActuatorHealthResponse health();

	@GetMapping(value = "/{mRID}")
  ContingencyResponse getContingencyById(@PathVariable("mRID") String mRID) throws ContingencyNotFoundException, BadRequestException;
}
