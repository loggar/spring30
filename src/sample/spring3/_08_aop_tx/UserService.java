package sample.spring3._08_aop_tx;

import java.util.List;

public interface UserService {
	void add(User user);

	void deleteAll();

	void update(User user);

	User get(String id);

	List<User> getAll();

	void upgradeLevels();
}
