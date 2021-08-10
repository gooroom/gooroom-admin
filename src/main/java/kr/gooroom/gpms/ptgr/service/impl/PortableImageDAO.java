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

    public List<PortableImageViewVO> selectPortableImageList () throws SQLException {
        List<PortableImageViewVO> res = null;
        res = sqlSessionMeta.selectList("selectPortableImageList");
        return res;
    }

    public List<PortableImageVO> selectPortableImageList (HashMap<String, Object> options) throws SQLException {
        List<PortableImageVO> res = null;
        res = sqlSessionMeta.selectList("selectPortableImageListByCondition", options);
        return res;
    }

    public long updatePortableImage (PortableImageVO imageVO) throws SQLException {
        return (long) sqlSessionMeta.update("updatePortableImage", imageVO);
    }
    public long updatePortableImageStatus (HashMap<String, Object> options) throws SQLException {
        return (long) sqlSessionMeta.update("updatePortableImageStatus", options);
    }
    public long deletePortableImage (HashMap<String, Object> ids) throws SQLException {
        return (long) sqlSessionMeta.delete("deletePortableImage", ids);
    }

    public long deleteAllPortableImage () throws SQLException {
        return (long) sqlSessionMeta.delete("deleteAllPortableImage");
    }

    public int selectNextPortableImageNumber() throws SQLException {
        return sqlSessionMeta.selectOne("selectNextImageNo" );
    }

    public int selectPortableImageCount() throws SQLException {
        return sqlSessionMeta.selectOne("selectNextImageCount" );
    }
}
