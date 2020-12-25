package sample.spring3._11_oxm;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import sample.spring3._10_jaxb.SqlNotFoundException;

/**
 * SqlRegistry 의 기능구현에 추가로 SQL map 을 수정할 수있는 기능을 더함.
 * UpdatableSqlRegistry 는 SqlRegistr 를 [interface-상속] 한 Interface 이다.
 * 
 * HashMap 으로는 multi-thread 환경에서의 동시적인 조회/수정/삭제 에 대해 예상치 못한 결과가 발생할 수 있다.
 * 그래서 동기화된 hash data 조작에 최적화되도록 만들어진 ConcurrentHashMap 을 사용하겠다.
 * 
 */
public class ConcurrentHashMapSqlRegistry implements UpdatableSqlRegistry {
	private Map<String, String> sqlMap = new ConcurrentHashMap<String, String>();

	public String findSql(String key) throws SqlNotFoundException {
		String sql = sqlMap.get(key);
		if (sql == null) throw new SqlNotFoundException(key + "를 이용해서 SQL을 찾을 수 없습니다");
		return sql;
	}

	public void registerSql(String key, String sql) {
		sqlMap.put(key, sql);
	}

	public void updateSql(String key, String sql) throws SqlUpdateFailureException {
		if (sqlMap.get(key) == null) {
			throw new SqlUpdateFailureException(key + "에 해당하는 SQL을 찾을 수 없습니다");
		}

		sqlMap.put(key, sql);
	}

	public void updateSql(Map<String, String> sqlmap) throws SqlUpdateFailureException {
		for (Map.Entry<String, String> entry : sqlmap.entrySet()) {
			updateSql(entry.getKey(), entry.getValue());
		}
	}
}
