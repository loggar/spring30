package sample.spring3._23_test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "context.xml", "context2.xml" })
public class TestContextTest3 {
	@Test
	public void test1() {

	}

	@Test
	public void test2() {

	}

}
