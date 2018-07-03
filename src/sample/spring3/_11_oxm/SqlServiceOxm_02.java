package sample.spring3._11_oxm;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.Unmarshaller;

import sample.spring3._09_sqlmap.SqlRetrievalFailureException;
import sample.spring3._09_sqlmap.SqlService;
import sample.spring3._10_jaxb.SqlReader;
import sample.spring3._10_jaxb.SqlRegistry;
import sample.spring3._10_jaxb.SqlRegistryHashMap;
import sample.spring3._10_jaxb.SqlServiceImpl;
import sample.spring3._10_jaxb.UserDao;
import sample.spring3._10_jaxb.sqlmap.SqlType;
import sample.spring3._10_jaxb.sqlmap.Sqlmap;

public class SqlServiceOxm_02 implements SqlService {
	private final SqlServiceImpl baseSqlService = new SqlServiceImpl();

	private final OxmSqlReader oxmSqlReader = new OxmSqlReader();
	private SqlRegistry sqlRegistry = new SqlRegistryHashMap();

	public void setSqlRegistry(SqlRegistry sqlRegistry) {
		this.sqlRegistry = sqlRegistry;
	}

	public void setUnmarshaller(Unmarshaller unmarshaller) {
		this.oxmSqlReader.setUnmarshaller(unmarshaller);
	}

	public void setSqlmap(Resource sqlmap) {
		this.oxmSqlReader.setSqlmap(sqlmap);
	}

	@PostConstruct
	public void loadSql() {
		this.baseSqlService.setSqlReader(this.oxmSqlReader);
		this.baseSqlService.setSqlRegistry(this.sqlRegistry);

		this.baseSqlService.loadSql();
	}

	public String getSql(String key) throws SqlRetrievalFailureException {
		return this.baseSqlService.getSql(key);
	}

	private class OxmSqlReader implements SqlReader {
		private Unmarshaller unmarshaller;

		// default sqlmap-resource
		private Resource sqlmap = new ClassPathResource("sqlmap.xml", UserDao.class);

		public void setUnmarshaller(Unmarshaller unmarshaller) {
			this.unmarshaller = unmarshaller;
		}

		// sqlmap-resource DI
		public void setSqlmap(Resource sqlmap) {
			this.sqlmap = sqlmap;
		}

		public void read(SqlRegistry sqlRegistry) {
			try {
				Source source = new StreamSource(sqlmap.getInputStream());
				Sqlmap sqlmap = (Sqlmap) this.unmarshaller.unmarshal(source);
				for (SqlType sql : sqlmap.getSql()) {
					sqlRegistry.registerSql(sql.getKey(), sql.getValue());
				}
			} catch (IOException e) {
				throw new IllegalArgumentException(sqlmap.getFilename() + "을 가져올 수 없습니다", e);
			}
		}
	}
}
