package sample.spring3._05_proxy;

import java.util.List;

import sample.spring3._03_transaction.Level;
import sample.spring3._03_transaction.User;
import sample.spring3._03_transaction.UserDao;

/**
 * UserService Interface 분리, TransactionManager 분리(UserService_01_Tx).
 * 
 */
public class UserServiceImpl implements UserService {
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
