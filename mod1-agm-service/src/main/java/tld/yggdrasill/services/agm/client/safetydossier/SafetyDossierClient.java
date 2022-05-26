package tld.yggdrasill.services.agm.client.safetydossier;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import tld.yggdrasill.services.agm.client.safetydossier.config.SafetDossierClientConfiguration;
import tld.yggdrasill.services.agm.client.safetydossier.config.exceptions.BadRequestException;
import tld.yggdrasill.services.agm.client.safetydossier.config.exceptions.SafetyDossierNotFoundException;
import tld.yggdrasill.services.agm.client.safetydossier.model.ActuatorHealthResponse;
import tld.yggdrasill.services.agm.client.safetydossier.model.SafetyDossierResponse;

@FeignClient(name = "safety-dossier-service",
	url = "${app.client.safety-dossier-service.url}",
	configuration = SafetDossierClientConfiguration.class
)
public interface SafetyDossierClient {
	@GetMapping("/manage/health")
	ActuatorHealthResponse health();

	@GetMapping(value = "/safety-dossiers/{mRID}")
	SafetyDossierResponse getSafetyDossierById(@PathVariable("mRID") String mRID) throws SafetyDossierNotFoundException, BadRequestException;
}
