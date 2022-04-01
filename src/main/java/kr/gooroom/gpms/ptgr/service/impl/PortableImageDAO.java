package kr.gooroom.gpms.ptgr.service.impl;

import kr.gooroom.gpms.common.service.dao.SqlSessionMetaDAO;
import kr.gooroom.gpms.ptgr.service.PortableImageVO;
import kr.gooroom.gpms.ptgr.service.PortableImageViewVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

@Repository("portableImageDAO")
public class PortableImageDAO extends SqlSessionMetaDAO {
    private static final Logger logger = LoggerFactory.getLogger(PortableImageDAO.class);

    public long createPortableImage(PortableImageVO vo) throws SQLException {
        return (long) sqlSessionMeta.insert("insertPortableImage", vo);
    }

    public long createPortableImageHist(PortableImageVO vo) throws SQLException {
        return (long) sqlSessionMeta.insert("insertPortableImageHist", vo);
    }

    public long createPortableAllImageHist() throws SQLException {
        return (long) sqlSessionMeta.insert("insertPortableAllImageHist");
    }

    public List<PortableImageViewVO> selectPortableImageList () throws SQLException {
        List<PortableImageViewVO> res = null;
        res = sqlSessionMeta.selectList("selectPortableImageList");
        return res;
    }

    public List<PortableImageViewVO> selectPortableImageList (HashMap<String, Object> options) throws SQLException {
        List<PortableImageViewVO> res = null;
        res = sqlSessionMeta.selectList("selectPortableImageListByCondition", options);
        return res;
    }

    public PortableImageVO selectPortableImageByImageId (int imageId) throws SQLException {
        PortableImageVO res = null;
        res = sqlSessionMeta.selectOne("selectPortableImageByImageId", imageId);
        return res;
    }

    public long updatePortableImage (PortableImageVO imageVO) throws SQLException {
        return (long) sqlSessionMeta.update("updatePortableImage", imageVO);
    }
    public long updatePortableImageStatus (HashMap<String, Object> options) throws SQLException {
        return (long) sqlSessionMeta.update("updatePortableImageStatus", options);
    }

    public long removePortableImage (int imageId) throws SQLException {
        return (long) sqlSessionMeta.delete("removePortableImage", imageId);
    }

    public long removePortableImages (HashMap<String, Object> ids) throws SQLException {
        return (long) sqlSessionMeta.delete("removePortableImages", ids);
    }

    public long removeAllPortableImage () throws SQLException {
        return (long) sqlSessionMeta.delete("removeAllPortableImage");
    }

    public long deletePortableImage (int imageId) throws SQLException {
        return (long) sqlSessionMeta.delete("deletePortableImageById", imageId);
    }

    public long deletePortableImageAll () throws SQLException {
        return (long) sqlSessionMeta.delete("deletePortableImageAll");
    }

    public int selectNextPortableImageNumber() throws SQLException {
        return sqlSessionMeta.selectOne("selectNextImageNo" );
    }

    public long  selectPortableImageTotalCount(HashMap<String, Object> options) throws SQLException {
        return sqlSessionMeta.selectOne("selectPortableImageTotalCount", options );
    }

    public long selectPortableImageFilteredCount(HashMap<String, Object> options) throws SQLException {
        return sqlSessionMeta.selectOne("selectPortableImageFilteredCount", options);
    }
}
