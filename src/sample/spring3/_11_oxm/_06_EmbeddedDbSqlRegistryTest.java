package sample.spring3._11_oxm;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import sample.spring3._10_jaxb.SqlNotFoundException;

/**
 * 내장형 DB (이 예제에서는 HSQL) 를 이용하는 UPDATABLE-SQLMAP TEST
 * 
 */
public class _06_EmbeddedDbSqlRegistryTest {
	UpdatableSqlRegistry sqlRegistry;
	EmbeddedDatabase db;

	@Before
	public void setUp() {
		db = new EmbeddedDatabaseBuilder()
				.setType(HSQL)
				.addScript("classpath:sample/spring3/_11_oxm/sqlRegistrySchema.sql")
				.build();

		EmbeddedDbSqlRegistry embeddedDbSqlRegistry = new EmbeddedDbSqlRegistry();
		embeddedDbSqlRegistry.setDataSource(db);

		sqlRegistry = embeddedDbSqlRegistry;
		sqlRegistry.registerSql("KEY1", "SQL1");
		sqlRegistry.registerSql("KEY2", "SQL2");
		sqlRegistry.registerSql("KEY3", "SQL3");
	}

	@After
	public void tearDown() {
		db.shutdown();
	}

	@Test
	public void find() {
		checkFind("SQL1", "SQL2", "SQL3");
	}

	@Test(expected = SqlNotFoundException.class)
	public void unknownKey() {
		sqlRegistry.findSql("SQL9999!@#$");
	}

	protected void checkFind(String expected1, String expected2, String expected3) {
		assertThat(sqlRegistry.findSql("KEY1"), is(expected1));
		assertThat(sqlRegistry.findSql("KEY2"), is(expected2));
		assertThat(sqlRegistry.findSql("KEY3"), is(expected3));
	}

	@Test
	public void updateSingle() {
		sqlRegistry.updateSql("KEY2", "Modified2");

		checkFind("SQL1", "Modified2", "SQL3");
	}

	@Test
	public void updateMulti() {
		Map<String, String> sqlmap = new HashMap<String, String>();
		sqlmap.put("KEY1", "Modified1");
		sqlmap.put("KEY3", "Modified3");

		sqlRegistry.updateSql(sqlmap);

		checkFind("Modified1", "SQL2", "Modified3");
	}

	@Test(expected = SqlUpdateFailureException.class)
	public void updateWithNotExistingKey() {
		sqlRegistry.updateSql("SQL9999!@#$", "Modified2");
	}

	@Test
	public void transactionalUpdate() {
		checkFind("SQL1", "SQL2", "SQL3");

		Map<String, String> sqlmap = new HashMap<String, String>();
		sqlmap.put("KEY1", "Modified1");
		sqlmap.put("KEY9999!@#$", "Modified9999");

		try {
			sqlRegistry.updateSql(sqlmap);
			fail();
		} catch (SqlUpdateFailureException e) {
		}

		checkFind("SQL1", "SQL2", "SQL3");
	}

}
