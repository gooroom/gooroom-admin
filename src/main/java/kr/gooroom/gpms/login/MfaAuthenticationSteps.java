package kr.gooroom.gpms.login;

import java.util.ArrayList;
import java.util.List;

public class MfaAuthenticationSteps {
    private List<String> requiredSteps;
    private List<String> completedSteps;

    public MfaAuthenticationSteps() {
        requiredSteps = new ArrayList<String>();
        requiredSteps.add("first");
        requiredSteps.add("second");
    }

    public List<String> getCompletedSteps() {
        return completedSteps;
    }

    public void setRequiredSteps(List<String> requiredSteps) {
        this.requiredSteps = requiredSteps;
    }

    public List<String> getRequiredSteps() {
        return requiredSteps;
    }

    public void addCompletedSteps(String step) {
        this.completedSteps.add(step);
    }

    public boolean isCompleted() {
        if(completedSteps.size() == requiredSteps.size()) {
            return true;
        }

        return false;
    }
}
