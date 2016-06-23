package fr.info.antillesinfov2.business.model;

/**
 * Created by qjrs8151 on 23/06/2016.
 */
public class Session {
    private String sessionName;
    private Integer sessionId;

    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }
}
