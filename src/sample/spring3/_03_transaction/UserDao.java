package sample.spring3._03_transaction;

import java.util.List;

public interface UserDao {
	public int delete(User user);

	public int add(User user);

	public User get(String id);

	public int deleteAll();

	public int getCount();

	public List<User> getAll();

	public int update(User user);
}
