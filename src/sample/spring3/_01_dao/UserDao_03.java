package sample.spring3._01_dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * class 로 분리 - SimpleConnectionMaker Class 에 직접의존적이다.
 * 
 */
public class UserDao_03 implements UserDaoInterface_01 {
	private ConnectionMakerSimple simpleConnectionMaker;

	public UserDao_03() {
		simpleConnectionMaker = new ConnectionMakerSimple();
	}

	@Override
	public int delete(User user) throws ClassNotFoundException, SQLException {
		Connection connection = simpleConnectionMaker.makeNewConnection();

		PreparedStatement ps = connection.prepareStatement("delete from users where id = ?");
		ps.setString(1, user.getId());

		int resultUpdate = ps.executeUpdate();

		ps.close();
		connection.close();

		return resultUpdate;
	}

	@Override
	public int add(User user) throws ClassNotFoundException, SQLException {
		Connection connection = simpleConnectionMaker.makeNewConnection();

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
		Connection connection = simpleConnectionMaker.makeNewConnection();

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
		Connection connection = simpleConnectionMaker.makeNewConnection();

		PreparedStatement ps = connection.prepareStatement("delete from users");
		int resultUpdate = ps.executeUpdate();

		ps.close();
		connection.close();

		return resultUpdate;
	}

	@Override
	public int getCount() throws SQLException, ClassNotFoundException {
		Connection connection = simpleConnectionMaker.makeNewConnection();

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
