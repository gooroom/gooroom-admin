package kr.gooroom.gpms.common.errors;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.io.Serial;

public class NotAcceptableException extends AbstractThrowableProblem {

	@Serial
	private static final long serialVersionUID = -4452209384734069653L;

	public NotAcceptableException() {
		this("Not Acceptable");
	}

	public NotAcceptableException(String title) {
		super(null, title, Status.NOT_ACCEPTABLE);
	}
}
