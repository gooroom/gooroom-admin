package kr.gooroom.gpms.common.errors;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.io.Serial;

public class UnauthorizedException extends AbstractThrowableProblem {

	@Serial
	private static final long serialVersionUID = 1387170267771437485L;

	public UnauthorizedException() {
		this("Unauthorized");
	}

	public UnauthorizedException(String title) {
		super(null, title, Status.UNAUTHORIZED);
	}
}
