package kr.gooroom.gpms;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;

public class LoggedUserListener implements ApplicationListener<InteractiveAuthenticationSuccessEvent> {
//	@Autowired
//	private HttpSession session;

	@Override
	public void onApplicationEvent(InteractiveAuthenticationSuccessEvent event) {
//        session.setAttribute("key", "value");

	}
}
