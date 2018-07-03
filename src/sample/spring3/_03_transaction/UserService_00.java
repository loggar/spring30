package sample.spring3._03_transaction;

import java.util.List;

/**
 * 트랜잭션이 적용되지 않은 UserService
 * 
 */
public class UserService_00 {
	public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
	public static final int MIN_RECCOMENDCOUNT_FOR_GOLD = 30;

	UserDao userDao;

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

	private void upgradeLevel(User user) {
		Level newLevel = Level.valueOf(user.getLevel().intValue() + 1);
		user.setLevel(newLevel);
		userDao.update(user);
	}

	private boolean canUpgradeLevel(User user) {
		switch (user.getLevel()) {
			case BASIC:
				return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
			case SILVER:
				return (user.getRecommend() >= MIN_RECCOMENDCOUNT_FOR_GOLD);
			case GOLD:
				return false;
			default:
				throw new IllegalArgumentException("Unknown Level:" + user.getLevel());
		}
	}
}
