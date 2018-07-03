package sample.spring3._05_proxy;

/**
 * HelloTarget 에 추가 기능을 데코레이트 할 간단한 Proxy Class.
 * 이 프록시의 두가지문제점 1. TargetClass 인 HelloTarget class 처럼 Hello interface 를 모두 구현해야한다.
 * 부가기능(toUpperCase)이 모든 메소드에 중복된다.
 * 
 */
public class HelloUpperProxy implements Hello {
	Hello hello;

	public HelloUpperProxy(Hello hello) {
		super();
		this.hello = hello;
	}

	@Override
	public String sayHello(String name) {
		return hello.sayHello(name).toUpperCase();
	}

	@Override
	public String sayHi(String name) {
		return hello.sayHi(name).toUpperCase();
	}

	@Override
	public String sayThanks(String name) {
		return hello.sayThanks(name).toUpperCase();
	}

}
