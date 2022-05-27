package tld.yggdrasill.services.cav.client.contingency;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import tld.yggdrasill.services.cav.client.contingency.config.FeignClientConfiguration;
import tld.yggdrasill.services.cav.client.contingency.model.ContingencyResponse;

@FeignClient(name = "contingency-service",
	url = "${app.client.contingency-service.url}",
	configuration = FeignClientConfiguration.class
)
public interface ContingencyClient {
	@GetMapping(value = "/{mRID}")
  ContingencyResponse getContingencyById(@PathVariable("mRID") String mRID);
}
