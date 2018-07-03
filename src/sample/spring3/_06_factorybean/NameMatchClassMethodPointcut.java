package sample.spring3._06_factorybean;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.util.PatternMatchUtils;

/**
 * 클래스 이름 필터가 포함된 포인트컷
 * 
 */
public class NameMatchClassMethodPointcut extends NameMatchMethodPointcut {

	/**
	 * generated serial version id
	 */
	private static final long serialVersionUID = -3180857992099518764L;

	/*
	 * NameMatchMethodPointcut 의 ClassFilter 는 원래 모든 class 를 다 허용한다.
	 * 이를 mappedClassName 으로 필터링하는 SimpleClassFilter(inner class 정의) 로 덮어씌운다.
	 */
	public void setMappedClassName(String mappedClassName) {
		this.setClassFilter(new SimpleClassFilter(mappedClassName));
	}

	static class SimpleClassFilter implements ClassFilter {
		String mappedName;

		private SimpleClassFilter(String mappedName) {
			this.mappedName = mappedName;
		}

		@Override
		public boolean matches(Class<?> clazz) {
			// PatternMatchUtils.simpleMatch 는 와일드카드(*) 가 들어간 문자열 비교를 지원하는 spring utilitie method 이다.
			// *name, name*, *name* 세가지 방식을 모두 지원한다.
			return PatternMatchUtils.simpleMatch(mappedName, clazz.getSimpleName());
		}
	}

}
