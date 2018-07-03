package sample.spring3._05_proxy;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Proxy;
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
import sample.spring3._05_proxy.TransactionHandler;
import sample.spring3._05_proxy.UserService;

/**
 * TransactionHandler Dynamic Proxy Class 를 이용한 광범위한 트랜잭션 구현부 테스트
 * 이때 TransactionHandler transactionHandler 를 스프링 DI 할 수 없다. (TargetObject 등의 정보가 dynamic 하므로 사전에 알 수 없다)
 * 오로지 Proxy.newProxyInstance() 라는 static factory method 를 통해서만 생성된다.
 * 다음장에서 이를 해결하기 위해 스프링의 FactoryBean 인터페이스 도입한다.
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/sample/spring3/_05_proxy/test-UserService_02.xml")
public class UserServiceImplTest_04_DynamicProxy {
	@Autowired private UserDao userDao;
	@Autowired private UserService userService;
	@Autowired private PlatformTransactionManager transactionManager;

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
	public void upgradeLevels() throws Exception {
		TransactionHandler transactionHandler = new TransactionHandler();
		transactionHandler.setTarget(userService);
		transactionHandler.setTransactionManager(transactionManager);
		transactionHandler.setPattern("upgradeLevels");

		UserService txUserService = (UserService) Proxy.newProxyInstance(
				getClass().getClassLoader(),
				new Class[] { UserService.class },
				transactionHandler);

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
