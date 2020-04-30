package sample.spring3._10_jaxb;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static sample.spring3._10_jaxb.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static sample.spring3._10_jaxb.UserServiceImpl.MIN_RECCOMEND_FOR_GOLD;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import sample.spring3._08_aop_tx.Level;
import sample.spring3._08_aop_tx.User;

/**
 * resource 의 종류에 관계없이 읽기위한 SqlReader, 어떠한 object 형태로 resource 를 세팅 해둘지를 결정하는 SqlRegistry.
 * 위 두 인터페이스를 도입하여 SqlService 를 구현하였다.
 * 이를 통해 SqlService 에서 XML, JAXB, HashMap 등의 의존도를 제거했다.
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "test-resourceInterface.xml")
public class _03_ResourceInterfaceTest {
	@Autowired UserService userService;
	@Autowired ApplicationContext context;

	List<User> users;

	@Before
	public void setup() {
		users = Arrays.asList(
				new User("loggar", "ChangHee Lee", "pw1", "abc1@abc.com", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER - 1, 0),
				new User("loggar2", "ChangHee Lee2", "pw2", "abc2@abc.com", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0),
				new User("loggar3", "ChangHee Lee3", "pw3", "abc3@abc.com", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD - 1),
				new User("loggar4", "ChangHee Lee4", "pw4", "abc4@abc.com", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD),
				new User("loggar5", "ChangHee Lee5", "pw5", "abc5@abc.com", Level.GOLD, 100, 100)
				);
	}

	@Test
	public void upgradeLevels() {
		userService.deleteAll();
		for (User user : users) {
			try {
				userService.add(user);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		List<User> userList = userService.getAll();
		assertThat(userList.size(), is(users.size()));

		try {
			userService.upgradeLevels();
		} catch (Exception e) {
			e.printStackTrace();
		}

		assertThat(userService.get(users.get(0).getId()).getLevel(), is(Level.BASIC));
		assertThat(userService.get(users.get(1).getId()).getLevel(), is(Level.SILVER));
		assertThat(userService.get(users.get(2).getId()).getLevel(), is(Level.SILVER));
		assertThat(userService.get(users.get(3).getId()).getLevel(), is(Level.GOLD));
		assertThat(userService.get(users.get(4).getId()).getLevel(), is(Level.GOLD));

		userService.deleteAll();
		userList = userService.getAll();
		assertThat(userList.size(), is(0));
	}
}
