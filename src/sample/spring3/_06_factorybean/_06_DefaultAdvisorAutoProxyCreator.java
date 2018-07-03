package sample.spring3._06_factorybean;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import sample.spring3._03_transaction.Level;
import sample.spring3._03_transaction.User;
import sample.spring3._03_transaction.UserDao;
import sample.spring3._05_proxy.UserService;
import sample.spring3._05_proxy.UserServiceImpl;

/**
 * Spring 빈 후처리기 interface BeanPostProcessor 를 구현한 DefaultAdvisorAutoProxyCreator 의 spring bean 등록
 * DefaultAdvisorAutoProxyCreator 는 Advisor 를 이용한 자동 프록시 생성기이다.
 * DefaultAdvisorAutoProxyCreator 는 등록된 bean 중에서 Advisor 의 구현체를 모두 찾아서 앞으로 생성될 모든 bean 에 대해 pointcut 을 적용하며 해당하는 경우
 * factoryBean 을 통해 porxy object 를 생성하여 target bean 을 대체한다.
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/sample/spring3/_06_factorybean/test-DefaultAdvisorAutoProxyCreator.xml")
public class _06_DefaultAdvisorAutoProxyCreator {
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
		System.out.println("userService.getClass().getSimpleName(): " + userService.getClass().getSimpleName());
		System.out.println("userServiceTest.getClass().getSimpleName(): " + userServiceTest.getClass().getSimpleName());
		System.out.println("testUserServiceImpl.getClass().getSimpleName(): " + testUserServiceImpl.getClass().getSimpleName());

		assertThat(userService, is(java.lang.reflect.Proxy.class));
		assertThat(userServiceTest, is(UserServiceImplTest.class));
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

	/**
	 * proxy 적용되지 않을 테스트용 타겟 클래스 (*ServiceImpl 이 아니므로)
	 * context 주입을 통해 bean 생성 한 후 UserServiceImpl 자동 생성때와 Advisor 적용 여부를 비교해 본다.
	 * 
	 */
	static class UserServiceImplTest extends UserServiceImpl {
		protected void upgradeLevel(User user) {
			if (user.getId().equals("loggar4")) {
				System.out.println("UserServiceImplTest.upgradeLevels() throw TestUserServiceException");
				throw new TestUserServiceException();
			}
			super.upgradeLevel(user);
		}
	}

	/**
	 * proxy 적용될 테스트용 타겟 클래스 (*ServiceImpl)
	 * context 주입을 통해 bean 생성 한 후 UserServiceImpl 자동 생성때와 Advisor 적용 여부를 비교해 본다.
	 * 
	 */
	static class TestUserServiceImpl extends UserServiceImpl {
		protected void upgradeLevel(User user) {
			if (user.getId().equals("loggar4")) {
				System.out.println("TestUserServiceImpl.upgradeLevels() throw TestUserServiceException");
				throw new TestUserServiceException();
			}
			super.upgradeLevel(user);
		}
	}

	@SuppressWarnings("serial")
	static class TestUserServiceException extends RuntimeException {
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
