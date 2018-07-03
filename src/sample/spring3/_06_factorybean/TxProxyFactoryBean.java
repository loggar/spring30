package sample.spring3._06_factorybean;

import java.lang.reflect.Proxy;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import sample.spring3._05_proxy.TransactionHandler;

/**
 * TransactionHandler 가 적용된 Target Object 의 Interface 를 생성해주는 FactoryBean
 * 
 */
public class TxProxyFactoryBean implements FactoryBean<Object> {
	Object target;
	PlatformTransactionManager transactionManager;
	String pattern;

	/*
	 * Dynamic Proxy 를 생성할 때 필요하다.
	 */
	Class<?> serviceInterface;

	public void setTarget(Object target) {
		this.target = target;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public void setServiceInterface(Class<?> serviceInterface) {
		this.serviceInterface = serviceInterface;
	}

	/**
	 * TransactionHandler 가 적용된 target Object 구현체 생성후, serviceInterface 를 반환
	 */
	@Override
	public Object getObject() throws Exception {
		TransactionHandler transactionHandler = new TransactionHandler();
		transactionHandler.setTarget(target);
		transactionHandler.setTransactionManager(transactionManager);
		transactionHandler.setPattern("upgradeLevels");

		return Proxy.newProxyInstance(
				getClass().getClassLoader(),
				new Class[] { serviceInterface },
				transactionHandler);
	}

	@Override
	public Class<?> getObjectType() {
		return serviceInterface;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

}
