package sample.spring3._13_jdbc;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "test-simpledriverdatasource.xml")
public class _04_SimpleJdbcInsertTest {
	@Autowired SimpleDao dao;
	@Autowired DataSource dataSource;

	@Test
	public void simpleJdbcInsert_test() {
		/*
		 * SimpleJdbcInsert 의 초기화
		 */
		SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("member");
		// jdbcInsert.withSchemaName(schemaName);
		// jdbcInsert.withCatalogName(catalogName);
		// jdbcInsert.usingColumns(columnName1, columnName2, ...);
		// jdbcInsert.usingGeneratedKeyColumns("pk-id"); // 자동 생성되는 키 컬럼 지정. INSERT 문장 구성시 제외되며 INSERT 성공시 반환값으로 활용된다.
		// jdbcInsert.withoutTableColumnMetaDataAccess();

		dao.deleteAll();

		/*
		 * SimpleJdbcInsert 초기화 시 usingGeneratedKeyColumns 이 지정된 경우에는
		 * SimpleJdbcInsert.execute() 대신 SimpleJdbcInsert.executeAndReturnKey() 를 실행하면 자동생성 된 키 값을 int 의 형태로 받을 수 있다.
		 * 
		 * 하나이상의 usingGeneratedKeyColumns 이 지정된, 자동생성되는 키가 하나이상인 테이블의 경우에는
		 * SimpleJdbcInsert.executeAndReturnKeyHoler() 를 수행하면 List<Map<String,Object>> 의 형태로 돌려주는 getKeyList() 메소드가 제공된다.
		 */
		MapSqlParameterSource paramSource = new MapSqlParameterSource()
				.addValue("id", 1)
				.addValue("name", "Spring")
				.addValue("point", 10.5);
		jdbcInsert.execute(paramSource);

		Member m = new Member(2, "Jdbc", 3.3);
		jdbcInsert.execute(new BeanPropertySqlParameterSource(m));

		SimpleJdbcTemplate sjt = new SimpleJdbcTemplate(dataSource);
		List<Map<String, Object>> list = sjt.queryForList("select * from member order by id");

		assertThat(list.size(), is(2));

		assertThat((Integer) list.get(0).get("id"), is(1));
		assertThat((String) list.get(0).get("name"), is("Spring"));
		assertThat((Double) list.get(0).get("point"), is(10.5));

		assertThat((Integer) list.get(1).get("id"), is(2));
		assertThat((String) list.get(1).get("name"), is("Jdbc"));
		assertThat((Double) list.get(1).get("point"), is(3.3));
	}

}
