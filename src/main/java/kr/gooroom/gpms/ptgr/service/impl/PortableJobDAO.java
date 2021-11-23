package kr.gooroom.gpms.ptgr.service.impl;

import kr.gooroom.gpms.common.service.dao.SqlSessionMetaDAO;
import kr.gooroom.gpms.ptgr.service.PortableJobVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;

@Repository("portableJobDAO")
public class PortableJobDAO extends SqlSessionMetaDAO {
    private static final Logger logger = LoggerFactory.getLogger(PortableJobDAO.class);

    public long createPortableJob (PortableJobVO vo) throws SQLException {
        return (long) sqlSessionMeta.insert("insertPortableJob", vo);
    }

    public long deletePortableJobByImageId (int imageId) throws SQLException {
        return (long) sqlSessionMeta.delete("deletePortableJobByImageId");
    }

    public long deleteAllPortableJob () throws SQLException {
        return (long) sqlSessionMeta.delete("deleteAllPortableJob");
    }

    public PortableJobVO selectPortableJobByImageId (int imageId)  {
        PortableJobVO res = null;
        res = sqlSessionMeta.selectOne("selectPortableJobByImageId", imageId);
        return res;
    }

    public long updatePortableJob (PortableJobVO jobVO)  {
       return (long) sqlSessionMeta.update ("updatePortableJob", jobVO);
    }
}
