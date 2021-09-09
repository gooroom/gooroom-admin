package kr.gooroom.gpms.ptgr.util;

import kr.gooroom.gpms.common.GPMSConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class JenkinsUtils {

    private static final Logger logger = LoggerFactory.getLogger(JenkinsUtils.class);

    private String getJenkinsURL (String buildState) {
        String jobUrl = String.format("%s/job/%s/%s", GPMSConstants.PORTABLE_JENKINS_URL, GPMSConstants.PORTABLE_JENKINS_JOBNAME, buildState);
        return String.format("http://%s:%s@%s", GPMSConstants.PORTABLE_JENKINS_USER, GPMSConstants.PORTABLE_JENKINS_TOKEN, jobUrl);
    }

    public void jenkinsBuild() {
        String jenkinsUrl =  getJenkinsURL("build");

        try {
            ProcessBuilder bProcessBuilder = new ProcessBuilder();
            bProcessBuilder.command("curl", "-X", "POST", jenkinsUrl);
            Process bProcess = bProcessBuilder.start();
            bProcess.waitFor(1, TimeUnit.MINUTES);
            bProcess.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void jenkinsBuildWithParameter (ArrayList<String> params) {
        String jenkinsUrl =  getJenkinsURL("buildWithParameters");
        logger.debug(jenkinsUrl);

        try {
            ArrayList<String> commadList = new ArrayList<>();
            commadList.add("curl");
            commadList.add("-X");
            commadList.add("POST");
            commadList.add(jenkinsUrl);
            params.forEach((String val) -> {
                commadList.add("-F");
                commadList.add(val);
            });
            ProcessBuilder bProcessBuilder = new ProcessBuilder();
            bProcessBuilder.command(commadList);
            Process bProcess = bProcessBuilder.start();
            bProcess.waitFor(1, TimeUnit.MINUTES);
            bProcess.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
