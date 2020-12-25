package sample.spring3._06_factorybean;

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
import org.springframework.transaction.PlatformTransactionManager;

import sample.spring3._03_transaction.Level;
import sample.spring3._03_transaction.User;
import sample.spring3._03_transaction.UserDao;
import sample.spring3._05_proxy.UserService;
import sample.spring3._05_proxy.UserServiceImpl;

/**
 * TxProxyFactoryBean 를 생성하여 txProxyFactoryBean.getObject() 를 통해 UserService 를 반환받음.
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/sample/spring3/_06_factorybean/test-TxProxyFactoryBean.xml")
public class _02_TxProxyFactoryBeanTest_01 {
	@Autowired private UserDao userDao;
	@Autowired private UserServiceImpl userServiceImpl;
	@Autowired private PlatformTransactionManager transactionManager;

	List<User> users;

	@Before
	public void setup() {
		users = Arrays.asList(new User("loggar", "ChangHee Lee", "pw1", Level.BASIC, UserService.MIN_LOGCOUNT_FOR_SILVER - 1, 0), new User("loggar2", "ChangHee Lee2", "pw2", Level.BASIC, UserService.MIN_LOGCOUNT_FOR_SILVER, 0),
				new User("loggar3", "ChangHee Lee3", "pw3", Level.SILVER, 60, UserService.MIN_RECCOMENDCOUNT_FOR_GOLD - 1), new User("loggar4", "ChangHee Lee4", "pw4", Level.SILVER, 60, UserService.MIN_RECCOMENDCOUNT_FOR_GOLD),
				new User("loggar5", "ChangHee Lee5", "pw5", Level.GOLD, 100, 100));
	}

	@Test
	public void upgradeLevels() throws Exception {
		String pattern = "upgradeLevels";
		Class<?> serviceInterface = UserService.class;

		TxProxyFactoryBean txProxyFactoryBean = new TxProxyFactoryBean();
		txProxyFactoryBean.setTarget(userServiceImpl);
		txProxyFactoryBean.setTransactionManager(transactionManager);
		txProxyFactoryBean.setPattern(pattern);
		txProxyFactoryBean.setServiceInterface(serviceInterface);

		UserService userService = (UserService) txProxyFactoryBean.getObject();
		assertThat(userService, is(UserService.class));
		UserService txUserService = userService;

		userDao.deleteAll();
		for (User user : users) {
			txUserService.add(user);
		}

		int countUser = userDao.getCount();
		assertThat(countUser, is(users.size()));

		try {
			txUserService.upgradeLevels();
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
