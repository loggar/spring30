package sample.spring3._03_transaction;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class UserDaoMysqlJdbc implements UserDao {
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
			user.setLevel(Level.valueOf(rs.getInt("level")));
			user.setLogin(rs.getInt("login"));
			user.setRecommend(rs.getInt("recommend"));
			return user;
		}
	};

	@Override
	public int delete(User user) {
		String sql = "delete from users where id = ?";
		Object[] args = new Object[] { user.getId() };

		return this.jdbcTemplate.update(sql, args);
	}

	@Override
	public int add(User user) {
		String sql = "insert into users(id, name, password, level, login, recommend) values(?, ?, ?, ?, ?, ?)";
		Object[] args = new Object[] { user.getId(), user.getName(), user.getPassword(), user.getLevel().intValue(), user.getLogin(), user.getRecommend() };

		return this.jdbcTemplate.update(sql, args);
	}

	@Override
	public User get(String id) {
		String sql = "select * from users where id = ?";
		Object[] args = new Object[] { id };

		return this.jdbcTemplate.queryForObject(sql, args, this.userMapper);
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

	@Override
	public int update(User user) {
		String sql = "update users set name=?, password=?, level=?, login=?, recommend=? where id=?";
		Object[] args = new Object[] { user.getName(), user.getPassword(), user.getLevel().intValue(), user.getLogin(), user.getRecommend(), user.getId() };

		return this.jdbcTemplate.update(sql, args);
	}

}
