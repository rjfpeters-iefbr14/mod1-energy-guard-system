package tld.yggdrasill.services.pcm.api.common.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.DefaultProblem;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.spring.web.advice.ProblemHandling;
import org.zalando.problem.violations.ConstraintViolationProblem;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

import static java.lang.String.format;

@ControllerAdvice
@Slf4j
class ControllerExceptionHandler implements ProblemHandling {

  private static final String FIELD_ERRORS_KEY = "fieldErrors";
  private static final String ERROR_CODE_KEY = "errorcode";
  private static final String TIMESTAMP_KEY = "timestamp";
  private static final String MESSAGE_KEY = "message";
  private static final String PATH_KEY = "path";
  private static final String VIOLATIONS_KEY = "violations";

  // The causal chain of causes is disabled by default,
  // but we can easily enable it by overriding the behavior:
  @Override
  public boolean isCausalChainsEnabled() {
    return true;
  }

  /**
   * Post-process the Problem payload to add the message key for the front-end if needed
   */
  @Override
  public ResponseEntity<Problem> process(@Nullable ResponseEntity<Problem> entity, NativeWebRequest request) {
    if (entity == null) {
      return entity;
    }
    Problem problem = entity.getBody();
    if (!(problem instanceof ConstraintViolationProblem || problem instanceof DefaultProblem)) {
      return entity;
    }
    ProblemBuilder builder = Problem.builder()
                               .withType(Problem.DEFAULT_TYPE.equals(problem.getType()) ? ErrorConstants.DEFAULT_TYPE : problem.getType())
                               .withStatus(problem.getStatus())
                               .withTitle(problem.getTitle())
                               .with(PATH_KEY, request.getNativeRequest(HttpServletRequest.class).getRequestURI());

    if (problem instanceof ConstraintViolationProblem) {
      builder
        .with(VIOLATIONS_KEY, ((ConstraintViolationProblem) problem).getViolations())
        .with(MESSAGE_KEY, ErrorConstants.ERR_VALIDATION);
    } else {
      builder
        .withCause(((DefaultProblem) problem).getCause())
        .withDetail(problem.getDetail())
        .withInstance(problem.getInstance());
    }
    problem.getParameters().forEach(builder::with);
    if (!problem.getParameters().containsKey(MESSAGE_KEY) && problem.getStatus() != null) {
      builder.with(MESSAGE_KEY, "error.http." + problem.getStatus().getStatusCode());
    }
    return new ResponseEntity<>(builder.build(), entity.getHeaders(), entity.getStatusCode());
  }

  private void logError(Throwable throwable) {
    log.error(message(throwable), throwable);
  }

  private void logWarn(Throwable throwable) {
    log.warn(message(throwable));
  }

  private String message(final Throwable throwable) {
    return format("A(n) %s error occurred.", throwable.getClass().getSimpleName());
  }
}
