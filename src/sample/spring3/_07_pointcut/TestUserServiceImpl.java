package sample.spring3._07_pointcut;

import java.util.List;

import sample.spring3._03_transaction.Level;
import sample.spring3._03_transaction.User;
import sample.spring3._03_transaction.UserDao;
import sample.spring3._05_proxy.UserService;

/**
 * proxy 적용될 테스트용 타겟 클래스 (*ServiceImpl)
 * context 주입을 통해 bean 생성 한 후 UserServiceImpl 자동 생성때와 Advisor 적용 여부를 비교해 본다.
 * 
 */
public class TestUserServiceImpl implements UserService {
	protected UserDao userDao;

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public void upgradeLevels() {
		List<User> users = userDao.getAll();
		for (User user : users) {
			if (canUpgradeLevel(user)) upgradeLevel(user);
		}
	}

	public int add(User user) {
		if (user.getLevel() == null) user.setLevel(Level.BASIC);
		return userDao.add(user);
	}

	protected void upgradeLevel(User user) {
		if (user.getId().equals("loggar4")) {
			System.out.println("UserServiceImplTest.upgradeLevels() throw TestUserServiceException");
			throw new TestUserServiceException();
		}

		Level newLevel = Level.valueOf(user.getLevel().intValue() + 1);
		user.setLevel(newLevel);
		userDao.update(user);
	}

	protected boolean canUpgradeLevel(User user) {
		switch (user.getLevel()) {
			case BASIC:
				return (user.getLogin() >= UserService.MIN_LOGCOUNT_FOR_SILVER);
			case SILVER:
				return (user.getRecommend() >= UserService.MIN_RECCOMENDCOUNT_FOR_GOLD);
			case GOLD:
				return false;
			default:
				throw new IllegalArgumentException("Unknown Level:" + user.getLevel());
		}
	}
}
