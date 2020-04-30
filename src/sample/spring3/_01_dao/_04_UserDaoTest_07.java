package sample.spring3._01_dao;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * with Spring DI from applicationContext by "@Autowired"
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/sample/spring3/_01_dao/test-UserDao_07.xml")
public class _04_UserDaoTest_07 {
	@Autowired private UserDaoInterface_01 userDao;

	User user1;

	@Before
	public void setUserDao() {
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

		// JUint is() 는 equals() 검사이므로 위와 같은결과.
		assertThat(user1, is(user2));

		// user1, user2 는 같은 instance 가 아니다.
		assertThat(user1, not(sameInstance(user2)));
	}
}
