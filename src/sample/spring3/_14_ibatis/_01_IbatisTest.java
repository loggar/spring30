package sample.spring3._14_ibatis;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import sample.spring3._13_jdbc.Member;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "ibatistest-context.xml")
public class _01_IbatisTest {
	@Autowired IbatisDao dao;

	@Test
	public void ibatis() {
		dao.deleteAll();
		dao.insert(new Member(5, "iBatis", 1.2));
		dao.insert(new Member(6, "sqlMap", 3.3));

		Member m = dao.select(5);
		assertThat(m.getId(), is(5));
		assertThat(m.getName(), is("iBatis"));
		assertThat(m.getPoint(), is(1.2));

		List<Member> ms = dao.selectAll();
		assertThat(ms.size(), is(2));

	}
}
