package sample.spring3._11_oxm;

import java.util.Map;

import sample.spring3._10_jaxb.SqlRegistry;

/**
 * 인터페이스 상속의 예.
 * UpdatableSqlRegistry 를 구현하는 클래스는 동시에 SqlRegistry 도 모두 구현하도록 설계할 수 있다.
 * 
 */
public interface UpdatableSqlRegistry extends SqlRegistry {
	public void updateSql(String key, String sql) throws SqlUpdateFailureException;

	public void updateSql(Map<String, String> sqlmap) throws SqlUpdateFailureException;
}
