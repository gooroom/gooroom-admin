package kr.gooroom.gpms.common.errors;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class BadRequestException extends AbstractThrowableProblem {

	private static final long serialVersionUID = 1L;

	public BadRequestException() {
		this("Bad Request");
	}

	public BadRequestException(String title) {
		super(null, title, Status.BAD_REQUEST);
	}
}
