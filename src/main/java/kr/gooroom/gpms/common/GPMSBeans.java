package kr.gooroom.gpms.common;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class GPMSBeans {
	
	@Bean
	public JavaMailSender getJavaMailSender() {
	    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
	    
	    //mailSender.setProtocol("smtp");
	    mailSender.setHost(GPMSConstants.CFG_MAIL_HOST);
	    mailSender.setPort(Integer.parseInt(GPMSConstants.CFG_MAIL_PORT));
	     
	    mailSender.setUsername(GPMSConstants.CFG_MAIL_AUTH_USERNAME);
	    mailSender.setPassword(GPMSConstants.CFG_MAIL_AUTH_PASSWORD);
	     
	    Properties props = mailSender.getJavaMailProperties();
	    props.put("mail.transport.protocol", "smtp");
	    props.put("mail.smtp.auth", "true");
	    props.put("mail.smtp.starttls.enable", "true");
	    props.put("mail.debug", "false");
	    if(GPMSConstants.CFG_MAIL_USESSL.equalsIgnoreCase(GPMSConstants.GUBUN_YES)) {
	    	props.put("mail.smtp.ssl.trust", GPMSConstants.CFG_MAIL_HOST);
	    }	    
	    
	    return mailSender;
	}

}
