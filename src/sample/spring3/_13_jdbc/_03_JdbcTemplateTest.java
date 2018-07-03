package sample.spring3._13_jdbc;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * JdbcTemplate 의 사용
 * SimpleDao 참고.
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "test-simpledriverdatasource.xml")
public class _03_JdbcTemplateTest {
	@Autowired SimpleDao dao;

	@Test
	public void simeJdbcTemplate_test() {
		dao.deleteAll();

		// update()
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("id", 1);
		m.put("name", "Spring");
		m.put("point", 3.5);
		dao.insert(m);
		dao.insert(new MapSqlParameterSource().addValue("id", 2).addValue("name", "Book").addValue("point", 10.1));
		dao.insert(new Member(3, "Jdbc", 20.5));

		// queryForInt()
		assertThat(dao.rowCount(), is(3));
		assertThat(dao.rowCount(5), is(2));
		assertThat(dao.rowCount(1), is(3));

		// queryForObject(Class)
		assertThat(dao.name(1), is("Spring"));
		assertThat(dao.point(1), is(3.5));

		// queryForObject(RowMapper)
		Member mret = dao.get(1);
		assertThat(mret.id, is(1));
		assertThat(mret.name, is("Spring"));
		assertThat(mret.point, is(3.5));

		// query(RowMapper)
		assertThat(dao.find(1).size(), is(3));
		assertThat(dao.find(5).size(), is(2));
		assertThat(dao.find(100).size(), is(0));

		// queryForMap
		Map<String, Object> mmap = dao.getMap(1);
		assertThat((Integer) mmap.get("id"), is(1));
		assertThat((String) mmap.get("name"), is("Spring"));
		assertThat((Double) mmap.get("point"), is(3.5));

		// batchUpdates()
		@SuppressWarnings("unchecked") 
		Map<String, Object>[] paramMaps = new HashMap[2];
		paramMaps[0] = new HashMap<String, Object>();
		paramMaps[0].put("id", 1);
		paramMaps[0].put("name", "Spring2");
		paramMaps[1] = new HashMap<String, Object>();
		paramMaps[1].put("id", 2);
		paramMaps[1].put("name", "Book2");
		dao.updates(paramMaps);

		assertThat(dao.name(1), is("Spring2"));
		assertThat(dao.name(2), is("Book2"));

		/*
		 * simpleJdbcTemplate.batchUpdate 는 다음처럼 여러 종류의 파라미터구성도 순차적으로 매핑하여 update 수행한다.
		 * SqlParameterSource 에 해당하는 파라미터 구성이기만 하면 된다.
		 */
		dao.simpleJdbcTemplate.batchUpdate("update member set name = :name where id = :id",
				new SqlParameterSource[] {
						new MapSqlParameterSource().addValue("id", 1).addValue("name", "Spring3"),
						new BeanPropertySqlParameterSource(new Member(2, "Book3", 0))
				}
				);

		assertThat(dao.name(1), is("Spring3"));
		assertThat(dao.name(2), is("Book3"));
	}

}
