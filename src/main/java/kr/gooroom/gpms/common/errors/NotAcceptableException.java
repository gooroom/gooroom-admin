package kr.gooroom.gpms.common.errors;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class NotAcceptableException extends AbstractThrowableProblem {

	private static final long serialVersionUID = 1L;

	public NotAcceptableException() {
		this("Not Acceptable");
	}

	public NotAcceptableException(String title) {
		super(null, title, Status.NOT_ACCEPTABLE);
	}
}
