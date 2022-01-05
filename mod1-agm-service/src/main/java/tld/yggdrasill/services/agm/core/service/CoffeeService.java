package tld.yggdrasill.services.agm.core.service;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CoffeeService {
  public void pourCoffee(DelegateExecution execution) throws Exception {
    String coffeeOrder = (String) execution.getVariable("order");

    log.info("Order Coffee Process: " + execution.getCurrentActivityName() + " - " + coffeeOrder);
  }
}
