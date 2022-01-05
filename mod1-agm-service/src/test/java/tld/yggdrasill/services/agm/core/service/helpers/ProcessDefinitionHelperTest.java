package tld.yggdrasill.services.agm.core.service.helpers;

import org.junit.jupiter.api.Test;
import tld.yggdrasill.services.agm.core.model.ProcessDefinition;

import static org.assertj.core.api.Assertions.assertThat;

class ProcessDefinitionHelperTest {

  @Test
  void successful_format_processName_without_spaces() {
    ProcessDefinition processDefinition = ProcessDefinition.builder()
      .contingencyName("Neerijnen").build();

    String processTemplate = "safety-case-${contingencyName}";
    String resolvedString = ProcessDefinitionHelper.formatBpmnProcessName(processTemplate,processDefinition);

    assertThat(resolvedString).isNotBlank().isEqualTo("safety-case-Neerijnen");
  }

  @Test
  void successful_format_processName_with_spaces() {
    ProcessDefinition processDefinition = ProcessDefinition.builder()
      .contingencyName("RS Neerijnen").build();

    String processTemplate = "safety-case-${contingencyName}";
    String resolvedString = ProcessDefinitionHelper.formatBpmnProcessName(processTemplate,processDefinition);

    assertThat(resolvedString).isNotBlank().isEqualTo("safety-case-RS-Neerijnen");
  }

  @Test
  void successful_format_processId() {
    ProcessDefinition processDefinition = ProcessDefinition.builder()
      .contingencyName("Neerijnen").build();

    String resolvedString = ProcessDefinitionHelper.formatBpmnProcessId(processDefinition);

    assertThat(resolvedString).isNotBlank().startsWith("GCPK-Neerijnen");
  }

  @Test
  void successful_format_processId_with_spaces() {
    ProcessDefinition processDefinition = ProcessDefinition.builder()
      .contingencyName("RS Neerijnen").build();

    String resolvedString = ProcessDefinitionHelper.formatBpmnProcessId(processDefinition);

    assertThat(resolvedString).isNotBlank().startsWith("GCPK-RS-Neerijnen");
  }

  @Test
  void successful_format_processDefinitionId() {
    ProcessDefinition processDefinition = ProcessDefinition.builder()
      .contingencyName("Neerijnen").build();

    String resolvedString = ProcessDefinitionHelper.formatBpmnProcessDefinitionId(processDefinition);

    assertThat(resolvedString).isNotBlank().startsWith("GCDK-Neerijnen");
  }

  @Test
  void successful_format_processDefinitionId_with_spaces() {
    ProcessDefinition processDefinition = ProcessDefinition.builder()
      .contingencyName("RS Neerijnen").build();

    String resolvedString = ProcessDefinitionHelper.formatBpmnProcessDefinitionId(processDefinition);

    assertThat(resolvedString).isNotBlank().startsWith("GCDK-RS-Neerijnen");
  }
}
