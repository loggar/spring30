package sample.spring3._01_dao;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import sample.spring3._01_dao.User;
import sample.spring3._01_dao.UserDaoInterface_02;

/**
 * with Spring JdbcTemplate
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/sample/spring3/_01_dao/test-UserDao_08.xml")
public class _05_UserDaoTest_08 {
	@Autowired private UserDaoInterface_02 userDao;

	User user1;

	@Before
	public void setUserDao() {
		this.user1 = new User("loggar", "ChangHee Lee", "thisispassword");
	}

	@Test
	public void userDao_08_test() {
		userDao.deleteAll();
		assertThat(userDao.getCount(), is(0));

		userDao.add(user1);
		assertThat(userDao.getCount(), is(1));

		User user2 = userDao.get(user1.getId());
		assertThat(user1.equals(user2), is(true));
	}
}
