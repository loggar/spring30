package sample.spring3._03_transaction;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/sample/spring3/_03_transaction/test-UserService_01.xml")
public class _01_UserServiceTest {
	@Autowired private UserDao userDao;
	@Autowired private UserService_01 userService;

	List<User> users;

	@Before
	public void setup() {
		users = Arrays.asList(
				new User("loggar", "ChangHee Lee", "pw1", Level.BASIC, UserService_01.MIN_LOGCOUNT_FOR_SILVER - 1, 0),
				new User("loggar2", "ChangHee Lee2", "pw2", Level.BASIC, UserService_01.MIN_LOGCOUNT_FOR_SILVER, 0),
				new User("loggar3", "ChangHee Lee3", "pw3", Level.SILVER, 60, UserService_01.MIN_RECCOMENDCOUNT_FOR_GOLD - 1),
				new User("loggar4", "ChangHee Lee4", "pw4", Level.SILVER, 60, UserService_01.MIN_RECCOMENDCOUNT_FOR_GOLD),
				new User("loggar5", "ChangHee Lee5", "pw5", Level.GOLD, 100, 100)
				);
	}

	@Test
	public void upgradeLevels() {
		userDao.deleteAll();
		for (User user : users) {
			userDao.add(user);
		}

		int countUser = userDao.getCount();
		assertThat(countUser, is(users.size()));

		try {
			userService.upgradeLevels();
		} catch (Exception e) {
			e.printStackTrace();
		}

		assertThat(userDao.get(users.get(0).getId()).getLevel(), is(Level.BASIC));
		assertThat(userDao.get(users.get(1).getId()).getLevel(), is(Level.SILVER));
		assertThat(userDao.get(users.get(2).getId()).getLevel(), is(Level.SILVER));
		assertThat(userDao.get(users.get(3).getId()).getLevel(), is(Level.GOLD));
		assertThat(userDao.get(users.get(4).getId()).getLevel(), is(Level.GOLD));
	}

	@Test
	public void userService_add() throws Exception {
		userDao.deleteAll();

		User userWithLevel = users.get(4);

		User userWithoutLevel = users.get(0);
		userWithoutLevel.setLevel(null);

		userService.add(userWithLevel);
		userService.add(userWithoutLevel);

		assertThat(userDao.get(userWithLevel.getId()).getLevel(), is(userWithLevel.getLevel()));
		assertThat(userDao.get(userWithoutLevel.getId()).getLevel(), is(Level.BASIC));
	}

}
