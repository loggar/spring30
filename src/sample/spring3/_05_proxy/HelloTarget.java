package sample.spring3._05_proxy;

public class HelloTarget implements Hello {

	@Override
	public String sayHello(String name) {
		return "Hello " + name;
	}

	@Override
	public String sayHi(String name) {
		return "Hi " + name;
	}

	@Override
	public String sayThanks(String name) {
		return "Thanks " + name;
	}

}
