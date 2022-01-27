package tld.yggdrasill.services.agm.core.delegate;

import java.util.logging.Logger;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import javax.inject.Named;

@Slf4j
@Component
@Named("loggerDelegate")
public class LoggerDelegate implements JavaDelegate {
   public void execute(DelegateExecution execution) throws Exception {
    log.info("... LoggerDelegate invoked by "
            + "processDefinitionId=" + execution.getProcessDefinitionId()
            + ", activityId=" + execution.getCurrentActivityId()
            + ", activityName='" + execution.getCurrentActivityName() + "'"
            + ", processInstanceId=" + execution.getProcessInstanceId()
            + ", businessKey=" + execution.getProcessBusinessKey()
            + ", executionId=" + execution.getId());
  }
}
