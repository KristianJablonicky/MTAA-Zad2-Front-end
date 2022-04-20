package mtaa.java.data;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.JSONObject;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Application implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer workerID;
    private Integer JobOfferID;
    private String description;
    private Boolean response;
    private LocalDateTime created;
    private LocalDateTime expires;

    public Application(Integer workerID, Integer JobOfferID)
    {
        this.id = -1;
        this.JobOfferID = JobOfferID;
        this.workerID = workerID;

        this.description = "";
        this.response = null;
        this.created = null;
        this.expires = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Application(JSONObject app) throws Exception
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        this.id = (Integer) app.get("id");
        this.workerID = (Integer) app.get("worker_id");
        this.JobOfferID = (Integer) app.get("job_offer_id");

        if (JSONObject.NULL.equals(app.get("description"))) this.description = "";
        else this.description = (String) app.get("description");

        this.response = (Boolean) app.get("response");

        this.created = LocalDateTime.parse((String) app.get("created_on"), formatter);
        this.expires = LocalDateTime.parse((String) app.get("expires_on"), formatter);
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getWorkerID() {
        return workerID;
    }

    public void setWorkerID(Integer workerID) {
        this.workerID = workerID;
    }

    public Integer getJobOfferID() {
        return JobOfferID;
    }

    public void setJobOfferID(Integer jobOfferID) {
        JobOfferID = jobOfferID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getResponse() {
        return response;
    }

    public void setResponse(Boolean response) {
        this.response = response;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getExpires() {
        return expires;
    }

    public void setExpires(LocalDateTime expires) {
        this.expires = expires;
    }
}
