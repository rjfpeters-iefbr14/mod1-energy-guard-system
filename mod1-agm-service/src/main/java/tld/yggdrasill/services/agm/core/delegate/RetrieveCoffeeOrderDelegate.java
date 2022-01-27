package tld.yggdrasill.services.agm.core.delegate;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import javax.inject.Named;

@Slf4j
@Component
@Named("retrieveCoffeeOrder")
public class RetrieveCoffeeOrderDelegate implements JavaDelegate {

  public void execute(DelegateExecution execution) throws Exception {
    String coffeeOrder = (String) execution.getVariable("order");

    log.info("Order Coffee Process: " + execution.getCurrentActivityName() + " - " + coffeeOrder);
  }

}
