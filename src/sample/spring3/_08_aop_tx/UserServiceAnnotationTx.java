package sample.spring3._08_aop_tx;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface UserServiceAnnotationTx {
	void add(User user);

	void deleteAll();

	void update(User user);

	@Transactional(readOnly = true)
	User get(String id);

	@Transactional(readOnly = true)
	List<User> getAll();

	void upgradeLevels();
}
