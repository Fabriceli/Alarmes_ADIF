package model;

/**
 * Created by Fabrice on 8/14/2015.
 */
public class AlarmeItemBean {
    private String message;
    public AlarmeItemBean(String message){
        setMessage(message);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
