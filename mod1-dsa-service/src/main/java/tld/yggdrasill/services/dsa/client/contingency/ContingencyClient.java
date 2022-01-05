package tld.yggdrasill.services.dsa.client.contingency;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import tld.yggdrasill.services.dsa.client.contingency.config.FeignClientConfiguration;
import tld.yggdrasill.services.dsa.client.contingency.model.Contingency;

@FeignClient(name = "contingency-service",
	url = "${app.client.contingency-service.url}",
	configuration = FeignClientConfiguration.class
)
public interface ContingencyClient {
	@GetMapping(value = "/{mRID}")
	Contingency getContingencyById(@PathVariable("mRID") String mRID);
}
