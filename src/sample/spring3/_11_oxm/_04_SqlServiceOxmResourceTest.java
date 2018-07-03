package sample.spring3._11_oxm;

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
import sample.spring3._10_jaxb.UserService;

/**
 * Resource 추상화
 * 다양한 위치의 Resource 를 가져오기위해 Resource 를 추상화한다. (test-sqlservice-oxm-resource.xml)
 * 
 * Resource 는 다음의 DI (String value) 로 바로 적절한 Resource 구현체의 생성자가 호출되어 만들어진다.
 * 1. classpath:com/loggar/oxm/abc.xml
 * 2. file:c:/abc/abc.xml
 * 3. http://www.loggar.com/abc.xml
 * 4. ftp://111.222.333.444/abc.xml
 * 5. 접두어없음 abc.xml : ResourceLoader 구현에 따라 위치결정. ServletResourceLoader 라면 ServletContext Root 기준으로 해석.
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "test-sqlservice-oxm-resource.xml")
public class _04_SqlServiceOxmResourceTest {
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
