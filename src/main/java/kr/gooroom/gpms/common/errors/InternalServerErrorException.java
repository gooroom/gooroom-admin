package kr.gooroom.gpms.common.errors;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class InternalServerErrorException extends AbstractThrowableProblem {

	private static final long serialVersionUID = 1L;

	public InternalServerErrorException(String message) {
		super(null, message, Status.INTERNAL_SERVER_ERROR);
	}

}
