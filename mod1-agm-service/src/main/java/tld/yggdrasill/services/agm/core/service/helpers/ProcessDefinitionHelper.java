package tld.yggdrasill.services.agm.core.service.helpers;

import org.apache.commons.text.StringSubstitutor;
import tld.yggdrasill.services.agm.core.model.ProcessDefinition;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProcessDefinitionHelper {

  public static String sanitizeName(String name) {
    if (name != null) {
      return name.replaceAll("[^A-Za-z0-9]+", "-");
    }
    return null;
  }

  //-- StringSubstitutor is not thread-safe
  public synchronized static String formatBpmnProcessName(String processName, ProcessDefinition processDefinition) {
    Map<String, String> valuesMap = new HashMap<>();
    valuesMap.put("contingencyId", processDefinition.getContingencyId());
    valuesMap.put("contingencyName", sanitizeName(processDefinition.getContingencyName()));
    StringSubstitutor substitutor = new StringSubstitutor(valuesMap);
    return substitutor.replace(processName);
  }

  //-- GCPK - Grid Camunda Process Key
  public static String formatBpmnProcessId(ProcessDefinition processDefinition) {
    String DEFAULT_PROCESS_ID_FORMAT = "GCPK-%s-%s";
    return String.format(DEFAULT_PROCESS_ID_FORMAT, sanitizeName(processDefinition.getContingencyName()),
      UUID.randomUUID().toString().substring(0, 8));
  }

  //-- GCDK - Grid Camunda Deployment Key
  public static String formatBpmnProcessDefinitionId(ProcessDefinition processDefinition) {
    String DEFAULT_PROCESS_DEFINITION_FORMAT = "GCDK-%s-%s";
    return String.format(DEFAULT_PROCESS_DEFINITION_FORMAT, sanitizeName(processDefinition.getContingencyName()),
      UUID.randomUUID().toString().substring(0, 8));
  }
}
