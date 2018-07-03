package sample.spring3._03_transaction;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import sample.spring3._03_transaction.Level;
import sample.spring3._03_transaction.User;
import sample.spring3._03_transaction.UserDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/sample/spring3/_03_transaction/test-UserService_00.xml")
public class UserDaoTest {
	@Autowired private UserDao userDao;

	User user1;
	User user2;

	@Before
	public void setup() {
		this.user1 = new User("loggar", "ChangHee Lee", "thisispassword", Level.valueOf(1), 0, 0);
		this.user2 = new User("loggar2", "ChangHee Lee2", "thisispassword2", Level.valueOf(1), 0, 0);
	}

	@Test
	public void userDao_test() {
		userDao.deleteAll();
		assertThat(userDao.getCount(), is(0));

		userDao.add(user1);
		assertThat(userDao.getCount(), is(1));

		User user2 = userDao.get(user1.getId());
		assertThat(user1.equals(user2), is(true));
	}

	@Test
	public void userDao_update() {
		userDao.deleteAll();

		userDao.add(user1);
		userDao.add(user2);

		user1.setName("ChangHee Lee3");
		user1.setLevel(Level.GOLD);
		user1.setLogin(1000);

		userDao.update(user1);

		User newUser1 = userDao.get(user1.getId());
		User newUser2 = userDao.get(user2.getId());

		assertThat(newUser1, is(not(user2)));
		assertThat(newUser2, is(user2));
	}
}
