package sample.spring3._06_factorybean;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import sample.spring3._03_transaction.Level;
import sample.spring3._03_transaction.User;
import sample.spring3._03_transaction.UserDao;
import sample.spring3._05_proxy.UserService;

/**
 * ApplicationContext 로부터 TxProxyFactoryBean 이 생성하는 UserService interface(TransactionHandler 적용된 UserServiceImpl Object) 를 DI
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/sample/spring3/_06_factorybean/test-TxProxyFactoryBean.xml")
public class _03_TxProxyFactoryBeanTest_02 {
	@Autowired ApplicationContext context;

	@Autowired private UserDao userDao;
	@Autowired private UserService userService;

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

}
