package sample.spring3._03_transaction;

import java.util.List;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * PlatformTransactionManager 을 DI 하여 의존성 탈피.
 * 
 */
public class UserService_03 {
	public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
	public static final int MIN_RECCOMENDCOUNT_FOR_GOLD = 30;

	protected UserDao userDao;
	protected PlatformTransactionManager transactionManager;

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void upgradeLevels() throws Exception {
		TransactionStatus txStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());

		try {
			List<User> users = userDao.getAll();
			for (User user : users) {
				if (canUpgradeLevel(user)) upgradeLevel(user);
			}

			transactionManager.commit(txStatus);
		} catch (Exception e) {
			transactionManager.rollback(txStatus);
			throw e;
		}
	}

	public int add(User user) throws Exception {
		TransactionStatus txStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());

		int updateResult = 0;

		try {
			if (user.getLevel() == null) user.setLevel(Level.BASIC);
			updateResult = userDao.add(user);
			transactionManager.commit(txStatus);
		} catch (Exception e) {
			transactionManager.rollback(txStatus);
			throw e;
		}

		return updateResult;
	}

	protected void upgradeLevel(User user) {
		Level newLevel = Level.valueOf(user.getLevel().intValue() + 1);
		user.setLevel(newLevel);
		userDao.update(user);
	}

	protected boolean canUpgradeLevel(User user) {
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
