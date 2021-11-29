package tobous.collegedroid.functions.changes.encapsulation;

import java.io.Serializable;

/**
 * Created by Ondra on 31. 1. 2016.
 */
public class Change implements Serializable {

    private String author;
    private String startDate;
    private String endDate;
    private String name;
    private String reason;

    private boolean isViewed = false;

    public boolean isViewed() {
        return isViewed;
    }

    public void setIsViewed(boolean isViewed) {
        this.isViewed = isViewed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
