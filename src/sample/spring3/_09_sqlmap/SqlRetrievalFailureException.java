package sample.spring3._09_sqlmap;

@SuppressWarnings("serial")
public class SqlRetrievalFailureException extends RuntimeException {
	public SqlRetrievalFailureException() {
		super();
	}

	public SqlRetrievalFailureException(String message) {
		super(message);
	}

	public SqlRetrievalFailureException(Throwable cause) {
		super(cause);
	}

	public SqlRetrievalFailureException(String message, Throwable cause) {
		super(message, cause);
	}
}
