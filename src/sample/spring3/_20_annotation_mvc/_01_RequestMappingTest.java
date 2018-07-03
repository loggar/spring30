package sample.spring3._20_annotation_mvc;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Test;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import sample.spring3._16_webtest.AbstractDispatcherServletTest;

/**
 * "@RequestMapping" 에 의한 핸들러 매핑
 *     @RequestMapping("/admin/user")    URL Patterns
 *     @RequestMapping("/admin/user/{id}")    {path변수}    {} 안의 변수를 request prameter 로 사용, url pattern 안에 포함.
 *     @RequestMapping("/hello", "/hello/", "/hello.*") = @RequestMapping("/hello") 동일한 결과
 *     @RequestMapping(value="/test" method=RequestMethod.GET)    HTTP METHOD 에 따라 매핑
 *     @RequestMapping(value="/test" params="type=admin")    REQUEST PARAMETER type=admin 을 갖는 URL(/test) 만 매핑
 *     @RequestMapping(value="/test" params="!type")    type 이라는 이름의 REQUEST PARAMETER 이 없어야 매핑
 *     @RequestMapping(value="/test" headers="content-type=text/*")    HTTP HEADER 의 content-type 에 매핑
 * 
 * 하나의 URL 이 여러개의 "@RequestMapping" 메소드에 매핑될때는 조건이 더 구체적인 쪽이 선택된다.
 * URL 패턴에서는 더 긴 쪽이 선택된다.
 * 
 * 클래스 레벨과 메소드 레벨에 "@RequestMapping" 이 등록된 경우 클래스 레벨에 정의된 URL 은 메소드 레벨에 정의된 URL 의 상위 path 로 지정된다.
 * 메소드 레벨에만 "@RequestMapping" 이 등록된 경우 매핑이 동작하지 않는다. 공통의 path 가 필요없는경우라도 클래스레벨에 세부값이 없는 "@RequestMapping" 을 등록해줘야 매핑에 참여한다.
 * 클래스 레벨에만 "@RequestMapping" 이 등록된 경우 해당 URL 패턴의 하위 path 와 그 클래스의 메소드 이름과 매핑한다.
 * 
 * "@RequestMapping" 는 sub class controller 로 상속된다.
 * sub class controller 에서 재정의 하면 sub class controller 의 "@RequestMapping" 이 적용된다.
 * 인터페이스에 정의된 "@RequestMapping" 역시 상속된다.
 * 
 */
public class _01_RequestMappingTest extends AbstractDispatcherServletTest {
	@Test
	public void requestmapping() throws ServletException, IOException {
		setClasses(Ctrl1.class);
		// default suffix pattern
		runService("/hello").assertViewName("hello.jsp");
		runService("/hello/").assertViewName("hello.jsp");
		runService("/hello.a").assertViewName("hello.jsp");
		runService("/hello.html").assertViewName("hello.jsp");

		// method
		initRequest("/user/add", "GET").runService().assertViewName("get.jsp");
		initRequest("/user/add", "POST").runService().assertViewName("post.jsp");
		assertThat(initRequest("/user/add", "PUT").runService().getModelAndView(), is(nullValue()));
		assertThat(this.response.getStatus(), is(405)); // method not allowed

		// param
		initRequest("/user/edit", "POST").addParameter("type", "admin").runService().assertViewName("admin.jsp");
		initRequest("/user/edit", "GET").addParameter("type", "member").runService().assertViewName("member.jsp");
		initRequest("/user/edit").addParameter("type", "xxx").runService().assertViewName("edit.jsp");
		initRequest("/user/edit").runService().assertViewName("not.jsp");
	}

	@RequestMapping
	static class Ctrl1 {
		@RequestMapping("/hello")
		public String suffixpattern() {
			return "hello.jsp";
		}

		@RequestMapping(value = "/user/add", method = RequestMethod.GET)
		public String get() {
			return "get.jsp";
		}

		@RequestMapping(value = "/user/add", method = RequestMethod.POST)
		public String post() {
			return "post.jsp";
		}

		@RequestMapping(value = "/user/edit", params = "type=admin")
		public String paramadmin() {
			return "admin.jsp";
		}

		@RequestMapping(value = "/user/edit", params = "type=member")
		public String paraqmmember() {
			return "member.jsp";
		}

		@RequestMapping(value = "/user/edit", params = "!type")
		public String nottype() {
			return "not.jsp";
		}

		@RequestMapping(value = "/user/edit")
		public String noparam() {
			return "edit.jsp";
		}
	}

	@Test
	public void combine() throws ServletException, IOException {
		setClasses(MemberController.class);
		runService("/member/add").assertViewName("add.jsp");
		runService("/member/edit").assertViewName("edit.jsp");
		runService("/member/delete").assertViewName("delete.jsp");
	}

	@RequestMapping("/member")
	static class MemberController {
		@RequestMapping("/add")
		public String add() {
			return "add.jsp";
		}

		@RequestMapping("/edit")
		public String edit() {
			return "edit.jsp";
		}

		@RequestMapping("/delete")
		public String delete() {
			return "delete.jsp";
		}
	}

	@Test
	public void classonly() throws ServletException, IOException {
		setClasses(Ctrl2.class);
		runService("/member/add").assertViewName("add.jsp");
		runService("/member/edit").assertViewName("edit.jsp");
		runService("/member/delete").assertViewName("delete.jsp");
	}

	@RequestMapping("/member/*")
	static class Ctrl2 {
		@RequestMapping
		public String add() {
			return "add.jsp";
		}

		@RequestMapping
		public String edit() {
			return "edit.jsp";
		}

		@RequestMapping
		public String delete() {
			return "delete.jsp";
		}
	}

	@Test
	public void methodonly() throws ServletException, IOException {
		setClasses(Ctrl3.class, Ctrl4.class);
		runService("/member/add").assertViewName("add.jsp");
		runService("/member/edit").assertViewName("edit.jsp");
		runService("/member/delete").assertViewName("delete.jsp");
		runService("/user/add").assertViewName("add.jsp");
		runService("/user/edit").assertViewName("edit.jsp");
		runService("/user/delete").assertViewName("delete.jsp");
	}

	@RequestMapping
	static class Ctrl3 {
		@RequestMapping("/member/add")
		public String add() {
			return "add.jsp";
		}

		@RequestMapping("/member/edit")
		public String edit() {
			return "edit.jsp";
		}

		@RequestMapping("/member/delete")
		public String delete() {
			return "delete.jsp";
		}
	}

	@Controller
	static class Ctrl4 {
		@RequestMapping("/user/add")
		public String add() {
			return "add.jsp";
		}

		@RequestMapping("/user/edit")
		public String edit() {
			return "edit.jsp";
		}

		@RequestMapping("/user/delete")
		public String delete() {
			return "delete.jsp";
		}
	}

	@Test
	public void inheritance() throws ServletException, IOException {
		setClasses(Sub1.class, Sub2.class, Sub2a.class, Sub3.class, Sub4.class, Sub6.class, Sub6a.class);
		runService("/member/add").assertViewName("add.jsp");
		runService("/user/edit").assertViewName("edit.jsp");
		runService("/user2a/edit").assertViewName("edit2a.jsp");
		runService("/admin/sub").assertViewName("subview.jsp");
		assertThat(runService("/admin/super").getModelAndView(), is(nullValue()));
		runService("/sys/super").assertViewName("subview.jsp"); // 클래스 상속과 인터페이스 구현의 다른 특징!
		runService("/super/add").assertViewName("add.jsp");
		runService("/super2/add").assertViewName("add2.jsp");
	}

	@RequestMapping("/member")
	static class Super1 {
	}

	static class Sub1 extends Super1 {
		@RequestMapping("/add")
		public String add() {
			return "add.jsp";
		}
	}

	static class Super2 {
		@RequestMapping("/edit")
		public String add() {
			return "edit.jsp";
		}
	}

	@RequestMapping("/user")
	static class Sub2 extends Super2 {
	}

	static class Super2a {
		@RequestMapping("/edit")
		public String add() {
			return "edit.jsp";
		}
	}

	@RequestMapping("/user2a")
	static class Sub2a extends Super2a {
		public String add() {
			return "edit2a.jsp";
		}
	}

	@RequestMapping("/adming")
	static class Super3 {
		@RequestMapping(value = "/super", method = RequestMethod.POST)
		public String add() {
			return "superview.jsp";
		}
	}

	@RequestMapping("/admin")
	static class Sub3 extends Super3 {
		@RequestMapping("/sub")
		public String add() {
			return "subview.jsp";
		}
	}

	static class Super4 {
		@RequestMapping(value = "/super")
		public String add() {
			return "superview.jsp";
		}
	}

	@RequestMapping("/sys")
	static class Sub4 extends Super4 {
		@RequestMapping
		public String add() {
			return "subview.jsp";
		}
	}

	@RequestMapping("/super")
	static class Super6 {
		@RequestMapping("/add")
		public String add() {
			return "add.jsp";
		}
	}

	static class Sub6 extends Super6 {
	}

	@RequestMapping("/super2")
	static class Super6a {
		@RequestMapping("/add")
		public String add() {
			return "add.jsp";
		}
	}

	static class Sub6a extends Super6a {
		public String add() {
			return "add2.jsp";
		}
	}

	@Test
	public void interfaces() throws ServletException, IOException {
		setClasses(Cls1.class, Cls2.class, Cls3.class, Cls4.class, Cls6.class);
		runService("/member/add").assertViewName("add.jsp");
		runService("/user/edit").assertViewName("edit.jsp");
		runService("/admin/sub").assertViewName("subview.jsp");
		assertThat(runService("/admin/super").getModelAndView(), is(nullValue()));
		assertThat(runService("/sys/super").getModelAndView(), is(nullValue())); // 클래스 상속과 인터페이스 구현의 다른 특징!
		runService("/sub/add").assertViewName("add.jsp");
	}

	@RequestMapping("/member")
	interface Inf1 {
		public String add();
	}

	static class Cls1 implements Inf1 {
		@RequestMapping("/add")
		public String add() {
			return "add.jsp";
		}
	}

	static interface Inf2 {
		@RequestMapping("/edit")
		public String add();
	}

	@RequestMapping("/user")
	static class Cls2 implements Inf2 {
		public String add() {
			return "edit.jsp";
		}
	}

	@RequestMapping("/adming")
	static interface Inf3 {
		@RequestMapping(value = "/super", method = RequestMethod.POST)
		public String add();
	}

	@RequestMapping("/admin")
	static class Cls3 implements Inf3 {
		@RequestMapping("/sub")
		public String add() {
			return "subview.jsp";
		}
	}

	static interface Inf4 {
		@RequestMapping(value = "/super")
		public String add();
	}

	@RequestMapping("/sys")
	static class Cls4 implements Inf4 {
		@RequestMapping
		public String add() {
			return "subview.jsp";
		}
	}

	@RequestMapping("/sub")
	interface Inf6 {
		@RequestMapping("/add")
		public String add();
	}

	static class Cls6 implements Inf6 {
		public String add() {
			return "add.jsp";
		}
	}
}
