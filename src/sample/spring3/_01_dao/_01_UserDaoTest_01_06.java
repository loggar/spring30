package sample.spring3._01_dao;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class _01_UserDaoTest_01_06 {
	@Test
	public void userDao_01_test() throws ClassNotFoundException, SQLException {
		UserDaoInterface_01 userDao = null;
		userDao = new UserDao_01();

		userDao.deleteAll();
		assertThat(userDao.getCount(), is(0));

		User user1 = new User("loggar", "ChangHee Lee", "thisispassword");
		userDao.add(user1);
		assertThat(userDao.getCount(), is(1));

		User user2 = userDao.get(user1.getId());
		assertThat(user1.equals(user2), is(true));
	}

	@Test
	public void userDao_02_test() throws ClassNotFoundException, SQLException {
		UserDaoInterface_01 userDao = null;
		userDao = new UserDao_02();

		userDao.deleteAll();
		assertThat(userDao.getCount(), is(0));

		User user1 = new User("loggar", "ChangHee Lee", "thisispassword");
		userDao.add(user1);
		assertThat(userDao.getCount(), is(1));

		User user2 = userDao.get(user1.getId());
		assertThat(user1.equals(user2), is(true));
	}

	@Test
	public void userDao_03_test() throws ClassNotFoundException, SQLException {
		UserDaoInterface_01 userDao = null;
		userDao = new UserDao_03();

		userDao.deleteAll();
		assertThat(userDao.getCount(), is(0));

		User user1 = new User("loggar", "ChangHee Lee", "thisispassword");
		userDao.add(user1);
		assertThat(userDao.getCount(), is(1));

		User user2 = userDao.get(user1.getId());
		assertThat(user1.equals(user2), is(true));
	}

	@Test
	public void userDao_04_test() throws ClassNotFoundException, SQLException {
		UserDaoInterface_01 userDao = null;
		userDao = new UserDao_04();

		userDao.deleteAll();
		assertThat(userDao.getCount(), is(0));

		User user1 = new User("loggar", "ChangHee Lee", "thisispassword");
		userDao.add(user1);
		assertThat(userDao.getCount(), is(1));

		User user2 = userDao.get(user1.getId());
		assertThat(user1.equals(user2), is(true));
	}

	@Test
	public void userDao_05_test() throws ClassNotFoundException, SQLException {
		UserDaoInterface_01 userDao = null;
		userDao = new DaoFactory_05().userDao();

		userDao.deleteAll();
		assertThat(userDao.getCount(), is(0));

		User user1 = new User("loggar", "ChangHee Lee", "thisispassword");
		userDao.add(user1);
		assertThat(userDao.getCount(), is(1));

		User user2 = userDao.get(user1.getId());
		assertThat(user1.equals(user2), is(true));
	}

	@Test
	public void userDao_06_test() throws ClassNotFoundException, SQLException {
		ApplicationContext applicationContext = new AnnotationConfigApplicationContext(DaoFactory_06.class);

		// getBean() "userDao" : Configuration Class 의 Bean Method 이름 (@Configuration public class DaoFactory_06 { ...)
		// UserDao.class : generic 에 사용될 반환 Object Type (이를 명시해주면 반환값에 대해 특별히 Type Cast 필요없음)
		UserDaoInterface_01 userDao = applicationContext.getBean("userDao", UserDaoInterface_01.class);

		userDao.deleteAll();
		assertThat(userDao.getCount(), is(0));

		User user1 = new User("loggar", "ChangHee Lee", "thisispassword");
		userDao.add(user1);
		assertThat(userDao.getCount(), is(1));

		User user2 = userDao.get(user1.getId());
		assertThat(user1.equals(user2), is(true));

		// DaoFactory_06 로부터 생성해온 UserDao 는 Singleton 이 아님.
		DaoFactory_06 factory = new DaoFactory_06();
		UserDaoInterface_01 userDao1 = factory.userDao();
		UserDaoInterface_01 userDao2 = factory.userDao();
		assertThat(userDao1 == userDao2, is(false));

		// ApplicationContext 로부터 생성해온 Spring Bean 이 Singleton.
		UserDaoInterface_01 userDao3 = applicationContext.getBean("userDao", UserDaoInterface_01.class);
		UserDaoInterface_01 userDao4 = applicationContext.getBean("userDao", UserDaoInterface_01.class);
		assertThat(userDao3 == userDao4, is(true));
	}

}
