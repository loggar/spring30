package sample.spring3._05_proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Dynamic Proxy Handler 구현
 * HelloTarget 의 InvocationHandler
 * 
 */
public class HelloUpperProxyHandler implements InvocationHandler {
	Object target;

	public HelloUpperProxyHandler(Object target) {
		super();
		this.target = target;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object ret = method.invoke(target, args);

		if (ret instanceof String && method.getName().startsWith("say")) {
			return ((String) ret).toUpperCase();
		} else {
			return ret;
		}
	}

}
