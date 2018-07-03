package sample.spring3._07_pointcut;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

/**
 * pointcut 표현식
 * AspectJExpressionPointcut 의 execution() 지시자를 이용한 pointcut 지정
 * execution([접근지정자] ReturnTypePattern [TypePattern.]methodNamePattern([ParamTypePattern])[ throws ExceptionType])
 * 
 * excution() 외의 AspectJExpressionPointcut Expression 소개
 * 1. bean(*Service) : id 가 "Service" 로 끝나는 Spring Bean 으로 등록된 객체.
 * 2. annotation(org.springframework.transaction.annotation.Transactional) : "@Transaction" 애노태이션이 적용된 모든 메소드.
 * 
 * AspectJExpressionPointcut 은 Pointcut.getClassFilter() 의 동작이 의도한 것과 다르다.
 * class 의 판별 역시 메소드 단계에서 matches 를 테스트해야한다.
 * /spring3/comment/_07_pointcut_getClassFilter.html 참고.
 * 
 */
public class _01_AspectJExpressionPointcutExpressionTest {
	@Test
	public void methodSignaturePointcut() throws SecurityException, NoSuchMethodException {
		AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
		pointcut.setExpression("execution(public int sample.spring3._07_pointcut.Target.minus(int,int) throws java.lang.RuntimeException)");

		System.out.println(pointcut.getExpression());

		assertThat(pointcut.matches(Target.class.getMethod("minus", int.class, int.class), Target.class, null), is(true));
		assertThat(pointcut.matches(Target.class.getMethod("plus", int.class, int.class), Target.class, null), is(false));
		assertThat(pointcut.matches(Bean.class.getMethod("method"), Bean.class, null), is(false));

		// 다음은 true 를 return 한다. /spring3/comment/_07_pointcut_getClassFilter.html 참고.
		assertThat(pointcut.matches(Bean.class), is(true));
	}

	@Test
	public void pointcut_expression() throws Exception {
		tagetClassPointcutMatches("execution(* *(..))", true, true, true, true, true, true);
		tagetClassPointcutMatches("execution(* hello(..))", true, true, false, false, false, false);
		tagetClassPointcutMatches("execution(* hello())", true, false, false, false, false, false);
		tagetClassPointcutMatches("execution(* hello(String))", false, true, false, false, false, false);
		tagetClassPointcutMatches("execution(* meth*(..))", false, false, false, false, true, true);

		tagetClassPointcutMatches("execution(* *(int,int))", false, false, true, true, false, false);
		tagetClassPointcutMatches("execution(* *())", true, false, false, false, true, true);

		tagetClassPointcutMatches("execution(* sample.spring3._07_pointcut.Target.*(..))", true, true, true, true, true, false);
		tagetClassPointcutMatches("execution(* sample.spring3._07_pointcut.*.*(..))", true, true, true, true, true, true);
		tagetClassPointcutMatches("execution(* sample.spring3._07_pointcut..*.*(..))", true, true, true, true, true, true);
		tagetClassPointcutMatches("execution(* sample.spring3..*.*(..))", true, true, true, true, true, true);

		tagetClassPointcutMatches("execution(* com..*.*(..))", false, false, false, false, false, false);
		tagetClassPointcutMatches("execution(* *..Target.*(..))", true, true, true, true, true, false);
		tagetClassPointcutMatches("execution(* *..Tar*.*(..))", true, true, true, true, true, false);
		tagetClassPointcutMatches("execution(* *..*get.*(..))", true, true, true, true, true, false);
		tagetClassPointcutMatches("execution(* *..B*.*(..))", false, false, false, false, false, true);

		tagetClassPointcutMatches("execution(* *..TargetInterface.*(..))", true, true, true, true, false, false);

		tagetClassPointcutMatches("execution(* *(..) throws Runtime*)", false, false, false, true, false, true);

		tagetClassPointcutMatches("execution(int *(..))", false, false, true, true, false, false);
		tagetClassPointcutMatches("execution(void *(..))", true, true, false, false, true, true);
	}

	private void tagetClassPointcutMatches(String expression, boolean... expected) throws Exception {
		if (expected.length >= 1) pointcutMatches(expression, expected[0], Target.class, "hello");
		if (expected.length >= 2) pointcutMatches(expression, expected[1], Target.class, "hello", String.class);
		if (expected.length >= 3) pointcutMatches(expression, expected[2], Target.class, "plus", int.class, int.class);
		if (expected.length >= 4) pointcutMatches(expression, expected[3], Target.class, "minus", int.class, int.class);
		if (expected.length >= 5) pointcutMatches(expression, expected[4], Target.class, "method");
		if (expected.length >= 6) pointcutMatches(expression, expected[5], Bean.class, "method");
	}

	private void pointcutMatches(String expression, Boolean expected, Class<?> clazz, String methodName, Class<?>... args) throws Exception {
		AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
		pointcut.setExpression(expression);

		assertThat(pointcut.matches(clazz.getMethod(methodName, args), clazz, null), is(expected));
	}

}
