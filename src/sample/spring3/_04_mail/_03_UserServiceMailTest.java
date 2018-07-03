package sample.spring3._04_mail;

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

import sample.spring3._04_mail.Level;
import sample.spring3._04_mail.User;
import sample.spring3._04_mail.UserServiceMail_03;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "test-dummy.xml")
public class _03_UserServiceMailTest {
	@Autowired private UserServiceMail_03 userService;

	List<User> users;

	@Before
	public void setup() {
		users = Arrays.asList(
				new User("loggar", "ChangHee Lee", "pw1", Level.BASIC, 49, 0, "email1@somewhere.com"),
				new User("loggar2", "ChangHee Lee2", "pw2", Level.BASIC, 50, 0, "email2@somewhere.com"),
				new User("loggar3", "ChangHee Lee3", "pw3", Level.SILVER, 60, 29, "email3@somewhere.com"),
				new User("loggar4", "ChangHee Lee4", "pw4", Level.SILVER, 60, 30, "email4@somewhere.com"),
				new User("loggar5", "ChangHee Lee5", "pw5", Level.GOLD, 100, 100, "email5@somewhere.com")
				);
	}

	@Test
	public void mailSend() {
		for (User user : users) {
			userService.sendMailForLevelUpgradeInform(user);
		}

		assertThat(1, is(1));
	}
}
