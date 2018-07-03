package sample.spring3._06_factorybean;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * 데코레이팅 할 Advice Class.
 * 메소드 실행시 Target Object 를 전달 할 필요가 없다.
 * Advice를 사용하는 ProxyFactoryBean 에 Target Object가 세팅되어 있다..
 * MethodInterceptor(Advice) 는 부가기능을 제공하는 데만 집중한다.
 * MethodInterceptor(Advice) 는 특정 Target 에 종속적이지 않다.
 * MethodInvocation 은 ProxyFactoryBean 의 Target Object 의 method 를 실행하는 객체이다.
 * 
 */
public class UpperAdvice implements MethodInterceptor {

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		String ret = (String) invocation.proceed();
		return ret.toUpperCase();
	}

}
