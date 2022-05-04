package mtaa.java.data;

import android.net.Uri;
import android.os.Build;

import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import mtaa.java.Requests;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String name;
    private String password;
    private String phone;
    private String email;
    private String birthday;
    private Integer companyID;  //companyID = null -> User is Employer

    // offline spravanie
    private Boolean offlineMode;

    private class MYoffers implements Serializable {
        protected JobOffer job;
        protected String type;

        public MYoffers(String type, JobOffer newJob) {
            this.type = type;
            this.job = newJob;
        }
    }
    private ArrayList<MYoffers> URLoffers = new ArrayList<MYoffers> ();

    private String povodneUdaje = null;
    private String zivotopisURI = null; // String ktory sa konvertuje na Uri, kedze sa objekt User posiela medzi aktivitami


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

    public boolean isEmployer() {
        if (this.companyID == null) return false;
        else return true;
    }

    public Boolean isOffline() {
        return offlineMode;
    }

    public void setOfflineMode(Boolean newBool) {
        this.offlineMode = newBool;
    }

    public Integer executeURLs() {

        Integer failed = 0;

        for (int i = 0; i < this.URLoffers.size(); i++)
        {
            String method = this.URLoffers.get(i).type;
            JobOffer j = this.URLoffers.get(i).job;

            if (!method.equals("GET"))
            {
                String URLstring;

                if (method.equals("DELETE"))
                    URLstring = "/delJobOffer/" + this.getName() + "/" + this.getPassword() + "/" + j.getId() + "/";

                else if (method.equals("PUT"))
                {
                    URLstring = "/putJobOffer/" + this.getName() + "/" + this.getPassword() + "/" + j.getId() + "/";
                    URLstring += "name=" + j.getName() + "/field=" + j.getField() + "/";

                    if (!j.getSalary().equals("")) URLstring += "salary=" + j.getSalary() + "/";
                    if (!j.getWorking_hours().equals("")) URLstring += "hours=" + j.getWorking_hours() + "/";
                    if (!j.getLocation().equals("")) URLstring += "location=" + j.getLocation() + "/";
                    if (!j.getDetail().equals("")) URLstring += "detail=" + j.getDetail() + "/";
                }

                else
                {
                    URLstring = "/postJobOffer/" + this.getName() + "/" + this.getPassword() + "/";
                    URLstring += j.getName() + "/" + j.getField() + "/";

                    if (!j.getSalary().equals(-1)) URLstring += "salary=" + j.getSalary() + "/";
                    if (!j.getWorking_hours().equals("")) URLstring += "hours=" + j.getWorking_hours() + "/";
                    if (!j.getLocation().equals("")) URLstring += "location=" + j.getLocation() + "/";
                    if (!j.getDetail().equals("")) URLstring += "detail=" + j.getDetail() + "/";
                }

                String response = Requests.OTHER_request(this.URLoffers.get(i).type ,URLstring);

                if (!response.equals("200") && !response.equals("201")) ++failed;
                else this.URLoffers.set(i, new MYoffers("GET",j));
            }
        }

        return failed;
    }

    public void addJob(String type, JobOffer j) {
        if (type.equals("DELETE") || type.equals("PUT")) return;
        this.URLoffers.add(new MYoffers(type, j));
    }

    public void updateJob(JobOffer j, JobOffer newJob)
    {
        Integer index = null;
        for (int i = 0; i < this.URLoffers.size(); i++)
        {
            if (this.URLoffers.get(i).job.equals(j))
            {
                index = i;
                break;
            }

        }

        if (index == null) return;

        if (this.URLoffers.get(index).type.equals("POST")) this.URLoffers.set(index, new MYoffers("POST", newJob));
        else this.URLoffers.set(index, new MYoffers("PUT", newJob));
    }

    public void removeJob(JobOffer j)
    {
        Integer index = null;

        for (int i = 0; i < this.URLoffers.size(); i++)
        {
            if (this.URLoffers.get(i).job.equals(j))
            {
                index = i;
                break;
            }
        }

        if (index != null)
        {
            if (this.URLoffers.get(index).type.equals("POST")) this.URLoffers.remove(index);
            else this.URLoffers.set(index, new MYoffers("DELETE", j));
        }
    }

    public ArrayList<JobOffer> getMYoffers() {
        ArrayList<JobOffer> offers = new ArrayList<JobOffer>();

        for (int i = 0; i < this.URLoffers.size(); i++)
            if (!this.URLoffers.get(i).type.equals("DELETE")) offers.add(URLoffers.get(i).job);

        return offers;
    }

    public void clearList () {
        this.URLoffers = new ArrayList<MYoffers> ();
    }

    public String getPovodneUdaje() {
        return povodneUdaje;
    }

    public void setPovodneUdaje(String povodneUdaje) {
        this.povodneUdaje = povodneUdaje;
    }

    public Uri getZivotopisURI() {
        if(zivotopisURI != null) return Uri.parse(zivotopisURI);
        else return null;
    }

    public void setZivotopisURI(Uri uri) {
        this.zivotopisURI = String.valueOf(uri);
    }

    public String[] uploadPDF(Uri uri)
    {
        if(!this.isOffline())
        {
            Requests objekt = new Requests();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                if (objekt.PDF_POST_request(uri.getPath(), "/postPDF/" + this.getName() + "/" + this.getPassword() + "/") <= 200)
                    return new String[]{ "Úspech", "Životopis bol úspešne uverejnený." };

                else return new String[]{"Chyba", "Niekde nastala chyba."};
            }
            else return new String[]{"Chyba", "Vaša verzia Androidu nepodpruje túto funkcionalitu."};
        }
        else
        {
            this.setZivotopisURI(uri);
            return new String[]{"Info:", "Zvolený súbor sa uverejní, až keď obnovíte pripojenie."};
        }
    }

}
