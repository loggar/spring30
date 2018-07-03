package sample.spring3._05_proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * Transaction 기능을 부가 할 Dynamic Proxy Class
 * 
 */
public class TransactionHandler implements InvocationHandler {
	Object target;
	PlatformTransactionManager transactionManager;

	/**
	 * Transaction 을 적용 할 메소드 이름 패턴
	 */
	String pattern;

	public void setTarget(Object target) {
		this.target = target;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	/*
	 * 실행시 트랜잭션이 적용되는지 여부를 Debug 할 수 있는 모니터링(Logger.debug()) 제공하겟다.
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (method.getName().startsWith(pattern)) {
			System.out.println("invoke " + target.getClass().getName() + "." + method.getName() + " with Transaction");
			return invokeInTransaction(method, args);
		} else {
			return method.invoke(target, args);
		}
	}

	private Object invokeInTransaction(Method method, Object[] args) throws Throwable {
		TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

		try {
			Object ret = method.invoke(target, args);
			transactionManager.commit(status);
			return ret;
		} catch (InvocationTargetException e) {
			transactionManager.rollback(status);
			throw e.getTargetException();
		}
	}
}
