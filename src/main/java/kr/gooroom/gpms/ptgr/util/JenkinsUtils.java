package kr.gooroom.gpms.ptgr.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.gooroom.gpms.common.GPMSConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class JenkinsUtils {

    private static final Logger logger = LoggerFactory.getLogger(JenkinsUtils.class);

    private String getJenkinsURL (String jobName, String buildState) {
        String jobUrl = String.format("%s/job/%s/%s", GPMSConstants.PORTABLE_JENKINS_URL, jobName, buildState);
        return String.format("http://%s:%s@%s", GPMSConstants.PORTABLE_JENKINS_USER, GPMSConstants.PORTABLE_JENKINS_TOKEN, jobUrl);
    }

    public void jenkinsBuild() {
        String res;
        String jenkinsUrl =  getJenkinsURL(GPMSConstants.PORTABLE_JENKINS_JOBNAME, "build");
        try {
            ProcessBuilder bProcessBuilder = new ProcessBuilder();
            bProcessBuilder.command("curl", "-X", "POST", jenkinsUrl);
            Process bProcess = bProcessBuilder.start();
            bProcess.waitFor(1, TimeUnit.MINUTES);
            bProcess.destroy();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void jenkinsBuildWithParameter (ArrayList<String> params) {
        String jenkinsUrl =  getJenkinsURL(GPMSConstants.PORTABLE_JENKINS_JOBNAME, "buildWithParameters");
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
            logger.debug(bProcessBuilder.toString());
            Process bProcess = bProcessBuilder.start();
            bProcess.waitFor(1, TimeUnit.MINUTES);
            bProcess.destroy();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    public String jenkinsGetJobDuration(int jobNo) {
        String res = "";
        String jenkinsUrl =  getJenkinsURL(GPMSConstants.PORTABLE_JENKINS_JOBNAME, jobNo + "/api/json");

        try {
            ProcessBuilder bProcessBuilder = new ProcessBuilder();
            bProcessBuilder.command("curl", "-X", "POST", jenkinsUrl);
            Process bProcess = bProcessBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(bProcess.getInputStream()));
            res = reader.readLine();

            bProcess.waitFor(1, TimeUnit.MINUTES);
            bProcess.destroy();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return res;
    }

    public String jenkinsRemoveFile (String id) {
        String res = "";
        String jenkinsUrl =  getJenkinsURL(GPMSConstants.PORTABLE_JENKINS_REMOVE_JOBNAME, "buildWithParameters");
        try {
            ArrayList<String> commadList = new ArrayList<>();
            commadList.add("curl");
            commadList.add("-X");
            commadList.add("POST");
            commadList.add(jenkinsUrl);
            commadList.add("-F");
            commadList.add("imageIds="+id);

            ProcessBuilder bProcessBuilder = new ProcessBuilder();
            bProcessBuilder.command(commadList);
            Process bProcess = bProcessBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(bProcess.getInputStream()));
            res = reader.readLine();

            bProcess.waitFor(1, TimeUnit.MINUTES);
            bProcess.destroy();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return res;
    }

    public void jenkinsGetJobDurationTest () {

        String json = "{\"_class\":\"hudson.model.FreeStyleBuild\",\"actions\":[{\"_class\":\"hudson.model.ParametersAction\",\"parameters\":[{\"_class\":\"hudson.model.PasswordParameterValue\",\"name\":\"isoPassword\"},{\"_class\":\"hudson.model.FileParameterValue\",\"name\":\"root.pem\"},{\"_class\":\"hudson.model.FileParameterValue\",\"name\":\"cert.pem\"},{\"_class\":\"hudson.model.FileParameterValue\",\"name\":\"private.key\"},{\"_class\":\"hudson.model.StringParameterValue\",\"name\":\"serverUrl\",\"value\":\"https://ptgr-gpms.gooroom.kr/gpms/portable/updateImageList\"},{\"_class\":\"hudson.model.StringParameterValue\",\"name\":\"imageId\",\"value\":\"270\"},{\"_class\":\"hudson.model.StringParameterValue\",\"name\":\"certId\",\"value\":\"270\"},{\"_class\":\"hudson.model.StringParameterValue\",\"name\":\"checkSum\",\"value\":\"\"},{\"_class\":\"hudson.model.StringParameterValue\",\"name\":\"certDeleteUrl\",\"value\":\"https://ptgr-gpms.gooroom.kr/gpms/portable/removeCert\"},{\"_class\":\"hudson.model.StringParameterValue\",\"name\":\"userId\",\"value\":\"ddoya\"},{\"_class\":\"hudson.model.PasswordParameterValue\",\"name\":\"userPassword\"}]},{\"_class\":\"hudson.model.CauseAction\",\"causes\":[{\"_class\":\"hudson.model.Cause$UserIdCause\",\"shortDescription\":\"Started by user Jenkins Debian Glue\",\"userId\":\"jenkins-debian-glue\",\"userName\":\"Jenkins Debian Glue\"}]},{},{\"_class\":\"org.jenkinsci.plugins.displayurlapi.actions.RunDisplayAction\"}],\"artifacts\":[],\"building\":false,\"description\":null,\"displayName\":\"#96\",\"duration\":49099,\"estimatedDuration\":49805,\"executor\":null,\"fullDisplayName\":\"portable-iso-builder #96\",\"id\":\"96\",\"keepLog\":false,\"number\":96,\"queueId\":873,\"result\":\"SUCCESS\",\"timestamp\":1635838560228,\"url\":\"http://gooroom.dscloud.me:8132/job/portable-iso-builder/96/\",\"builtOn\":\"\",\"changeSet\":{\"_class\":\"hudson.scm.EmptyChangeLogSet\",\"items\":[],\"kind\":null},\"culprits\":[]}";

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JenkinsJob job = objectMapper.readValue(json, JenkinsJob.class);
            System.out.println(job.getEstimatedDuration());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }
}
