package kr.gooroom.gpms.metrics.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import kr.gooroom.gpms.common.GPMSConstants;
import kr.gooroom.gpms.common.service.ResultVO;
import kr.gooroom.gpms.common.service.StatusVO;
import kr.gooroom.gpms.common.utils.MessageSourceHelper;
import kr.gooroom.gpms.metrics.service.ResourceMetricsService;
import kr.gooroom.gpms.metrics.service.ResourceMetricsVO;

@Service("resourceMetricsService")
public class ResourceMetricsServiceImpl implements ResourceMetricsService {
    private static final Logger logger = LoggerFactory.getLogger(ResourceMetricsServiceImpl.class);

    @Resource(name = "cpuResourceDAO")
    private CpuResourceDAO cpuResourceDAO;

    @Resource(name = "memResourceDAO")
    private MemResourceDAO memResourceDAO;

    @Resource(name = "netResourceDAO")
    private NetResourceDAO netResourceDAO;

    @Resource(name = "diskResourceDAO")
    private DiskResourceDAO diskResourceDAO;

    @Override
    public ResultVO readResourceMetrics(String resourceType) throws Exception {
        ResultVO resultVO = new ResultVO();

        try {
            switch (resourceType) {
                case "cpu":
                    resultVO = readMetrics(cpuResourceDAO);
                    break;
                case "mem":
                    resultVO = readMetrics(memResourceDAO);
                    break;
                case "net":
                    resultVO = readMetrics(netResourceDAO);
                    break;
                case "disk":
                    resultVO = readMetrics(diskResourceDAO);
                    break;
                default:
                    resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                            MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
                    logger.error("Invalid resourceType: {}", resourceType);
                    break;
            }
        } catch (Exception ex) {
            logger.error("Error in readResourceMetrics: {}", ex.toString());
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
        }
        return resultVO;
    }

    private ResultVO readMetrics(ResourceMetricsDAO dao) throws Exception {
        ResultVO resultVO = new ResultVO();
        try {
            List<? extends ResourceMetricsVO> re = dao.selectResourceList();

            if (re != null && !re.isEmpty()) {
                try {
                    ResourceMetricsVO[] row = re.toArray(new ResourceMetricsVO[0]);

                    logger.info("cpu resource array data : {}", row);
                    resultVO.setData(row);
                } catch (Exception ex) {
                    resultVO.setData(new Object[0]);
                    resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR, "Casting Error"));
                }

            } else {
                resultVO.setData(new Object[0]);
                resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SELECTERROR,
                        MessageSourceHelper.getMessage("system.common.noselectdata")));
            }

        } catch (Exception ex) {
            logger.error("Error in readMetrics: {}, {}", GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR), ex.toString());
            resultVO.setStatus(new StatusVO(GPMSConstants.MSG_FAIL, GPMSConstants.CODE_SYSERROR,
                    MessageSourceHelper.getMessage(GPMSConstants.MSG_SYSERROR)));
        }

        return resultVO;
    }
}
