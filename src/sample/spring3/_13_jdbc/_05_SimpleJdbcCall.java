package sample.spring3._13_jdbc;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "test-simpledriverdatasource.xml")
public class _05_SimpleJdbcCall {
	@Autowired SimpleDao dao;
	@Autowired DataSource dataSource;

	@Test
	public void simpleJdbcCall_test() {
		dao.deleteAll();
		Member member = new Member(1, "Spring", 3.5);
		SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("member");
		jdbcInsert.execute(new BeanPropertySqlParameterSource(member));

		/*
		 * SimpleJdbcCall 의 초기화
		 */
		SimpleJdbcCall jdbcCall = new SimpleJdbcCall(dataSource).withFunctionName("find_name");
		// jdbcInsert.withProcedureName(procedureName);
		// jdbcInsert.returningResultSet(String parametername, RarameterizedRowMapper rowMapper); // 프로시저가 ResultSet 을 반환하는 경우 이를 RowMapper 를 이용해 매핑해준다. 하나 이상의 ResultSet 이 반환되는 경우라면 순차적으로 returningResultSet() 을 수행하면 된다.

		/*
		 * <T>T executeObject(Class<T> returnType, [SQL parameter]) 저장 프로시저를 호출시.
		 * Map<String,Object> execute([SQL parameter]) 하나 이상의 출력 값을 가진 저장 프로시저 호출시
		 */
		String name = jdbcCall.executeFunction(String.class, member.getId());
		assertThat(name, is(member.getName()));
	}

}
