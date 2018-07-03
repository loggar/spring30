package sample.spring3._03_transaction;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import sample.spring3._03_transaction.Level;
import sample.spring3._03_transaction.User;
import sample.spring3._03_transaction.UserDao;

/**
 * Spring 의 Transaction interface 인 PlatformTransactionManager 트랜잭션을 적용한 UserService
 * 
 */
public class UserService_02 {
	public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
	public static final int MIN_RECCOMENDCOUNT_FOR_GOLD = 30;

	UserDao userDao;
	DataSource dataSource;

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void upgradeLevels() throws Exception {
		// DataSourceTransactionManager 이용, 이는 PlatformTransactionManager 의 구현체중 JDBC/Connection 에 해당하는 트랜잭션 구현체.
		// 아직 PlatformTransactionManager 를 DI 하여 DataSourceTransactionManager 의존으로부터 벗어날 필요가 있다.
		PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
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
		PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
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
