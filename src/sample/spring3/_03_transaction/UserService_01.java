package sample.spring3._03_transaction;

import java.sql.Connection;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * TransactionSynchronizationManager 트랜잭션을 적용한 UserService
 * 
 */
public class UserService_01 {
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
		TransactionSynchronizationManager.initSynchronization();
		Connection con = DataSourceUtils.getConnection(dataSource);
		con.setAutoCommit(false);

		try {
			List<User> users = userDao.getAll();
			for (User user : users) {
				if (canUpgradeLevel(user)) upgradeLevel(user);
			}

			con.commit();
		} catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			DataSourceUtils.releaseConnection(con, dataSource);
			TransactionSynchronizationManager.unbindResource(this.dataSource);
			TransactionSynchronizationManager.clearSynchronization();
		}
	}

	public int add(User user) throws Exception {
		TransactionSynchronizationManager.initSynchronization();
		Connection con = DataSourceUtils.getConnection(dataSource);
		con.setAutoCommit(false);

		int updateResult = 0;

		try {
			if (user.getLevel() == null) user.setLevel(Level.BASIC);
			updateResult = userDao.add(user);
			con.commit();
		} catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			DataSourceUtils.releaseConnection(con, dataSource);
			TransactionSynchronizationManager.unbindResource(this.dataSource);
			TransactionSynchronizationManager.clearSynchronization();
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
