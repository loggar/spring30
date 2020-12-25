package sample.spring3._10_jaxb;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import sample.spring3._09_sqlmap.SqlRetrievalFailureException;
import sample.spring3._09_sqlmap.SqlService;
import sample.spring3._10_jaxb.sqlmap.SqlType;
import sample.spring3._10_jaxb.sqlmap.Sqlmap;

public class SqlServiceXml implements SqlService {
	private Map<String, String> sqlMap = new HashMap<String, String>();

	private String sqlmapFile;

	public void setSqlmapFile(String sqlmapFile) {
		this.sqlmapFile = sqlmapFile;
	}

	@PostConstruct
	public void loadSql() {
		String contextPath = Sqlmap.class.getPackage().getName();
		try {
			JAXBContext context = JAXBContext.newInstance(contextPath);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			InputStream is = SqlServiceXml.class.getResourceAsStream(this.sqlmapFile);
			Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(is);

			for (SqlType sql : sqlmap.getSql()) {
				sqlMap.put(sql.getKey(), sql.getValue());
			}
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

	public String getSql(String key) throws SqlRetrievalFailureException {
		String sql = sqlMap.get(key);
		if (sql == null)
			throw new SqlRetrievalFailureException(key + " : cannot find sql");
		return sql;
	}
}
