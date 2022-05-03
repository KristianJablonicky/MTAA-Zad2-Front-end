package mtaa.java.data;

import android.net.Uri;

import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String name;
    private String password;
    private String phone;
    private String email;
    private String birthday;

    // offline spravanie
    private Boolean offlineMode;
    private String povodnyURL;
    private String zivotopisURI = null; // String ktory sa konvertuje na Uri, kedze sa objekt User posiela medzi aktivitami

    //companyID = null -> User is Employer
    private Integer companyID;

    //Constructors
    public User() {}

    public User(JSONObject user) throws Exception
    {
        this.id = (Integer) user.get("id");
        this.name = (String) user.get("name");
        this.password =  (String) user.get("password");

        if (JSONObject.NULL.equals(user.get("phone"))) this.phone = "";
        else this.phone = (String) user.get("phone");

        if (JSONObject.NULL.equals(user.get("email"))) this.email = "";
        else this.email =(String) user.get("email");

        if (JSONObject.NULL.equals(user.get("birth_date"))) this.birthday = null;
        else this.birthday = (String) user.get("birth_date");

        try
        {
            this.companyID = (Integer) user.get("company_id");
        }
        catch (Exception e)
        {
            this.companyID = null;
        }
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

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {this.birthday = birthday;}

    public boolean isEmployer()
    {
        if (this.companyID == null) return false;
        else return true;
    }

    public Boolean isOffline(){return offlineMode;}
    public void setOfflineMode(Boolean newBool){this.offlineMode = newBool;}

    public String getPovodnyURL(){return povodnyURL;}
    public void setPovodnyURL(String url){this.povodnyURL = url;}

    public Uri getZivotopisURI(){if(zivotopisURI != null) return Uri.parse(zivotopisURI);
        else return null;}
    public void setZivotopisURI(Uri uri){this.zivotopisURI = String.valueOf(uri);}

}
