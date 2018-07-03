package sample.spring3._03_transaction;

/**
 * User
 * level, login, recommend 필드 추가
 * 
 */
public class User {
	String id;
	String name;
	String password;
	Level level;
	int login;
	int recommend;

	public User() {
	}

	public User(String id, String name, String password, Level level, int login, int recommend) {
		this.id = id;
		this.name = name;
		this.password = password;
		this.level = level;
		this.login = login;
		this.recommend = recommend;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public int getLogin() {
		return login;
	}

	public void setLogin(int login) {
		this.login = login;
	}

	public int getRecommend() {
		return recommend;
	}

	public void setRecommend(int recommend) {
		this.recommend = recommend;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + login;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + recommend;
		result = prime * result + ((level == null) ? 0 : level.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		User other = (User) obj;
		if (id == null) {
			if (other.id != null) return false;
		} else if (!id.equals(other.id)) return false;
		if (login != other.login) return false;
		if (name == null) {
			if (other.name != null) return false;
		} else if (!name.equals(other.name)) return false;
		if (password == null) {
			if (other.password != null) return false;
		} else if (!password.equals(other.password)) return false;
		if (recommend != other.recommend) return false;
		if (level != other.level) return false;
		return true;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", password=" + password + ", level=" + level + ", login=" + login + ", recommend=" + recommend + "]";
	}

	public User clone() {
		User user = new User(id, name, password, level, login, recommend);
		return user;
	}

}
