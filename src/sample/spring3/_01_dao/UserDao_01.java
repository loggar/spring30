package sample.spring3._01_dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * hard code
 * 
 */
public class UserDao_01 implements UserDaoInterface_01 {
	@Override
	public int delete(User user) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/springbook", "springbook", "springbookpw");

		PreparedStatement ps = connection.prepareStatement("delete from users where id = ?");
		ps.setString(1, user.getId());

		int resultUpdate = ps.executeUpdate();

		ps.close();
		connection.close();

		return resultUpdate;
	}

	@Override
	public int add(User user) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/springbook", "springbook", "springbookpw");

		PreparedStatement ps = connection.prepareStatement("insert into users(id, name, password) values(?, ?, ?)");
		ps.setString(1, user.getId());
		ps.setString(2, user.getName());
		ps.setString(3, user.getPassword());

		int resultUpdate = ps.executeUpdate();

		ps.close();
		connection.close();

		return resultUpdate;
	}

	@Override
	public User get(String id) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/springbook", "springbook", "springbookpw");

		PreparedStatement ps = connection.prepareStatement("select * from users where id = ?");
		ps.setString(1, id);

		ResultSet rs = ps.executeQuery();
		rs.next();

		User user = new User(rs.getString("id"), rs.getString("name"), rs.getString("password"));

		rs.close();
		ps.close();
		connection.close();

		return user;
	}

	@Override
	public int deleteAll() throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/springbook", "springbook", "springbookpw");

		PreparedStatement ps = connection.prepareStatement("delete from users");
		int resultUpdate = ps.executeUpdate();

		ps.close();
		connection.close();

		return resultUpdate;
	}

	@Override
	public int getCount() throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/springbook", "springbook", "springbookpw");

		PreparedStatement ps = connection.prepareStatement("select count(*) from users");
		ResultSet rs = ps.executeQuery();
		rs.next();
		int count = rs.getInt(1);

		rs.close();
		ps.close();
		connection.close();

		return count;
	}
}
