package sample.spring3._20_annotation_mvc;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import sample.spring3._16_webtest.AbstractDispatcherServletTest;

/**
 * 모델을 검증하는 Validator 인터페이스는
 *     boolean supports(Class<?> clazz);
 *     void validate(Object target, Errors errors);
 * 위 두 메소드로 구성되어 있으며, 검증가능한 타입인지 supports() 로 테스트 후 성공하면 validate() 가 호출된다.
 * 검증과정에서 실패한 필드나 오브젝트는 Errors 인터페이스를 통해서 최종적으로 BindingResult 에 담겨 컨트롤러로 전달된다.
 * 
 * 컨트롤러 로직 직전의 오브젝트 검증은 반드시 필요하다. 클라이언트에서 데이터를 조작하여 요청을 시도 할 가능성이 늘 존재하기 때문이다.
 * 
 * Validator 다음 방식으로 적용 될 수 있다.
 *     컨트롤러 메소드 내의 코드
 *     "@Valid" 를 이용한 자동 검증
 *     서비스 계층에서의 검증
 *     서비스 계층으로서의 valid 오브젝트를 따로 두고 수행
 * 
 */
public class _11_ValidatorTest extends AbstractDispatcherServletTest {
	@Test
	public void atrvalid() throws ServletException, IOException {
		setClasses(UserValidator.class, UserController.class);
		initRequest("/add.do").addParameter("id", "1");// .addParameter("age", "-2");
		runService();
	}

	/*
	 * isAssignableFrom 은 해당 오브젝트 타입의 인스턴스가 생성 될 수 있는지 여부를 반환하는 메소드이다.
	 */
	static class UserValidator implements Validator {
		public boolean supports(Class<?> clazz) {
			return (User.class.isAssignableFrom(clazz));
		}

		public void validate(Object target, Errors errors) {
			User user = (User) target;
			ValidationUtils.rejectIfEmpty(errors, "name", "field.required");
			ValidationUtils.rejectIfEmpty(errors, "age", "field.required");
			
			/*
			 * "field.min" 은 메시지 프로퍼티의 key 값이다.
			 */
			if (errors.getFieldError("age") == null)
				if (user.getAge() < 0) errors.rejectValue("name", "field.min", new Object[] { 0 }, "min default");
		}
	}

	@Controller
	static class UserController {
		@Autowired UserValidator validator;

		@InitBinder
		public void init(WebDataBinder dataBinder) {
			dataBinder.setValidator(this.validator);
		}

		@RequestMapping("/add")
		public void add(@ModelAttribute User user, BindingResult result) {
			validator.validate(user, result);
			if (result.hasErrors()) {
			}
			else {
			}
			System.out.println(user);
			System.out.println(result);
		}
	}

	/*
	 * 다음은 LocalValidatorFactoryBean 을 DI 받아 사용하는 JSR-303 빈 검증 방식이다. 스프링의 Validator 와는 다른 방식이지만
	 * 모델 오브젝트에 검증 로직이 표현되어도 상관없다면 매우 간편한 방식이다.
	 */
	@Test
	public void jsr303() throws ServletException, IOException {
		setClasses(UserController2.class, LocalValidatorFactoryBean.class);
		initRequest("/add.do").addParameter("id", "1").addParameter("age", "-1");
		runService();
	}

	@Controller
	static class UserController2 {
		@Autowired Validator validator;

		@InitBinder
		public void init(WebDataBinder dataBinder) {
			dataBinder.setValidator(this.validator);
		}

		@RequestMapping("/add")
		public void add(@ModelAttribute @Valid User user, BindingResult result) {
			System.out.println(user);
			System.out.println(result);
		}
	}

	public static class User {
		int id;
		@NotNull String name;
		@Min(0) Integer age;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Integer getAge() {
			return age;
		}

		public void setAge(Integer age) {
			this.age = age;
		}

		@Override
		public String toString() {
			return "User [age=" + age + ", id=" + id + ", name=" + name + "]";
		}
	}
}
