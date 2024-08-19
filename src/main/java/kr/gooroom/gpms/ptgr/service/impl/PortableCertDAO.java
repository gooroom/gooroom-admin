package kr.gooroom.gpms.ptgr.service.impl;

import kr.gooroom.gpms.common.service.dao.SqlSessionMetaDAO;
import kr.gooroom.gpms.ptgr.service.PortableCertVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository("portableCertDAO")
public class PortableCertDAO extends SqlSessionMetaDAO {

    private static final Logger logger = LoggerFactory.getLogger(PortableCertDAO.class);

    public long createPortableCert(PortableCertVO vo) {
        return sqlSessionMeta.insert("insertPortableCert", vo);
    }

    public PortableCertVO selectPortableCert(String certId) {
        PortableCertVO res = null;
        res = sqlSessionMeta.selectOne("selectPortableCert", certId);
        return res;
    }

    public long updatePortableCert(PortableCertVO vo) {
        return sqlSessionMeta.update("updatePortableCert", vo);
    }

    public long deletePortableCert(int certId) {
        return sqlSessionMeta.delete("deletePortableCert", certId);
    }

    public long deletePortableCertAll() {
        return sqlSessionMeta.delete("deletePortableCertAll");
    }

    public int selectNextPortableCertNumber() {
        return sqlSessionMeta.selectOne("selectNextCertNo" );
    }

    public int selectPortableCertCount() {
        return sqlSessionMeta.selectOne("selectNextCertCount" );
    }
}
