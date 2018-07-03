package sample.spring3._11_oxm;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.XmlMappingException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import sample.spring3._10_jaxb._01_JaxbTest;
import sample.spring3._10_jaxb.sqlmap.SqlType;
import sample.spring3._10_jaxb.sqlmap.Sqlmap;

/**
 * org.springframework.oxm.Unmarshaller 를 bean 으로 등록하여 xml 로부터 sqlmap 을 생성해낸다.
 * JAXB Unmarshaller 가 아닌 Castor Unmarshaller 를 DI 한 테스트.
 * Unmarshaller 의 구현체를 DI 하는 context code 만 다르기때문에 테스트 코드는 _01_OxmTest 과 동일하다.
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "test-unmarshaller_02.xml")
public class _02_OxmTest {
	@Autowired Unmarshaller unmarshaller;

	@Test
	public void readSqlmap() throws XmlMappingException, IOException {
		Source xmlSource = new StreamSource(_01_JaxbTest.class.getResourceAsStream("TestXml.xml"));
		Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(xmlSource);

		List<SqlType> sqlList = sqlmap.getSql();

		assertThat(sqlList.size(), is(3));
		assertThat(sqlList.get(0).getKey(), is("add"));
		assertThat(sqlList.get(0).getValue(), is("insert"));
		assertThat(sqlList.get(1).getKey(), is("get"));
		assertThat(sqlList.get(1).getValue(), is("select"));
		assertThat(sqlList.get(2).getKey(), is("delete"));
		assertThat(sqlList.get(2).getValue(), is("delete"));
	}
}
