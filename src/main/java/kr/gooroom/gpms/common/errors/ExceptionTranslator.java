package kr.gooroom.gpms.common.errors;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.DefaultProblem;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.ThrowableProblem;
import org.zalando.problem.spring.web.advice.ProblemHandling;
import org.zalando.problem.violations.ConstraintViolationProblem;

/**
 * Controller advice to translate the server side exceptions to client-friendly
 * json structures. The error response follows RFC7807 - Problem Details for
 * HTTP APIs (https://tools.ietf.org/html/rfc7807)
 */
@ControllerAdvice
public class ExceptionTranslator implements ProblemHandling {

	private static final String MESSAGE_KEY = "message";
	private static final String MESSAGE_VALUE = "error.validation";
	private static final String PATH_KEY = "path";
	private static final String VIOLATIONS_KEY = "violations";

	/**
	 * Post-process the Problem payload to add the message key for the front-end if
	 * needed
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
		ProblemBuilder builder = Problem.builder().withStatus(problem.getStatus()).withTitle(problem.getTitle())
				.with(PATH_KEY, request.getNativeRequest(HttpServletRequest.class).getRequestURI());

		if (problem instanceof ConstraintViolationProblem) {
			builder.with(VIOLATIONS_KEY, ((ConstraintViolationProblem) problem).getViolations()).with(MESSAGE_KEY,
					MESSAGE_VALUE);
		} else {
			builder.withCause(((DefaultProblem) problem).getCause()).withDetail(problem.getDetail())
					.withInstance(problem.getInstance());
			problem.getParameters().forEach(builder::with);
			if (!problem.getParameters().containsKey(MESSAGE_KEY) && problem.getStatus() != null) {
				builder.with(MESSAGE_KEY, "error.http." + problem.getStatus().getStatusCode());
			}
		}
		ThrowableProblem throwableProblem = builder.build();
		throwableProblem.setStackTrace(new StackTraceElement[0]);
		return new ResponseEntity<>(throwableProblem, entity.getHeaders(), entity.getStatusCode());
	}
}
