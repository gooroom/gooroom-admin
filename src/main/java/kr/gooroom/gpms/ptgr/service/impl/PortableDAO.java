package kr.gooroom.gpms.ptgr.service.impl;

import kr.gooroom.gpms.common.service.dao.SqlSessionMetaDAO;
import kr.gooroom.gpms.ptgr.service.PortableVO;
import kr.gooroom.gpms.ptgr.service.PortableViewVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository("portableDAO")
public class PortableDAO extends SqlSessionMetaDAO {

    private static final Logger logger = LoggerFactory.getLogger(PortableDAO.class);


    public List<String> checkCertID (HashMap<String, Object> options) {
        List<String> res = null;
        res = sqlSessionMeta.selectList("checkId", options);
        return res;
    }

    public long createPortableData(PortableVO vo) {
        return sqlSessionMeta.insert("insertPortableData", vo);
    }

    public long createPortableDataHist(PortableVO vo) {
        return sqlSessionMeta.insert("insertPortableDataHist", vo);
    }

    public long createPortableDataAllHist() {
        return sqlSessionMeta.insert("insertPortableDataAllHist");
    }

    public List<PortableViewVO> selectPortableViewList() {
        List<PortableViewVO> res = null;
        res = sqlSessionMeta.selectList("selectPortableViewList");
        return res;
    }

    public List<PortableViewVO> selectPortableViewList(HashMap<String, Object> options) {
        List<PortableViewVO> res = null;
        res = sqlSessionMeta.selectList("selectPortableViewListPaged", options);
        return res;
    }

    public List<PortableVO> selectPortableDataList(HashMap<String, Object> options) {
        List<PortableVO> res = null;
        res = sqlSessionMeta.selectList("selectPortableDataList", options);
        return res;
    }

    public List<PortableVO> selectPortableDataListByAdminIdAndApprove(HashMap<String, Object> options) {
        List<PortableVO> res = null;
        res = sqlSessionMeta.selectList("selectPortableDataListByAdminIdAndApprove", options);
        return res;
    }

    public long selectPortableReapproveCount(HashMap<String, Object> options) {
        return sqlSessionMeta.selectOne("selectPortableReapproveCount", options);
    }

    public long updatePortableData(PortableVO vo) {
        return sqlSessionMeta.update("updatePortableData", vo);
    }

    public long updateAllPortableApprove(String adminId) {
        return sqlSessionMeta.update("updateAllPortableApprove", adminId);
    }

    public long deletePortableData (HashMap<String, Object> ids) {
        return sqlSessionMeta.delete("deletePortableData", ids);
    }

    public long deletePortableDataById (int ptgrId) {
        return sqlSessionMeta.delete("deletePortableDataById", ptgrId);
    }

    public long deleteAllPortableData () {
        return sqlSessionMeta.delete("deleteAllPortableData");
    }

    public int selectNextPortableNumber() {
        return sqlSessionMeta.selectOne("selectNextPortableNo" );
    }

    public long  selectPortableTotalCount(HashMap<String, Object> options) {
        return sqlSessionMeta.selectOne("selectPortableTotalCount", options );
    }

    public long selectPortableFilteredCount(HashMap<String, Object> options) {
        return sqlSessionMeta.selectOne("selectPortableFilteredCount", options);
    }

    public String selectPortableUserListForDuplicateUserId (String id) {
        String res = null;
        res = sqlSessionMeta.selectOne("selectPortableUserListForDuplicateUserId", id);
        return res;
    }
}
