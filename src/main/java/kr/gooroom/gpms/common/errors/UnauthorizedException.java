package kr.gooroom.gpms.common.errors;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class UnauthorizedException extends AbstractThrowableProblem {

	private static final long serialVersionUID = 1L;

	public UnauthorizedException() {
		this("Unauthorized");
	}

	public UnauthorizedException(String title) {
		super(null, title, Status.UNAUTHORIZED);
	}
}
