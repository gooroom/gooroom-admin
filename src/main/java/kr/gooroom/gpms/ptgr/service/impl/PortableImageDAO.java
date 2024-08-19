package kr.gooroom.gpms.ptgr.service.impl;

import kr.gooroom.gpms.common.service.dao.SqlSessionMetaDAO;
import kr.gooroom.gpms.ptgr.service.PortableImageVO;
import kr.gooroom.gpms.ptgr.service.PortableImageViewVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository("portableImageDAO")
public class PortableImageDAO extends SqlSessionMetaDAO {
    private static final Logger logger = LoggerFactory.getLogger(PortableImageDAO.class);

    public long createPortableImage(PortableImageVO vo) {
        return sqlSessionMeta.insert("insertPortableImage", vo);
    }

    public long createPortableImageHist(PortableImageVO vo) {
        return sqlSessionMeta.insert("insertPortableImageHist", vo);
    }

    public long createPortableAllImageHist() {
        return sqlSessionMeta.insert("insertPortableAllImageHist");
    }

    public List<PortableImageViewVO> selectPortableImageList () {
        List<PortableImageViewVO> res = null;
        res = sqlSessionMeta.selectList("selectPortableImageList");
        return res;
    }

    public List<PortableImageViewVO> selectPortableImageList (HashMap<String, Object> options) {
        List<PortableImageViewVO> res = null;
        res = sqlSessionMeta.selectList("selectPortableImageListByCondition", options);
        return res;
    }

    public PortableImageVO selectPortableImageByImageId (int imageId) {
        PortableImageVO res = null;
        res = sqlSessionMeta.selectOne("selectPortableImageByImageId", imageId);
        return res;
    }

    public long updatePortableImage (PortableImageVO imageVO) {
        return sqlSessionMeta.update("updatePortableImage", imageVO);
    }
    public long updatePortableImageStatus (HashMap<String, Object> options) {
        return sqlSessionMeta.update("updatePortableImageStatus", options);
    }

    public long removePortableImage (int imageId) {
        return sqlSessionMeta.delete("removePortableImage", imageId);
    }

    public long removePortableImages (HashMap<String, Object> ids) {
        return sqlSessionMeta.delete("removePortableImages", ids);
    }

    public long removeAllPortableImage () {
        return sqlSessionMeta.delete("removeAllPortableImage");
    }

    public long deletePortableImage (int imageId) {
        return sqlSessionMeta.delete("deletePortableImageById", imageId);
    }

    public long deletePortableImageAll () {
        return sqlSessionMeta.delete("deletePortableImageAll");
    }

    public int selectNextPortableImageNumber() {
        return sqlSessionMeta.selectOne("selectNextImageNo" );
    }

    public long  selectPortableImageTotalCount(HashMap<String, Object> options) {
        return sqlSessionMeta.selectOne("selectPortableImageTotalCount", options );
    }

    public long selectPortableImageFilteredCount(HashMap<String, Object> options) {
        return sqlSessionMeta.selectOne("selectPortableImageFilteredCount", options);
    }
}
