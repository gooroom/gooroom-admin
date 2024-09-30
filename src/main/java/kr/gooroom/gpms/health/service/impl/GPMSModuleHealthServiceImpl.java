package kr.gooroom.gpms.health.service.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.health.constant.GPMSModuleActionType;
import kr.gooroom.gpms.health.constant.GPMSModuleStatusType;
import kr.gooroom.gpms.health.constant.GPMSModuleType;
import kr.gooroom.gpms.health.service.GPMSModuleHealthVO;
import kr.gooroom.gpms.health.service.GPMSModuleStatusVO;
import kr.gooroom.gpms.health.service.GPMSMoudleHealthService;

@Service("gpmsModuleHealthService")
public class GPMSModuleHealthServiceImpl implements GPMSMoudleHealthService {
    private class ScriptResult {
        private boolean success;
        private String result;

        public ScriptResult(boolean success, String result) {
            this.success = success;
            this.result = result;
        }
    }

    private static class RefreshDelayHandler {
        private TimerTask lastTimerTask;
        private boolean wait = false;

        public void waitModuleStatus() {
            Timer timer = new Timer();
            if (lastTimerTask != null)
                lastTimerTask.cancel();
            wait = true;
            lastTimerTask = new TimerTask() {
                @Override
                public void run() {
                    wait = false;
                }
            };
            // millisecond
            timer.schedule(lastTimerTask, REFRESH_RATE * 1000);
        }
    }

    // second
    private static final int SCRIPT_WAIT_TIME = 3;
    private static final int REFRESH_RATE = 10;

    private static final String DEFAULT_SHELL = "/bin/bash";
    private static final String START_SCRIPT = "startup.sh";
    private static final String SHUTDOWN_SCRIPT = "shutdown.sh";
    private static final Map<String, RefreshDelayHandler> DelayHandler = Collections.unmodifiableMap( new HashMap<String,RefreshDelayHandler>(){{
        put(GPMSModuleType.GKM, new RefreshDelayHandler());
        put(GPMSModuleType.GLM, new RefreshDelayHandler());
        put(GPMSModuleType.GRM, new RefreshDelayHandler());
    }});

    @Resource(name = "gpmsModuleHealthDAO")
    private GPMSModuleHealthDAO gpmsModuleHealthDAO;

    private ScriptResult runScript(String[] cmd) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        Process process = pb.start();
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String line = "";
        String result = "";
        String error = "";
        while ((line = br.readLine()) != null) {
            result = result + line;
        }

        while ((line = errorReader.readLine()) != null) {
            error = error + line;
        }

        process.getInputStream().close();
        process.waitFor(SCRIPT_WAIT_TIME, TimeUnit.SECONDS);

        if (process != null) {
            process.destroy();
        }

        if (!error.equals("")) {
            return new ScriptResult(false, error);
        }
        return new ScriptResult(true, result);
    }

    private String getScriptPath(String moduleType, String moduleActionType) {
        if (!GPMSModuleType.GPMS_MODULE_LIST.contains(moduleType.toLowerCase())
                || !GPMSModuleActionType.GPMS_ACTION_LIST.contains(moduleActionType.toLowerCase()))
            return "";

        String scriptType = "";

        if (moduleActionType.equals(GPMSModuleActionType.START)) {
            scriptType = START_SCRIPT;
        } else if (moduleActionType.equals(GPMSModuleActionType.SHUTDOWN)) {
            scriptType = SHUTDOWN_SCRIPT;
        }

        return GPMSConstants.GPMS_INSTALL_PATH + "app/tomcat_" + moduleType.toLowerCase() + "/bin/" + scriptType;
    }

    public ResultVO getGPMSModuleStatus() throws Exception {
        StatusVO statusVO = new StatusVO();
        ResultVO resultVO = new ResultVO();
        List<Object> resultVOData = new ArrayList<Object>();
        try {
            ResultVO[] serviceResultVO = { getGPMSModuleStatus("gkm"), getGPMSModuleStatus("grm"),
                    getGPMSModuleStatus("glm") };

            Stream<ResultVO> resultStream = Arrays.stream(serviceResultVO);

            resultStream.forEach(res -> {
                if(res.getData().length>0){
                    resultVOData.add(res.getData()[0]);
                }
            });
            statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SUCCESS));
        } catch (Exception ex) {
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
        }

        resultVO.setStatus(statusVO);
        resultVO.setData(resultVOData.toArray());
        return resultVO;
    }

    public ResultVO getGPMSModuleStatus(String moduleType) throws Exception {
        StatusVO statusVO = new StatusVO();
        ResultVO resultVO = new ResultVO();
        List<Object> resultVOData = new ArrayList<Object>();

        GPMSModuleHealthVO res;

        try {
            res = gpmsModuleHealthDAO.selectGPMSModuleHealth(moduleType);
            Date date = res.getLastActivatedTime();

            long lastActivatedTime = OffsetDateTime.ofInstant(date.toInstant(), ZoneId.of("UTC")).toEpochSecond();
            long now = OffsetDateTime.now(ZoneId.of("UTC")).toEpochSecond();
            long diffSec = (now - lastActivatedTime);

            statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_SELECT,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SUCCESS));

            String status = "";
            if (diffSec < REFRESH_RATE && res.getModuleActionType().equals(GPMSModuleActionType.START)) {
                status = GPMSModuleStatusType.ONLINE;
            } else if (diffSec > REFRESH_RATE && res.getModuleActionType().equals(GPMSModuleActionType.SHUTDOWN)) {
                status = GPMSModuleStatusType.OFFLINE;
            } else if (DelayHandler.get(moduleType.toLowerCase()).wait) {
                if (res.getModuleActionType().equals(GPMSModuleActionType.START)
                        || res.getModuleActionType().equals(GPMSModuleActionType.RESTART))
                    status = GPMSModuleStatusType.ONLINE;
                else if (res.getModuleActionType().equals(GPMSModuleActionType.SHUTDOWN))
                    status = GPMSModuleStatusType.OFFLINE;
            } else {
                status = GPMSModuleStatusType.ERROR;
            }

            GPMSModuleStatusVO statusResponse = new GPMSModuleStatusVO(moduleType, res.getModuleActionType(), status,
                    res.getLastActivatedTime());
            resultVOData.add(statusResponse);
        }

        catch (Exception ex) {
            statusVO.setMessage(ex.toString());
        }

        resultVO.setStatus(statusVO);
        resultVO.setData(resultVOData.toArray());
        return resultVO;
    }

    @Override
    public StatusVO turnGPMSModule(String moduleType, String moduleActionType) throws Exception {

        StatusVO statusVO = new StatusVO();
        if (!GPMSModuleType.GPMS_MODULE_LIST.contains(moduleType.toLowerCase())
                || !GPMSModuleActionType.GPMS_ACTION_LIST.contains(moduleActionType.toLowerCase())) {
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR));
            return statusVO;
        }
        List<String[]> cmdList = new ArrayList<>();

        if (moduleActionType.equals(GPMSModuleActionType.RESTART)) {
            cmdList.add(new String[] { DEFAULT_SHELL, "-c", getScriptPath(moduleType, GPMSModuleActionType.SHUTDOWN) });
            cmdList.add(new String[] { DEFAULT_SHELL, "-c", getScriptPath(moduleType, GPMSModuleActionType.START) });
        } else {
            cmdList.add(new String[] { DEFAULT_SHELL, "-c", getScriptPath(moduleType, moduleActionType) });
        }

        if (gpmsModuleHealthDAO.updateLastModuleCommand(moduleType.toLowerCase(), moduleActionType) == 0) {
            statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_UPDATEERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_FAIL));
        } else {
            statusVO.setResultInfo(GPMSConstants.MSG_SUCCESS, GPMSConstants.CODE_UPDATE,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SUCCESS));
            DelayHandler.get(moduleType.toLowerCase()).waitModuleStatus();
        }

        for (String[] cmd : cmdList) {
            ScriptResult scriptResult = runScript(cmd);
            if (!moduleActionType.equals(GPMSModuleActionType.RESTART) && !scriptResult.success) {
                statusVO.setResultInfo(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                        MessageSourceHelper.getMessage(GPMSConstants.MSG_FAIL));
                return statusVO;
            }
        }

        return statusVO;
    }
}
