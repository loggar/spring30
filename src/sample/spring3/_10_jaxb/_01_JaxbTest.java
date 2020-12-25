package sample.spring3._10_jaxb;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

import sample.spring3._10_jaxb.sqlmap.SqlType;
import sample.spring3._10_jaxb.sqlmap.Sqlmap;

/**
 * JAXB TEST "/sqlmap.xsd" 컴파일로 생성된 sample.spring3._10_jaxb.SqlMap 의 사용
 * 
 * 용어 Marshalling: 바인딩 오브젝트 -> XML UnMarshalling: XML -> 자바 오브젝트
 * 
 */
public class _01_JaxbTest {
	@Test
	public void readSqlmap() throws JAXBException {
		String contextPath = Sqlmap.class.getPackage().getName();
		JAXBContext context = JAXBContext.newInstance(contextPath);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(getClass().getResourceAsStream("TestXml.xml"));

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
