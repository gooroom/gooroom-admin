package kr.gooroom.gpms.login;

import java.io.Serializable;

@SuppressWarnings("serial")
public class OtpDataVO implements Serializable {
    private String secret;
    private String url;

    //Setter
    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    //Getter
    public String getSecret() {
        return secret;
    }

    public String getUrl() {
        return url;
    }
}
