package sample.spring3._04_mail;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

/**
 * Spring MailSender 의 아무행동하지 않는 구현체 테스트 발송용 목적.
 * 
 */
public class MailSenderDummy implements MailSender {
	String host;
	int port;
	String username;
	String password;
	String defaultEncoding;

	@Override
	public void send(SimpleMailMessage arg0) throws MailException {
		for (String to : arg0.getTo()) {
			System.out.println(to);
		}
	}

	@Override
	public void send(SimpleMailMessage[] arg0) throws MailException {
		for (SimpleMailMessage msg : arg0) {
			for (String to : msg.getTo()) {
				System.out.println(to);
			}
		}
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDefaultEncoding() {
		return defaultEncoding;
	}

	public void setDefaultEncoding(String defaultEncoding) {
		this.defaultEncoding = defaultEncoding;
	}

}
