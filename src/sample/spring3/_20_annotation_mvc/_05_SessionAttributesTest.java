package sample.spring3._20_annotation_mvc;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.junit.Test;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import sample.spring3._16_webtest.AbstractDispatcherServletTest;

/**
 * html form 입력을 예로 "@SessionAttributes" 를 설명하겠다.
 * 모델정보를 사용자로부터 입력받을경우 입력이 필요없는 필드에 대한 바인딩을 위해
 *     hidden 필드 이용 : 보안상의 문제
 *     DB 재조회 : 성능상의 문제
 *     Session : 좋은 해결법이며, 사용 후 반드시 세션을 지워줘야한다.
 *     
 * "@SessionAttributes"
 *     컨트롤러에 "@SessionAttributes(sessionName)" 지정.
 *     컨트롤러가 생성하는 모델 중에서 sessionName 과 동일한 이름이 있다면 이를 세션에 자동으로 저장.
 *     "@ModelAttribute" 로 지정된 파라미터 이름이 sessionName 이라면 먼저 Session 에서 해당 객체를 가져와 매핑 후 폼에서 전달된 필드정보를 세팅.
 *     비지니스 로직 수행
 *     파라미터로 전달받은 sessionStatus 를 setComplete();
 * 
 */
public class _05_SessionAttributesTest extends AbstractDispatcherServletTest {
	@Test
	public void sessionAttr() throws ServletException, IOException {
		setClasses(UserController.class);
		initRequest("/user/edit").addParameter("id", "1");
		runService();

		HttpSession session = request.getSession();
		assertThat(session.getAttribute("user"), is(getModelAndView().getModel().get("user")));

		initRequest("/user/edit", "POST").addParameter("id", "1").addParameter("name", "Spring2");
		request.setSession(session);
		runService();
		assertThat(((User) getModelAndView().getModel().get("user")).getEmail(), is("mail@spring.com"));
		assertThat(session.getAttribute("user"), is(nullValue()));
	}

	@Controller
	@SessionAttributes("user")
	static class UserController {
		@RequestMapping(value = "/user/edit", method = RequestMethod.GET)
		public User form(@RequestParam int id) {
			return new User(1, "Spring", "mail@spring.com");
		}

		@RequestMapping(value = "/user/edit", method = RequestMethod.POST)
		public void submit(@ModelAttribute User user, SessionStatus sessionStatus) {
			sessionStatus.setComplete();
		}
	}

	static class User {
		int id;
		String name;
		String email;

		public User(int id, String name, String email) {
			this.id = id;
			this.name = name;
			this.email = email;
		}

		public User() {
		}

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

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String toString() {
			return "User [email=" + email + ", id=" + id + ", name=" + name + "]";
		}
	}
}
