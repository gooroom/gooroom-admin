package kr.gooroom.gpms.common.errors;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.io.Serial;

public class InternalServerErrorException extends AbstractThrowableProblem {

	@Serial
	private static final long serialVersionUID = 1L;

	public InternalServerErrorException(String message) {
		super(null, message, Status.INTERNAL_SERVER_ERROR);
	}

}
