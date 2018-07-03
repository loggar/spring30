package sample.spring3._13_jdbc;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

public class SimpleDao {
	SimpleJdbcTemplate simpleJdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		this.simpleJdbcTemplate = new SimpleJdbcTemplate(dataSource);
	}

	public void updates(Map<String, Object>[] maps) {
		this.simpleJdbcTemplate.batchUpdate("update member set name = :name where id = :id", maps);
	}

	public Map<String, Object> getMap(int id) {
		return this.simpleJdbcTemplate.queryForMap("select * from member where id = ?", id);
	}

	public List<Member> find(double point) {
		return this.simpleJdbcTemplate.query("select * from member where point > ?", new BeanPropertyRowMapper<Member>(Member.class), point);
	}

	public Member get(int id) {
		return this.simpleJdbcTemplate.queryForObject("select * from member where id = ?", new BeanPropertyRowMapper<Member>(Member.class), id);
	}

	public String name(int id) {
		return this.simpleJdbcTemplate.queryForObject("select name from member where id = ?", String.class, id);
	}

	public double point(int id) {
		return this.simpleJdbcTemplate.queryForObject("select point from member where id = ?", Double.class, id);
	}

	public int rowCount() {
		return this.simpleJdbcTemplate.queryForInt("select count(*) from member");
	}

	public int rowCount(double min) {
		return this.simpleJdbcTemplate.queryForInt("select count(*) from member where point > :min", new MapSqlParameterSource("min", min));
	}

	public void deleteAll() {
		this.simpleJdbcTemplate.update("delete from member");
	}

	@SuppressWarnings("unchecked")
	public void insert(@SuppressWarnings("rawtypes") Map map) {
		this.simpleJdbcTemplate.update("INSERT INTO MEMBER(ID, NAME, POINT) VALUES(:id, :name, :point)", map);
	}

	public void insert(Member m) {
		this.simpleJdbcTemplate.update("INSERT INTO MEMBER(ID, NAME, POINT) VALUES(:id, :name, :point)", new BeanPropertySqlParameterSource(m));
	}

	public void insert(SqlParameterSource m) {
		this.simpleJdbcTemplate.update("INSERT INTO MEMBER(ID, NAME, POINT) VALUES(:id, :name, :point)", m);
	}
}
