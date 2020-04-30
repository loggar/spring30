package sample.spring3._01_dao;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * applicationContext Spring Bean
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/sample/spring3/_01_dao/test-UserDao_07.xml")
public class _03_UserDaoTest_07 {
	@Autowired private ApplicationContext context;

	private UserDaoInterface_01 userDao;
	User user1;

	@Before
	public void setUserDao() {
		this.userDao = this.context.getBean("userDao", UserDaoInterface_01.class);
		this.user1 = new User("loggar", "ChangHee Lee", "thisispassword");
	}

	@Test
	public void userDao_07_test() throws ClassNotFoundException, SQLException {
		userDao.deleteAll();
		assertThat(userDao.getCount(), is(0));

		userDao.add(user1);
		assertThat(userDao.getCount(), is(1));

		User user2 = userDao.get(user1.getId());
		assertThat(user1.equals(user2), is(true));
	}
}
