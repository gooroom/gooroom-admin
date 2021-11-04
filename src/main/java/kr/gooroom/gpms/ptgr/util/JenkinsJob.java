package kr.gooroom.gpms.ptgr.util;

import java.util.List;

class JenkinsCauses {
    String _class;
    String shortDescription;
    String userId;
    String userName;

    public String get_class() {
        return _class;
    }

    public void set_class(String _class) {
        this._class = _class;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}

class JenkinsJobParameters {
    String _class;
    String name;
    String value;

    public String get_class() {
        return _class;
    }

    public void set_class(String _class) {
        this._class = _class;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

class JenkinsJobActions {

    String _class;
    List<JenkinsJobParameters> parameters;
    List<JenkinsCauses> causes;

    public String get_class() {
        return _class;
    }

    public void set_class(String _class) {
        this._class = _class;
    }

    public List<JenkinsJobParameters> getParameters() {
        return parameters;
    }

    public void setParameters(List<JenkinsJobParameters> parameters) {
        this.parameters = parameters;
    }

    public List<JenkinsCauses> getCauses() {
        return causes;
    }

    public void setCauses(List<JenkinsCauses> causes) {
        this.causes = causes;
    }
}
class JenkinsArtifacts {

    String _class;

    public String get_class() {
        return _class;
    }

    public void set_class(String _class) {
        this._class = _class;
    }
}

class JenkinsChangeSetItem {
    String _class;

    public String get_class() {
        return _class;
    }

    public void set_class(String _class) {
        this._class = _class;
    }
}

class JenkinsChangeSet {
    String _class;
    String kind;
    List<JenkinsChangeSetItem> items;

    public String get_class() {
        return _class;
    }

    public void set_class(String _class) {
        this._class = _class;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public List<JenkinsChangeSetItem> getItems() {
        return items;
    }

    public void setItems(List<JenkinsChangeSetItem> items) {
        this.items = items;
    }
}

class JenkinsCulprits {
    String _class;

    public String get_class() {
        return _class;
    }

    public void set_class(String _class) {
        this._class = _class;
    }
}
class JenkinsExecutor {
    String _class;
    String executor;

    public String get_class() {
        return _class;
    }

    public void set_class(String _class) {
        this._class = _class;
    }

    public String getExecutor() {
        return executor;
    }

    public void setExecutor(String executor) {
        this.executor = executor;
    }
}

public class JenkinsJob {

    boolean keepLog;
    boolean building;

    int number;
    int queueId;

    long duration;
    long estimatedDuration;
    long timestamp;

    String _class;
    String id;
    String url;
    String result;
    String builtOn;
    String description;
    String displayName;
    String fullDisplayName;

    List<JenkinsJobActions> actions;
    List<JenkinsArtifacts> artifacts;
    List<JenkinsCulprits> culprits;
    JenkinsChangeSet changeSet;
    JenkinsExecutor executor;

    public boolean isKeepLog() {
        return keepLog;
    }

    public void setKeepLog(boolean keepLog) {
        this.keepLog = keepLog;
    }

    public boolean isBuilding() {
        return building;
    }

    public void setBuilding(boolean building) {
        this.building = building;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getQueueId() {
        return queueId;
    }

    public void setQueueId(int queueId) {
        this.queueId = queueId;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getEstimatedDuration() {
        return estimatedDuration;
    }

    public void setEstimatedDuration(long estimatedDuration) {
        this.estimatedDuration = estimatedDuration;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String get_class() {
        return _class;
    }

    public void set_class(String _class) {
        this._class = _class;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getBuiltOn() {
        return builtOn;
    }

    public void setBuiltOn(String builtOn) {
        this.builtOn = builtOn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getFullDisplayName() {
        return fullDisplayName;
    }

    public void setFullDisplayName(String fullDisplayName) {
        this.fullDisplayName = fullDisplayName;
    }

    public List<JenkinsJobActions> getActions() {
        return actions;
    }

    public void setActions(List<JenkinsJobActions> actions) {
        this.actions = actions;
    }

    public List<JenkinsArtifacts> getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(List<JenkinsArtifacts> artifacts) {
        this.artifacts = artifacts;
    }

    public JenkinsChangeSet getChangeSet() {
        return changeSet;
    }

    public void setChangeSet(JenkinsChangeSet changeSet) {
        this.changeSet = changeSet;
    }

    public JenkinsExecutor getExecutor() {
        return executor;
    }

    public void setExecutor(JenkinsExecutor executor) {
        this.executor = executor;
    }

    public List<JenkinsCulprits> getCulprits() {
        return culprits;
    }

    public void setCulprits(List<JenkinsCulprits> culprits) {
        this.culprits = culprits;
    }

    public long findDuration (int imageId) {


        return 0;
    }
}
