package sample.spring3._04_mail;

import java.util.ArrayList;
import java.util.List;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

public class MailSenderTransaction {
	protected MailSender mailSender;
	protected List<SimpleMailMessage> msgList;

	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}

	public boolean addMsg(SimpleMailMessage msg) {
		if (msgList == null) msgList = new ArrayList<SimpleMailMessage>();
		return msgList.add(msg);
	}

	public void clearMsg() {
		if (msgList != null) msgList.clear();
		msgList = null;
	}

	public int sendAll() {
		SimpleMailMessage[] msgs = msgList.toArray(new SimpleMailMessage[msgList.size()]);
		mailSender.send(msgs);
		return msgs.length;
	}
}
