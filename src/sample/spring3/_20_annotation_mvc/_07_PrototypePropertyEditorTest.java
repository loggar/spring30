package sample.spring3._20_annotation_mvc;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.beans.PropertyEditorSupport;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.ServletException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import sample.spring3._16_webtest.AbstractDispatcherServletTest;

public class _07_PrototypePropertyEditorTest extends AbstractDispatcherServletTest {
	/*
	 * fake object (내용물은 없지만 id 는 가진, 가짜 오브젝트) 를 통한 바인딩
	 * User.Code 라는 객체에 int 를 바인딩하기위해 FakeCodePropertyEditor 를 등록하여 사용
	 * FakeCode 는 바인딩 이외의 목적으로 사용되는것을 방지하기 위해 id 정보를 제외하고는 UnsupportedOperationException 의 발생을 통해 사용을 막았다.
	 */
	@Test
	public void fakeCodePropertyEditor() throws ServletException, IOException {
		setClasses(UserController.class);
		initRequest("/add.do").addParameter("id", "1").addParameter("name", "Spring").addParameter("userType", "2");
		runService();
		User user = (User) getModelAndView().getModel().get("user");
		assertThat(user.getUserType().getId(), is(2));
		try {
			user.getUserType().getName();
			fail();
		} catch (UnsupportedOperationException e) {
		}
	}

	@Controller
	static class UserController {
		@InitBinder
		public void initBinder(WebDataBinder dataBinder) {
			dataBinder.registerCustomEditor(Code.class, new FakeCodePropertyEditor());
		}

		@RequestMapping("/add")
		public void add(@ModelAttribute User user) {
			System.out.println(user);
		}
	}

	static class FakeCodePropertyEditor extends PropertyEditorSupport {
		public void setAsText(String text) throws IllegalArgumentException {
			Code code = new FakeCode();
			code.setId(Integer.parseInt(text));
			setValue(code);
		}

		public String getAsText() {
			return String.valueOf(((Code) getValue()).getId());
		}

	}

	static class User {
		int id;
		String name;
		Code userType;

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

		public Code getUserType() {
			return userType;
		}

		public void setUserType(Code userType) {
			this.userType = userType;
		}

		@Override
		public String toString() {
			return "User [id=" + id + ", name=" + name + ", userType="
					+ userType + "]";
		}
	}

	static class Code {
		int id;
		String name;

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

		public String toString() {
			return "Code [id=" + id + ", name=" + name + "]";
		}
	}

	static class FakeCode extends Code {
		public String getName() {
			throw new UnsupportedOperationException();
		}

		public void setName(String name) {
			throw new UnsupportedOperationException();
		}
	}

	/*
	 * 아래는 프로토타입 도메인 오브젝트 프로퍼티 에디터의 사용 예시이다.
	 * fake object 를 만들지 않고 아예 Code 정보를 DB 로부터 읽어와 완전한 Code 객체를 생성해 내는 방법이다.
	 * 이 방법은 PropertyEditor 가 Dao|Service 객체를 DI 받아야하며, 이는 PropertyEditor 역시 빈으로 등록되어야 한다는 뜻이다. 
	 * 아래는 Provider 를 통해 CodePropertyEditor 를 빈으로 등록하는 방법을 사용했다.
	 */
	@Test
	public void prototypePropertyEditor() throws ServletException, IOException {
		setClasses(UserController2.class, CodePropertyEditor.class, CodeService.class);
		initRequest("/add.do").addParameter("id", "1").addParameter("name", "Spring").addParameter("userType", "2");
		runService();
		User user = (User) getModelAndView().getModel().get("user");
		user.getUserType().getName();
	}

	@Controller
	static class UserController2 {
		@Inject Provider<CodePropertyEditor> codeEditorProvider;

		@InitBinder
		public void initBinder(WebDataBinder dataBinder) {
			dataBinder.registerCustomEditor(Code.class, codeEditorProvider.get());
		}

		@RequestMapping("/add")
		public void add(@ModelAttribute User user) {
			System.out.println(user);
		}
	}

	/*
	 * 프로토타입 도메인 오브젝트 프로퍼티 에디터 는 싱글톤 빈으로 등록하지 않고 @Scope("prototype") 으로 지정한다.
	 * 프로퍼티에디터는 바인딩을 위해 적어도 한번은 상태를변경한 오브젝트를 인스턴스 필드로 가지게 되고,
	 * 이는 멀티스레드 환경에서 싱글톤으로 만들어 여러 오브젝트가 공유해서 사용하면 안된다는것을 의미한다.
	 * 싱글톤 빈으로 사용하기 위해서는 @Scope("prototype") 스코프를 지정해야 한다.
	 */
	@Scope("prototype")
	static class CodePropertyEditor extends PropertyEditorSupport {
		@Autowired CodeService codeService;

		public void setAsText(String text) throws IllegalArgumentException {
			setValue(codeService.getCode(Integer.parseInt(text)));
		}

		public String getAsText() {
			return String.valueOf(((Code) getValue()).getId());
		}
	}

	static class CodeService {
		public Code getCode(int id) {
			Code c = new Code();
			c.setId(id);
			c.setName("name");
			return c;
		}
	}
}
