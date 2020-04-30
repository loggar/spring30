package sample.spring3._05_proxy;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
/*
 * static import 하면 org.mockito.Mockito 의 메소드들을 로컬 메소드처럼 호출 할 수 있어 편하다.
 */
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import sample.spring3._03_transaction.Level;
import sample.spring3._03_transaction.User;
import sample.spring3._03_transaction.UserDao;

/**
 * Mockito framework 을 이용한 간단한 Mock Test.
 * 
 */
public class UserServiceImplTest_03_Mockito {
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
	public void mockUpgradeLevels() throws Exception {
		UserServiceImpl userServiceImpl = new UserServiceImpl();

		// Mock Object UserDao 생성. 아직 mockUserDao 에는 아무런 기능이 없다.
		UserDao mockUserDao = mock(UserDao.class);

		// mockUserDao.getAll() 호출시 users 를 반환하는 메소드 기능추가.
		when(mockUserDao.getAll()).thenReturn(this.users);

		// 위 Mock Object 를 userServiceImpl 에 DI 한다.
		userServiceImpl.setUserDao(mockUserDao);

		// 수행될 테스트.
		userServiceImpl.upgradeLevels();

		// User 타입의 오브젝트를 파라미터로하는 mockUserDao 의 update() 메소드가 2번 호출되었는지 검증(verify).
		verify(mockUserDao, times(2)).update(any(User.class));

		// mockUserDao.update(users.get(1)) 이 호출되었는가
		verify(mockUserDao).update(users.get(1));
		assertThat(users.get(1).getLevel(), is(Level.SILVER));

		// mockUserDao.update(users.get(3)) 이 호출되었는가
		verify(mockUserDao).update(users.get(3));
		assertThat(users.get(3).getLevel(), is(Level.GOLD));
	}
}
