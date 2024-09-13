package kr.gooroom.gpms.health.service.impl;

import java.sql.SQLException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.dao.SqlSessionMetaDAO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.health.constant.GPMSModuleActionType;
import kr.gooroom.gpms.health.constant.GPMSModuleType;
import kr.gooroom.gpms.health.service.GPMSModuleHealthVO;

@Repository("gpmsModuleHealthDAO")
public class GPMSModuleHealthDAO extends SqlSessionMetaDAO {
    private static final Logger logger = LoggerFactory.getLogger(GPMSModuleHealthDAO.class);

    public GPMSModuleHealthVO selectGPMSModuleHealth(String moduleType) throws SQLException {
        GPMSModuleHealthVO moduleHealthVO = new GPMSModuleHealthVO(moduleType);
        try {
            moduleHealthVO = sqlSessionMeta.selectOne("selectGPMSModuleHealth", moduleType);
        } catch (Exception ex) {
            logger.error("error in selectGPMSModuleHealth : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
        }
        return moduleHealthVO;
    }

    public long updateLastModuleCommand(String moduleType, String lastCommand) {
        try {
            if (!GPMSModuleType.GPMS_MODULE_LIST.contains(moduleType)
                    || !GPMSModuleActionType.GPMS_ACTION_LIST.contains(lastCommand))
                return 0;

            HashMap<String, String> map = new HashMap<>();
            String remapLastCommand = GPMSModuleActionType.RESTART.equals(lastCommand) ? GPMSModuleActionType.START
                    : lastCommand;

            map.put("moduleType", moduleType);
            map.put("lastCommand", remapLastCommand);
            return sqlSessionMeta.update("updateLastModuleCommand", map);
        } catch (Exception ex) {
            logger.error("error in updateLastModuleCommand : {}, {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
            return 0;
        }
    }
}
