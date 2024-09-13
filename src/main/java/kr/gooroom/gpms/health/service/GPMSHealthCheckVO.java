package kr.gooroom.gpms.health.service;

import java.util.Base64;
import java.util.Date;
import java.util.List;

import kr.gooroom.encryptor.AESEncryptor;

@SuppressWarnings("serial")
public class GPMSHealthCheckVO {
    
    private Integer id;
    private String httpStatus;
    private String serverName;
    private String releaseName;
    private String url;
    private Date createdDateTime;
    private Date updatedDateTime;
    private String status;
    private Integer schedule;
    private String note;
    private String target;
    private String sid;
    private String username;
    private String password;
    private String pkey;
    private String psalt;
    private String connection;
    private String port;
    private String dbType;
    private List<DbCheckItemsVO> dbCheckItems;
    private String dist;

    public String getDist() {
        return this.dist;
    }
    public void setDist(String value) {
        this.dist = value;
    }

    public String getDbType() {
        return this.dbType;
    }

    public void setDbType(String value) {
       this.dbType = value;
    }

    public String getPort() {
        return this.port;
    }

    public void setPort(String value) {
        this.port = value;
    }  

    public List<DbCheckItemsVO> getDbCheckItems() {
        return this.dbCheckItems;
      }
      public void setDbCheckItems(List<DbCheckItemsVO> value) {
        this.dbCheckItems = value;
      }


    public String getConnection() {
        return this.connection;
    }

    public void setConnection(String connection) {
        this.connection = connection;
    }


    public String getSid() {
        return this.sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPkey() {
        return this.pkey;
    }

    public void setPkey(String key) {
        this.pkey = key;
    }

    public String getPsalt() {
        return this.psalt;
    }

    public void setPsalt(String salt) {
        this.psalt = salt;
    }

    public String getTarget() {
        return this.target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getNote() {
        return this.note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getSchedule() {
        return this.schedule;
    }

    public void setSchedule(Integer schedule) {
        this.schedule = schedule;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHttpStatus() {
        return this.httpStatus;
    }

    public void setHttpStatus(String httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getServerName() {
        return this.serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getReleaseName() {
        return this.releaseName;
    }

    public void setReleaseName(String releaseName) {
        this.releaseName = releaseName;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getCreatedDateTime() {
        return this.createdDateTime;
    }

    public void setCreatedDateTime(Date createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public Date getUpdatedDateTime() {
        return this.updatedDateTime;
    }

    public void setUpdatedDateTime(Date updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    public Integer getId() {
      return this.id;
    }
    public void setId(Integer value) {
      this.id = value;
    }

    public static class DbCheckItemsVO {
        private Integer dbCheckItemId;
        private Integer parentId;
        private String itemName;
        private String checkQuery;
        private Date createdDateTime;
        private Date updatedDateTime;
        private String result;

        public Integer getDbCheckItemId() {
            return this.dbCheckItemId;
        }
        public void setDbCheckItemId(Integer value) {
            this.dbCheckItemId = value;
        }
        public Integer getParentId() {
            return this.parentId;
        }
        public void setParentId(Integer value) {
            this.parentId = value;
        }

        public String getItemName() {
            return this.itemName;
        }
        public void setItemName(String value) {
            this.itemName = value;
        }

        public String getCheckQuery() {
            return this.checkQuery;
        }
        public void setCheckQuery(String value) {
            this.checkQuery = value;
        }
        
        public Date getCreatedDateTime() {
            return this.createdDateTime;
        }
        public void setCreatedDateTime(Date value) {
            this.createdDateTime = value;
        }  
        public Date updatedDateTime() {
            return this.updatedDateTime;
        }
        public void updatedDateTime(Date value) {
            this.updatedDateTime = value;
        }  
        public String getResult() {
            return this.result;
            }
        public void setResult(String value) {
        this.result = value;
        } 
    }

    public void decodePassword() {
        if (getPassword() == null || getPassword().length() == 0) {
            return;
        }

        setPassword(customDecodePassword(getPassword()));
    }

    public void encodePassword() {
        if (getPassword() == null || getPassword().length() == 0) {
            return;
        }

        setPassword(customEncodePassword(getPassword()));
    }

    public void decryptPassword() {
        if (getPassword() == null || getPassword().length() == 0
            || getPkey() == null || getPkey().length() == 0
            || getPsalt() == null || getPsalt().length() == 0)
        {
            return;
        }

        try {
            setPassword(customDecryptPassword(getPassword(), getPkey(), getPsalt()));
            setPkey(null);
            setPsalt(null);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public void encryptPassword() {
        if (getPassword() == null || getPassword().length() == 0) {
            return;
        }

        try {
            String[] passwords = customEncryptPassword(getPassword());
            setPassword(passwords[0]);
            setPkey(passwords[1]);
            setPsalt(passwords[2]);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    private String customEncodePassword(String password) {
        if (password == null || password.length() == 0) {
            return password;
        }

        String pw1 = Base64.getEncoder().encodeToString(password.getBytes());
        String pw2 = new StringBuilder(pw1).reverse().toString();

        return pw2;
    }

    private String customDecodePassword(String password) {
        if (password == null || password.length() == 0) {
            return password;
        }

        String pw1 = new StringBuilder(password).reverse().toString();
        String pw2 = new String(Base64.getDecoder().decode(pw1));

        return pw2;
    }

    private String[] customEncryptPassword(String password) throws Exception {
        String pw1 = null;
        String key1 = null;
        String salt1 = null;
        AESEncryptor aesEncryptor = new AESEncryptor();
        String encValues = aesEncryptor.encryptByAES(password);

        String[] parts = encValues.split(",");

        for (String part : parts) {
            String[] keyValue = part.split(":");

            if (keyValue.length == 2) {
                if (keyValue[0].equals("PW")) {
                    pw1 = keyValue[1];
                } else if (keyValue[0].equals("KEY")) {
                    key1 = keyValue[1];
                } else if (keyValue[0].equals("SALT")) {
                    salt1 = keyValue[1];
                }
            }
        }

        return new String[] {pw1, key1, salt1};
    }

    private String customDecryptPassword(String password, String key, String salt) throws Exception {
        AESEncryptor aesEncryptor = new AESEncryptor();
        String decValue = aesEncryptor.decryptByAES(password, key, salt);

        return decValue;
    }
}
