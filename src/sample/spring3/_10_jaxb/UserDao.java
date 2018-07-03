package sample.spring3._10_jaxb;

import java.util.List;

import sample.spring3._08_aop_tx.User;

public interface UserDao {

	void add(User user);

	User get(String id);

	List<User> getAll();

	void deleteAll();

	int getCount();

	void update(User user);

}
