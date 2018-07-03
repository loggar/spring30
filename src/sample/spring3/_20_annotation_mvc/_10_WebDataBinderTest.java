package sample.spring3._20_annotation_mvc;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Test;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import sample.spring3._16_webtest.AbstractDispatcherServletTest;

/**
 * WebDataBinder 설정 항목
 *     allowedFields
 *         클라이언트에서 파라미터로 넘어온 필드 중 dataBinder.allowedFields 로 지정된 필드만 바인딩을 허락한다.
 *     disallowedFields
 *         반대로 클라이언트에서 파라미터로 넘어와도 바인딩 하지 않을 필드를 지정할 수 있다.
 *     requiredFields
 *         allowedFields 의 지정으로 필수 바인딩 필드를 지정 할 수 있다.
 *     fieldMarkerPrefix
 *         HTML 의 checkbox 는 체크시 해당이름의 값으로 체크박스의 value 정보가 넘어오지만 미체크시 아예 해당 이름의 파라미터가 구성되지 않는다.
 *         이는 바인딩시 큰 문제가 되므로 WebDataBinder 는 fieldMarkerPrefix 라는 속성을 가지고 있고, 이에 해당하는 문자가 파라미터 이름
 *         앞에 붙어오면 히든필드로 체크되지 않은 값의 파라미터 이름이 넘어온것으로 간주한다.
 *         그리고 바인딩시 해당 필드에 false, 빈 배열, null 중 하나로 대체한다. 
 *         필드마커의 기본값은 파라미터 이름 앞의 "_" 이며 변경하고 싶다면
 *         setFieldmarker() 함수를 이용한다.
 *     fieldDefaultPrefix
 *         체크박스가 체크되지 않았을때의 기본값을 지정할 수 있다. 기본값은 파라미터이름 앞에 "!" 이다. 해당 파라미터 이름에대해서 파라미터로 구성되어있지 않다면
 *         !파라미터이름 으로 넘어온 값을 바인딩한다. 해당 필드를 히든으로 만들어 넘어오는것이다(클라이언트가 넘겨줘야한다.)
 *     
 */
public class _10_WebDataBinderTest extends AbstractDispatcherServletTest {
	@Test
	public void allowed() throws ServletException, IOException {
		setClasses(UserController.class);
		initRequest("/add.do", "POST").addParameter("id", "1").addParameter("name", "name");
		runService();
		User user = (User) getModelAndView().getModel().get("user");
		assertThat(user.getId(), is(1));
		assertThat(user.getName(), is(nullValue()));
	}

	@Controller
	static class UserController {
		@InitBinder
		public void initBinder(WebDataBinder dataBinder) {
			dataBinder.setAllowedFields("id");
		}

		@RequestMapping("/add")
		public void add(@ModelAttribute User user) {
		}
	}

	@Test
	public void prefix() throws ServletException, IOException {
		setClasses(UserController2.class);
		initRequest("/add.do").addParameter("_flag", "").addParameter("!type", "member");
		runService();
		User user = (User) getModelAndView().getModel().get("user");
		assertThat(user.isFlag(), is(false));
		assertThat(user.getType(), is("member"));
	}

	@Controller
	static class UserController2 {
		@RequestMapping("/add")
		public void add(@ModelAttribute User user) {
		}
	}

	static class User {
		int id;
		String name;
		boolean flag = true;
		String type;

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

		public boolean isFlag() {
			return flag;
		}

		public void setFlag(boolean flag) {
			this.flag = flag;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		@Override
		public String toString() {
			return "User [flag=" + flag + ", id=" + id + ", name=" + name
					+ ", type=" + type + "]";
		}
	}
}
