package kr.gooroom.gpms.ptgr.service.impl;

import kr.gooroom.gpms.common.service.dao.SqlSessionMetaDAO;
import kr.gooroom.gpms.ptgr.service.PortableLogVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

@Repository("portableLogDAO")
public class PortableLogDAO extends SqlSessionMetaDAO {
    private static final Logger logger = LoggerFactory.getLogger(PortableLogDAO.class);

    public long createPortableLog (PortableLogVO vo) throws SQLException {
        return (long) sqlSessionMeta.insert("insertPortableLog", vo);
    }

    public List<PortableLogVO> selectPortableLogList () throws SQLException {
        List<PortableLogVO> res = null;
        res = sqlSessionMeta.selectList("selectPortableLogList");
        return res;
    }

    public List<PortableLogVO> selectPortableLogList (HashMap<String, Object> options) throws SQLException {
        List<PortableLogVO> res = null;
        res = sqlSessionMeta.selectList("selectPortableLogListByCondition", options);
        return res;
    }

    public long deleteAllPortableLog () throws SQLException {
        return (long) sqlSessionMeta.delete("deleteAllPortableLog");
    }

    public int selectNextPortableLogNumber() throws SQLException {
        return sqlSessionMeta.selectOne("selectNextLogNo" );
    }

    public int selectPortableLogCount() throws SQLException {
        return sqlSessionMeta.selectOne("selectNextLogCount" );
    }
}
