package kr.gooroom.gpms.ptgr.service.impl;

import kr.gooroom.gpms.common.service.dao.SqlSessionMetaDAO;
import kr.gooroom.gpms.ptgr.service.PortableCertVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;

@Repository("portableCertDAO")
public class PortableCertDAO extends SqlSessionMetaDAO {

    private static final Logger logger = LoggerFactory.getLogger(PortableCertDAO.class);

    public long createPortableCert(PortableCertVO vo) throws SQLException {
        return (long) sqlSessionMeta.insert("insertPortableCert", vo);
    }

    public PortableCertVO selectPortableCert(String certId) throws SQLException {
        PortableCertVO res = null;
        res = sqlSessionMeta.selectOne("selectPortableCert", certId);
        return res;
    }

    public long updatePortableCert(PortableCertVO vo) throws SQLException {
        return (long) sqlSessionMeta.update("updatePortableCert", vo);
    }

    public long deletePortableCert(int certId) throws SQLException {
        return (long) sqlSessionMeta.delete("deletePortableCert", certId);
    }

    public long deletePortableCertAll() throws SQLException {
        return (long) sqlSessionMeta.delete("deletePortableCertAll");
    }

    public int selectNextPortableCertNumber() throws SQLException {
        return sqlSessionMeta.selectOne("selectNextCertNo" );
    }

    public int selectPortableCertCount() throws SQLException {
        return sqlSessionMeta.selectOne("selectNextCertCount" );
    }
}
