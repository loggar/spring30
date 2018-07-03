package sample.spring3._10_jaxb;

import java.util.HashMap;
import java.util.Map;

import sample.spring3._09_sqlmap.SqlRetrievalFailureException;

public class SqlRegistryHashMap implements SqlRegistry {
	private Map<String, String> sqlMap = new HashMap<String, String>();

	public String findSql(String key) throws SqlNotFoundException {
		String sql = sqlMap.get(key);
		if (sql == null) throw new SqlRetrievalFailureException(key + "를 이용해서 SQL을 찾을 수 없습니다");
		else return sql;
	}

	public void registerSql(String key, String sql) {
		sqlMap.put(key, sql);
	}

}
