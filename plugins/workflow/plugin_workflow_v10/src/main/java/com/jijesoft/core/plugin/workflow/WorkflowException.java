package com.jijesoft.core.plugin.workflow;

public class WorkflowException extends RuntimeException {

	private static final long serialVersionUID = 8406910709779027196L;

	public WorkflowException() {
		super();
	}

	public WorkflowException(String message, Throwable cause) {
		super(message, cause);
	}

	public WorkflowException(String message) {
		super(message);
	}

	public WorkflowException(Throwable cause) {
		super(cause);
	}

}
