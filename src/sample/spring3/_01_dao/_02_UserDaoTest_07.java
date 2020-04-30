package sample.spring3._01_dao;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;

/**
 * JUnit "@Before", "@After"..
 * 
 */
public class _02_UserDaoTest_07 {
	// Test 를 수행하는데 필요한 정보나 Object 를 fixture 라 한다. 아래는 공통적으로 사용되는 fixture 를 정의하여 사용하는 예시.
	private UserDaoInterface_01 userDao;
	User user1;

	@Before
	public void setUserDao() {
		ApplicationContext context = new GenericXmlApplicationContext("/sample/spring3/_01_dao/test-UserDao_07.xml");
		this.userDao = context.getBean("userDao", UserDaoInterface_01.class);

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

	@Test(expected = EmptyResultDataAccessException.class)
	public void userDao_07_Exception() throws ClassNotFoundException, SQLException {
		userDao.deleteAll();

		User user = userDao.get("unknown_id_sample_for_expected_exception_junit_test");

		// process 는 아래까지 도달하지 못하고 EmptyResultDataAccessException 발생 예상.
		assertThat(user.getId(), is("unknown_id_sample_for_expected_exception_junit_test"));
	}
}
