package mtaa.java;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import mtaa.java.data.JobOffer;
import mtaa.java.data.User;
import mtaa.java.databinding.ActivityJobOfferBinding;

public class JobOfferActivity extends AppCompatActivity {

    private ActivityJobOfferBinding binding;

    public void popupMessage(String nadpis, String sprava){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(sprava);
        alertDialogBuilder.setTitle(nadpis);
        alertDialogBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Log.d(nadpis,"Ok button stlaceny");
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityJobOfferBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        TextView name = (TextView)findViewById(R.id.TFoffername);
        TextView field = (TextView)findViewById(R.id.TFofferfield);
        TextView salary = (TextView)findViewById(R.id.TFoffersalary);
        TextView hours = (TextView)findViewById(R.id.TFofferhours);
        TextView location = (TextView)findViewById(R.id.TFofferlocation);
        TextView detail = (TextView)findViewById(R.id.TFofferdetail);
        Button spat = (Button)findViewById(R.id.Boffernaspat);
        Button uloz = (Button)findViewById(R.id.Bofferuloz);
        Button zmaz = (Button)findViewById(R.id.Bofferdelete);

        Bundle extras = getIntent().getExtras();

        if (extras != null)
        {
            JobOffer j = (JobOffer) extras.get("currentJob");
            User u = (User) extras.get("currentUser");
            Integer editable = (Integer) extras.get("edit");

            //prezeranie
            if (editable == 0)
            {
                name.setFocusable(false);
                name.setClickable(false);
                name.setLongClickable(false);

                field.setFocusable(false);
                field.setClickable(false);
                field.setLongClickable(false);

                salary.setFocusable(false);
                salary.setClickable(false);
                salary.setLongClickable(false);

                hours.setFocusable(false);
                hours.setClickable(false);
                hours.setLongClickable(false);

                location.setFocusable(false);
                location.setClickable(false);
                location.setLongClickable(false);

                detail.setFocusable(false);
                detail.setClickable(false);
                detail.setLongClickable(false);

                uloz.setVisibility(View.GONE);
                zmaz.setVisibility(View.GONE);

                name.setText(j.getName());
                field.setText(j.getField());

                if (j.getSalary() == -1) salary.setText("");
                else salary.setText(j.getSalary().toString());

                hours.setText(j.getWorking_hours());
                location.setText(j.getLocation());
                detail.setText(j.getDetail());
            }

            //vytvaranie
            else if (editable == 1)
            {
                zmaz.setVisibility(View.GONE);
                uloz.setText("Vytor novú ponuku");
            }
            else
            {
                name.setText(j.getName());
                field.setText(j.getField());
                if (j.getSalary() == -1) salary.setText("");
                    else salary.setText(j.getSalary().toString());
                hours.setText(j.getWorking_hours());
                location.setText(j.getLocation());
                detail.setText(j.getDetail());
            }


            uloz.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String URLstring;
                    String method;

                    if (name.getText().toString().equals("") || field.getText().toString().equals(""))
                    {
                        popupMessage("Chýbajúce údaje","Nezadal si názov alebo obsať práce.");
                        return;
                    }

                    if (editable == 1)
                    {
                        method = "POST";
                        URLstring = "/postJobOffer/" + u.getName() + "/" + u.getPassword() + "/";
                        URLstring += name.getText().toString() + "/" + field.getText().toString() + "/";
                    }
                    else
                    {
                        method = "PUT";
                        URLstring = "/putJobOffer/"+ u.getName() + "/" + u.getPassword() + "/" + j.getId() + "/";
                        URLstring += "name=" + name.getText().toString() + "/field=" + field.getText().toString() + "/";
                    }

                    //vytvorenie pracovnej ponuky
                    JobOffer newJob = new JobOffer(u.getId());

                    newJob.setName(name.getText().toString());
                    newJob.setField(field.getText().toString());
                    newJob.setId(j.getId());

                    if (!salary.getText().toString().equals("")) newJob.setSalary(Integer.valueOf(salary.getText().toString()));
                    else newJob.setSalary(j.getSalary());

                    if (!hours.getText().toString().equals("")) newJob.setWorking_hours(hours.getText().toString());
                    else newJob.setWorking_hours(j.getWorking_hours());

                    if (!location.getText().toString().equals("")) newJob.setLocation(location.getText().toString());
                    else newJob.setLocation(j.getLocation());


                    if (!detail.getText().toString().equals("")) newJob.setDetail(detail.getText().toString());
                    else newJob.setDetail(j.getDetail());



                    if (!u.isOffline())
                    {
                        if (!salary.getText().toString().equals("")) URLstring += "salary=" + salary.getText().toString() + "/";

                        if (!hours.getText().toString().equals("")) URLstring += "hours=" + hours.getText().toString() + "/";

                        if (!location.getText().toString().equals("")) URLstring += "location=" + location.getText().toString() + "/";

                        if (!detail.getText().toString().equals("")) URLstring += "detail=" + detail.getText().toString() + "/";

                        String response = Requests.OTHER_request(method,URLstring);

                        if (!response.equals("200") && !response.equals("201"))
                        {
                            popupMessage("Zle zadané údaje!","HTTP erroe kod: " + response);

                            if (response.equals("408"))
                            {
                                u.setOfflineMode(true);
                                popupMessage("Chyba", "Nepodarilo sa nadviazať konverzáciu so serverom.\nZapína sa offline mód.\nPonuka bola ulozena.");

                                if (method.equals("POST")) u.addJob(method, newJob);
                                else u.updateJob(j,newJob);
                                spat.callOnClick();
                            }
                        }
                        else
                        {
                            popupMessage("Uspech","HTTP volanie " + method + " bolo pre pracovnu ponuku uspesne.");
                            spat.callOnClick();
                        }
                    }
                    else
                    {
                        if (method.equals("POST")) u.addJob(method, newJob);
                        else u.updateJob(j,newJob);
                        spat.callOnClick();
                    }

                }
            });

            zmaz.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!u.isOffline())
                    {
                        String URLstring;

                        URLstring = "/delJobOffer/" + u.getName() + "/" + u.getPassword() + "/" + j.getId() + "/";

                        String response = Requests.OTHER_request("DELETE",URLstring);

                        if (response.equals("408"))
                        {
                            u.setOfflineMode(true);
                            popupMessage("Chyba", "Nepodarilo sa nadviazať konverzáciu so serverom.\nZapína sa offline mód.\nPracovna ponuka bola vyzmazana.");

                            u.removeJob(j);
                            spat.callOnClick();
                        }
                        else if (!response.equals("200")) popupMessage("Chyba!","HTTP error kod: " + response);
                        else
                        {
                            popupMessage("Uspech","Pracovna ponuka bola vyzmazana.");
                            spat.callOnClick();
                        }
                    }
                    else
                    {
                        u.removeJob(j);
                        spat.callOnClick();
                    }

                }
            });

            spat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(JobOfferActivity.this, HomeActivity.class);
                    i.putExtra("currentUser", u);
                    startActivity(i);
                }
            });
        }
        else Log.e("ERROR","Screen 'JobOffer' could not be initialized.");

    }
}
