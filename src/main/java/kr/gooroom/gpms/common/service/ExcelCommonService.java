package kr.gooroom.gpms.common.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ExcelCommonService {

    /**
     * 업로드 된 엑셀 포맷에 맞는 workbook 객체 생성
     * @param file
     * @return
     */
    Workbook getWorkbook(MultipartFile file) throws Exception;

    /**
     * 셀 형식에 따른 값 String으로 변환
     * @param cell
     * @return
     */
    String getValue(Cell cell);

    /**
     * 일괄등록(조직, 사용자) 엑셀 파일 내용 읽기
     * @param file
     * @return
     * @throws Exception
     */
    List<List<String>> read(MultipartFile file, String fileType) throws Exception;

    /**
     * 조직, 사용자 리스트 엑셀파이로 출력
     * @param list
     * @return
     */
    XSSFWorkbook write(List<List<String>> list);

    /**
     * 조직코드 유효성 체크
     * @param deptCd
     * @return
     */
    boolean deptCdRegex(String deptCd);

    /**
     * 사용자 아이디 유효성 체크
     * @param userId
     * @return
     */
    boolean userIdRegex(String userId);

    /**
     * 이메일 형식 체크
     * @param email
     * @return
     */
    boolean emailRegex(String email);
}
