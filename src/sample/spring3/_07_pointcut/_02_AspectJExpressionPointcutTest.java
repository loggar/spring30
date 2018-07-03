package sample.spring3._07_pointcut;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import sample.spring3._03_transaction.Level;
import sample.spring3._03_transaction.User;
import sample.spring3._03_transaction.UserDao;
import sample.spring3._05_proxy.UserService;

/**
 * AspectJExpressionPointcut 의 execution() 지시자를 이용한 pointcut 지정
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/sample/spring3/_07_pointcut/test-AspectJExpressionPointcut.xml")
public class _02_AspectJExpressionPointcutTest {
	@Autowired private UserDao userDao;
	@Autowired private UserService userService;
	@Autowired private UserService userServiceTest;
	@Autowired private UserService testUserServiceImpl;

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
	public void debugSpringBeans() {
		Logger logger = LoggerFactory.getLogger(_02_AspectJExpressionPointcutTest.class);

		logger.debug("userService.getClass().getName(): " + userService.getClass().getName());
		logger.debug("userServiceTest.getClass().getName(): " + userServiceTest.getClass().getName());
		logger.debug("testUserServiceImpl.getClass().getName(): " + testUserServiceImpl.getClass().getName());

		assertThat(userService, is(java.lang.reflect.Proxy.class));
		assertThat(userServiceTest, is(TestUserServiceNotTransaction.class));
		assertThat(testUserServiceImpl, is(java.lang.reflect.Proxy.class));
	}

	@Test
	public void upgradeLevels() {
		userDao.deleteAll();
		for (User user : users) {
			try {
				userService.add(user);
			} catch (Exception e) {
				e.printStackTrace();
			}
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
	public void upgradeLevels_notTransaction() {
		userDao.deleteAll();
		for (User user : users) {
			try {
				userServiceTest.add(user);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		int countUser = userDao.getCount();
		assertThat(countUser, is(users.size()));

		try {
			userServiceTest.upgradeLevels();
			fail("TestUserServiceException expected");
		} catch (Exception e) {
		}

		assertThat(userDao.get(users.get(0).getId()).getLevel(), is(Level.BASIC));
		assertThat(userDao.get(users.get(1).getId()).getLevel(), is(Level.SILVER));
		assertThat(userDao.get(users.get(2).getId()).getLevel(), is(Level.SILVER));
		assertThat(userDao.get(users.get(3).getId()).getLevel(), is(Level.SILVER));
		assertThat(userDao.get(users.get(4).getId()).getLevel(), is(Level.GOLD));
	}

	@Test
	public void upgradeLevels_transaction() {
		userDao.deleteAll();
		for (User user : users) {
			try {
				testUserServiceImpl.add(user);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		int countUser = userDao.getCount();
		assertThat(countUser, is(users.size()));

		try {
			testUserServiceImpl.upgradeLevels();
			fail("TestUserServiceException expected");
		} catch (Exception e) {
		}

		assertThat(userDao.get(users.get(0).getId()).getLevel(), is(Level.BASIC));
		assertThat(userDao.get(users.get(1).getId()).getLevel(), is(Level.BASIC));
		assertThat(userDao.get(users.get(2).getId()).getLevel(), is(Level.SILVER));
		assertThat(userDao.get(users.get(3).getId()).getLevel(), is(Level.SILVER));
		assertThat(userDao.get(users.get(4).getId()).getLevel(), is(Level.GOLD));
	}

}
