package sample.spring3._01_dao;

import java.util.List;

public interface UserDaoInterface_02 {
	public int delete(User user);

	public int add(User user);

	public User get(String id);

	public int deleteAll();

	public int getCount();

	public List<User> getAll();
}
