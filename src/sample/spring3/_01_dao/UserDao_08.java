package sample.spring3._01_dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 * Spring JdbcTemplate 을 이용한 Dao ex) UserDaoMysqlJdbc : UserDao의 인터페이스를 구현, Mysql DB, Spring Jdbc 이용.
 * 
 */
public class UserDao_08 implements UserDaoInterface_02 {
	private JdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	private RowMapper<User> userMapper = new RowMapper<User>() {
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User();
			user.setId(rs.getString("id"));
			user.setName(rs.getString("name"));
			user.setPassword(rs.getString("password"));
			return user;
		}
	};

	@Override
	public int delete(User user) {
		String sql = "delete from users where id = ?";
		Object[] params = new Object[] { user.getId() };

		return this.jdbcTemplate.update(sql, params);
	}

	@Override
	public int add(User user) {
		String sql = "insert into users(id, name, password) values(?, ?, ?)";
		Object[] params = new Object[] { user.getId(), user.getName(), user.getPassword() };

		return this.jdbcTemplate.update(sql, params);
	}

	@Override
	public User get(String id) {
		String sql = "select * from users where id = ?";
		Object[] params = new Object[] { id };

		return this.jdbcTemplate.queryForObject(sql, params, this.userMapper);
	}

	@Override
	public int deleteAll() {
		String sql = "delete from users";

		return this.jdbcTemplate.update(sql);
	}

	@Override
	public int getCount() {
		String sql = "select count(*) from users";

		return this.jdbcTemplate.queryForInt(sql);
	}

	@Override
	public List<User> getAll() {
		String sql = "select * from users order by id";

		return this.jdbcTemplate.query(sql, this.userMapper);
	}

}
