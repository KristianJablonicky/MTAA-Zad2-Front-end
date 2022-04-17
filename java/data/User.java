package mtaa.java.data;

import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;

public class User {
    private String name;
    private String password;
    private String phone;
    private String email;
    private Date birthday;

    //companyID = null -> User is Employer
    private Integer companyID;

    //Constructors
    public User() {}

    //@SuppressLint("NewApi")
    public User(JSONObject user) throws Exception
    {
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");

        this.name = (String) user.get("name");
        this.password =  (String) user.get("password");

        if (JSONObject.NULL.equals(user.get("phone"))) this.phone = "";
        else this.phone = (String) user.get("phone");

        if (JSONObject.NULL.equals(user.get("email"))) this.email = "";
        else this.email =(String) user.get("email");

        if (JSONObject.NULL.equals(user.get("birth_date"))) this.birthday = null;
        else this.birthday = ft.parse((String) user.get("birth_date"));

        try
        {
            this.companyID = (int)user.get("company_id");
        }
        catch (Exception e)
        {
            this.companyID = null;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getCompanyID() {
        if (companyID == null) return -1;
        return companyID;
    }

    public void setCompanyID(Integer companyID) {
        this.companyID = companyID;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public boolean isEmployer()
    {
        if (this.companyID == null) return false;
        else return true;
    }
}
