package sample.spring3._06_factorybean;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * Transaction Advice Class
 * 
 */
public class TransactionAdvice implements MethodInterceptor {
	PlatformTransactionManager transactionManager;

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		System.out.println("TransactionAdvice.invoke()");

		TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

		try {
			Object ret = invocation.proceed();
			transactionManager.commit(status);
			System.out.println("TransactionAdvice transactionManager.commit()");
			return ret;
		} catch (RuntimeException e) {
			transactionManager.rollback(status);
			System.out.println("TransactionAdvice transactionManager.rollback()");
			throw e;
		}
	}

}
