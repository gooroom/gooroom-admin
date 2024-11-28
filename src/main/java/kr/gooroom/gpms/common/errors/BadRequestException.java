package kr.gooroom.gpms.common.errors;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.io.Serial;

public class BadRequestException extends AbstractThrowableProblem {

	@Serial
	private static final long serialVersionUID = 1977734174422524187L;

	public BadRequestException() {
		this("Bad Request");
	}

	public BadRequestException(String title) {
		super(null, title, Status.BAD_REQUEST);
	}
}
