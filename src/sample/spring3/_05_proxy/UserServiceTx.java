package sample.spring3._05_proxy;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import sample.spring3._03_transaction.User;

/**
 * 트랜잭션부만 분리한 UserService_01_Tx
 * 트랜잭션 코드만 담당하는 중 비지니스 로직은 다른 userService 구현체에 위임한다.
 * 
 */
public class UserServiceTx implements UserService {
	UserService userService;
	protected PlatformTransactionManager transactionManager;

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	@Override
	public int add(User user) throws Exception {
		TransactionStatus txStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());

		int updateResult = 0;

		try {
			updateResult = userService.add(user);
			transactionManager.commit(txStatus);
		} catch (Exception e) {
			transactionManager.rollback(txStatus);
			throw e;
		}

		return updateResult;
	}

	@Override
	public void upgradeLevels() throws Exception {
		TransactionStatus txStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());

		try {
			userService.upgradeLevels();

			transactionManager.commit(txStatus);
		} catch (Exception e) {
			transactionManager.rollback(txStatus);
			throw e;
		}
	}

}
