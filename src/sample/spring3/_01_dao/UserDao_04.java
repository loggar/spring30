package sample.spring3._01_dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * interface 로 분리 - ConnectionMaker interface 의 구현체에 의존.
 * 
 */
public class UserDao_04 implements UserDaoInterface_01 {
	private ConnectionMaker connectionMaker;

	public UserDao_04() {
		connectionMaker = new ConnectionMakerMysql();
	}

	@Override
	public int delete(User user) throws ClassNotFoundException, SQLException {
		Connection connection = connectionMaker.makeConnection();

		PreparedStatement ps = connection.prepareStatement("delete from users where id = ?");
		ps.setString(1, user.getId());

		int resultUpdate = ps.executeUpdate();

		ps.close();
		connection.close();

		return resultUpdate;
	}

	@Override
	public int add(User user) throws ClassNotFoundException, SQLException {
		Connection connection = connectionMaker.makeConnection();

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
		Connection connection = connectionMaker.makeConnection();

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
		Connection connection = connectionMaker.makeConnection();

		PreparedStatement ps = connection.prepareStatement("delete from users");
		int resultUpdate = ps.executeUpdate();

		ps.close();
		connection.close();

		return resultUpdate;
	}

	@Override
	public int getCount() throws SQLException, ClassNotFoundException {
		Connection connection = connectionMaker.makeConnection();

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
