package mtaa.java.data;

import org.json.JSONObject;
import java.io.Serializable;

public class JobOffer implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String name;
    private Integer employerID;
    private String field;
    private Integer salary;
    private String working_hours;
    private String location;
    private String detail;

    //Constructors
    public JobOffer(Integer employerID)
    {
        this.id = -1;
        this.name = "";
        this.employerID = employerID;
        this.field = "";
        this.salary = -1;
        this.working_hours = "";
        this.location = "";
        this.detail = "";
    }

    public JobOffer(JSONObject offer) throws Exception
    {
        this.id = (Integer) offer.get("id");
        this.name = (String) offer.get("name");
        this.employerID = (Integer) offer.get("employer_id");
        this.field = (String) offer.get("field");

        if (JSONObject.NULL.equals(offer.get("salary"))) this.salary = -1;
        else this.salary = (Integer) offer.get("salary");

        if (JSONObject.NULL.equals(offer.get("working_hours"))) this.working_hours = "";
        else this.working_hours = (String) offer.get("working_hours");

        if (JSONObject.NULL.equals(offer.get("location"))) this.location = "";
        else this.location = (String) offer.get("location");

        if (JSONObject.NULL.equals(offer.get("detail"))) this.detail = "";
        else this.detail = (String) offer.get("detail");

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getEmployerID() {
        return employerID;
    }

    public void setEmployerID(Integer employerID) {
        this.employerID = employerID;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Integer getSalary() {
        return salary;
    }

    public void setSalary(Integer salary) {
        this.salary = salary;
    }

    public String getWorking_hours() {
        return working_hours;
    }

    public void setWorking_hours(String working_hours) {
        this.working_hours = working_hours;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
