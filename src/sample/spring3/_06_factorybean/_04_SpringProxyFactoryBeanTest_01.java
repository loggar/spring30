package sample.spring3._06_factorybean;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.aop.Advisor;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

import sample.spring3._05_proxy.Hello;
import sample.spring3._05_proxy.HelloTarget;

/**
 * Spring 의 ProxyFactoryBean 을 통해 Target Object 와 부가기능(Advice) 가 적용된 service interface 를 생성한다.
 * 
 */
public class _04_SpringProxyFactoryBeanTest_01 {
	/**
	 * Advice 만 적용된 ProxyFactoryBean
	 */
	@Test
	public void proxyFactoryBean() {
		ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
		proxyFactoryBean.setTarget(new HelloTarget());
		proxyFactoryBean.addAdvice(new UpperAdvice());

		/*
		 * ProxyFactoryBean 은 Target Object 의 Service Interface 를 지정해주지 않아도
		 * 인터페이스 정보를 자동검출하여 알아낸 인터페이스들을 모두 구현하는 프록시를 만들어준다.
		 */
		Hello helloInterface = (Hello) proxyFactoryBean.getObject();

		assertThat(helloInterface.sayHello("Loggar"), is("HELLO LOGGAR"));
		assertThat(helloInterface.sayHi("Loggar"), is("HI LOGGAR"));
		assertThat(helloInterface.sayThanks("Loggar"), is("THANKS LOGGAR"));
	}

	/**
	 * Pointcut 과 Advisor 가 적용된 ProxyFactoryBean
	 */
	@Test
	public void pointcutAdvisor() {
		ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
		proxyFactoryBean.setTarget(new HelloTarget());

		/*
		 * 메소드 이름을 비교해서 대상 선정 알고리즘을 제공하는 Pointcut 인 NameMatchMethodPointcut 생성.
		 */
		NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
		pointcut.setMappedName("sayH*");

		/*
		 * proxyFactoryBean 에 Pointcut 과 Advice 를 묶은 Advisor 를 제공.
		 */
		Advisor upperAdvisor1 = new DefaultPointcutAdvisor(pointcut, new UpperAdvice());
		proxyFactoryBean.addAdvisor(upperAdvisor1);

		Hello hello = (Hello) proxyFactoryBean.getObject();

		assertThat(hello.sayHello("Loggar"), is("HELLO LOGGAR"));
		assertThat(hello.sayHi("Loggar"), is("HI LOGGAR"));
		assertThat(hello.sayThanks("Loggar"), is(not("THANKS LOGGAR")));
	}

	/**
	 * method 뿐 아니라 target Class 도 선별하는 Advisor
	 * 
	 * Pointcut interface 는 원래 메소드뿐 아니라 클래스의 선별까지 담당하는 인터페이스이다.
	 * 
	 * <pre>
	 * public interface Pointcut {
	 * 	ClassFilter getClassFilter();
	 * 
	 * 	MethodMatcher getMethodMatcher();
	 * }
	 * </pre>
	 */
	@Test
	public void classNamePointcutAdvisor() {
		/*
		 * Pointcut 정의.
		 * NameMatchMethodPointcut 를 확장하는 익명 inner class 를 정의하고
		 * getClassFilter 를 구현해 테스트용 부가 기능을 제공한다.
		 */
		@SuppressWarnings("serial")
		NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut() {
			public ClassFilter getClassFilter() {
				return new ClassFilter() {
					public boolean matches(Class<?> clazz) {
						boolean adviced = clazz.getSimpleName().startsWith("HelloT");

						if (adviced) System.out.println(clazz.getName() + " is adviced target class");

						return adviced;
					}
				};
			}
		};
		pointcut.setMappedName("sayH*");

		/*
		 * Advisor 대상 target class test
		 */
		ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
		proxyFactoryBean.setTarget(new HelloTarget());
		proxyFactoryBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UpperAdvice()));
		Hello hello = (Hello) proxyFactoryBean.getObject();

		assertThat(hello.sayHello("Loggar"), is("HELLO LOGGAR"));
		assertThat(hello.sayHi("Loggar"), is("HI LOGGAR"));
		assertThat(hello.sayThanks("Loggar"), is(not("THANKS LOGGAR")));

		/*
		 * adviosr 대상이 아닌 test 용 inner class 정의 후 test
		 */
		class HelloWorld extends HelloTarget {
		}
		;

		ProxyFactoryBean proxyFactoryBean_02 = new ProxyFactoryBean();
		proxyFactoryBean_02.setTarget(new HelloWorld());
		proxyFactoryBean_02.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UpperAdvice()));
		Hello hello_02 = (Hello) proxyFactoryBean_02.getObject();

		assertThat(hello_02.sayHello("Loggar"), is("Hello Loggar"));
		assertThat(hello_02.sayHi("Loggar"), is("Hi Loggar"));
		assertThat(hello_02.sayThanks("Loggar"), is("Thanks Loggar"));
	}

}
