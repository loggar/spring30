package sample.spring3._09_sqlmap;

public interface SqlService {
	String getSql(String key) throws SqlRetrievalFailureException;
}
