package sample.spring3._01_dao;

import java.sql.SQLException;

public interface UserDaoInterface_01 {
	public int delete(User user) throws ClassNotFoundException, SQLException;

	public int add(User user) throws ClassNotFoundException, SQLException;

	public User get(String id) throws ClassNotFoundException, SQLException;

	public int deleteAll() throws ClassNotFoundException, SQLException;

	public int getCount() throws ClassNotFoundException, SQLException;
}
