package sample.spring3._05_proxy;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import sample.spring3._03_transaction.Level;
import sample.spring3._03_transaction.User;
import sample.spring3._05_proxy.MockUserDao;
import sample.spring3._05_proxy.UserService;
import sample.spring3._05_proxy.UserServiceImpl;

/**
 * Mock Object 를 이용한 고립된 단위테스트
 * 또한 트랜잭션 로직을 분리한 UserServiceImpl 의 비지니스 로직의 고립된 단위테스트.
 * 
 */
public class UserServiceImplTest_02_MockObject {
	List<User> users;

	@Before
	public void setup() {
		users = Arrays.asList(
				new User("loggar", "ChangHee Lee", "pw1", Level.BASIC, UserService.MIN_LOGCOUNT_FOR_SILVER - 1, 0),
				new User("loggar2", "ChangHee Lee2", "pw2", Level.BASIC, UserService.MIN_LOGCOUNT_FOR_SILVER, 0),
				new User("loggar3", "ChangHee Lee3", "pw3", Level.SILVER, 60, UserService.MIN_RECCOMENDCOUNT_FOR_GOLD - 1),
				new User("loggar4", "ChangHee Lee4", "pw4", Level.SILVER, 60, UserService.MIN_RECCOMENDCOUNT_FOR_GOLD),
				new User("loggar5", "ChangHee Lee5", "pw5", Level.GOLD, 100, 100)
				);
	}

	@Test
	public void upgradeLevels() {
		/*
		 * Mock object 생성 후 DI
		 */
		MockUserDao userDao = new MockUserDao();
		UserServiceImpl userService = new UserServiceImpl();
		userService.setUserDao(userDao);

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

		assertThat(userDao.getLevelUpdateUsers().size(), is(2));

		assertThat(userDao.get(users.get(0).getId()).getLevel(), is(Level.BASIC));
		assertThat(userDao.get(users.get(1).getId()).getLevel(), is(Level.SILVER));
		assertThat(userDao.get(users.get(2).getId()).getLevel(), is(Level.SILVER));
		assertThat(userDao.get(users.get(3).getId()).getLevel(), is(Level.GOLD));
		assertThat(userDao.get(users.get(4).getId()).getLevel(), is(Level.GOLD));
	}

	@Test
	public void userService_add() throws Exception {
		MockUserDao userDao = new MockUserDao();
		UserServiceImpl userService = new UserServiceImpl();
		userService.setUserDao(userDao);

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
