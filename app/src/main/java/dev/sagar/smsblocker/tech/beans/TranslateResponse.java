package dev.sagar.smsblocker.tech.beans;

/**
 * Created by sagarpawar on 06/05/18.
 */

public class TranslateResponse {
    private Exception err;
    private String requestCode;
    private String fromLang;
    private String toLang;
    private String orgText;
    private String transText;

    public String getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(String requestCode) {
        this.requestCode = requestCode;
    }

    public Exception getErr() {
        return err;
    }

    public void setErr(Exception err) {
        this.err = err;
    }

    public String getFromLang() {
        return fromLang;
    }

    public void setFromLang(String fromLang) {
        this.fromLang = fromLang;
    }

    public String getToLang() {
        return toLang;
    }

    public void setToLang(String toLang) {
        this.toLang = toLang;
    }

    public String getOrgText() {
        return orgText;
    }

    public void setOrgText(String orgText) {
        this.orgText = orgText;
    }

    public String getTransText() {
        return transText;
    }

    public void setTransText(String transText) {
        this.transText = transText;
    }
}
