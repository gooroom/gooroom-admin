package kr.gooroom.gpms.ptgr.service.impl;

import kr.gooroom.gpms.common.service.dao.SqlSessionMetaDAO;
import kr.gooroom.gpms.ptgr.service.PortableJobVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository("portableJobDAO")
public class PortableJobDAO extends SqlSessionMetaDAO {
    private static final Logger logger = LoggerFactory.getLogger(PortableJobDAO.class);

    public long createPortableJob (PortableJobVO vo) {
        return sqlSessionMeta.insert("insertPortableJob", vo);
    }

    public long deletePortableJobByImageId (int imageId) {
        return sqlSessionMeta.delete("deletePortableJobByImageId");
    }

    public long deleteAllPortableJob () {
        return sqlSessionMeta.delete("deleteAllPortableJob");
    }

    public PortableJobVO selectPortableJobByImageId (int imageId)  {
        PortableJobVO res = null;
        res = sqlSessionMeta.selectOne("selectPortableJobByImageId", imageId);
        return res;
    }

    public long updatePortableJob (PortableJobVO jobVO)  {
       return sqlSessionMeta.update ("updatePortableJob", jobVO);
    }
}
