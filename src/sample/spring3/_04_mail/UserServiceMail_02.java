package sample.spring3._04_mail;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * Spring 의 MailSender 의 구현체중 기본적으로 JavaMail 을사용해 메일 기능을 제공하는 JavaMailSenderImpl 클래스를 이용한 mail service
 * 
 */
public class UserServiceMail_02 {
	protected void sendMailForLevelUpgradeInform(User user) {
		String host = "localhost";
		int port = 587;
		String username = "mailuser";
		String password = " mailuserpwd";
		String defaultEncoding = "utf-8";

		String from = "abc@webnlog.com";
		String subject = "Level Upgrade Infom";
		String text = user.getName() + " 님의 등급이 " + user.getLevel().name() + " 으로 Upgrade 되었습니다.";

		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(host);
		mailSender.setPort(port);
		mailSender.setUsername(username);
		mailSender.setPassword(password);
		mailSender.setDefaultEncoding(defaultEncoding);

		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setTo(user.getEmail());
		msg.setFrom(from);
		msg.setSubject(subject);
		msg.setText(text);

		mailSender.send(msg);
	}
}
