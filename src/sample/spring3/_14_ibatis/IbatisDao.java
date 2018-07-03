package sample.spring3._14_ibatis;

import java.util.List;

import org.springframework.orm.ibatis.SqlMapClientTemplate;

import sample.spring3._13_jdbc.Member;

import com.ibatis.sqlmap.client.SqlMapClient;

public class IbatisDao {
	private SqlMapClientTemplate sqlMapClientTemplate;

	public void setSqlMapClient(SqlMapClient sqlMapClient) {
		sqlMapClientTemplate = new SqlMapClientTemplate(sqlMapClient);
	}

	public void insert(Member m) {
		sqlMapClientTemplate.insert("insertMember", m);
	}

	public void deleteAll() {
		sqlMapClientTemplate.delete("deleteMemberAll");
	}

	public Member select(int id) {
		return (Member) sqlMapClientTemplate.queryForObject("findMemberById", id);
	}

	@SuppressWarnings("unchecked")
	public List<Member> selectAll() {
		return sqlMapClientTemplate.queryForList("findMembers");
	}
}
