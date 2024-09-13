package kr.gooroom.gpms.common.utils;

public interface LoginTrialStrategy {

    public enum LoginType {
        USERNAME_PASSWORD,
        TOTP,
    }

    public boolean handleLoginTrialCount();
}
