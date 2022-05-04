package mtaa.java;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;

import mtaa.java.data.JobOffer;
import mtaa.java.data.User;
import mtaa.java.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;

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
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        TextView username = (TextView)findViewById(R.id.TFusername);
        LinearLayout extraButtons = (LinearLayout)findViewById(R.id.LLbuttons);
        ImageButton nastaveniaTlacidlo = (ImageButton) findViewById(R.id.Bsettings);
        RadioButton pouzivateliaRB = (RadioButton) findViewById(R.id.RBusers);
        RadioButton ponukyRB = (RadioButton) findViewById(R.id.RBponuky);
        TextView searchBar = (TextView) findViewById(R.id.TFsearchbar);
        Switch searchSwitch = (Switch) findViewById(R.id.Ssearch);
        FloatingActionButton searchButton = (FloatingActionButton) findViewById(R.id.Bsearch);
        Button sucasnePonuky = (Button) findViewById(R.id.BsucasneP);
        ImageButton vytvoreniePonuky = (ImageButton) findViewById(R.id.BVytvorenieP);
        Button onlineButton = (Button) findViewById(R.id.Boffline);

        ListView list = (ListView) findViewById(R.id.LWsearch);
        SearchAdapter customAdapter = new SearchAdapter(this);
        list.setAdapter(customAdapter);


        Bundle extras = getIntent().getExtras();

        if (extras != null)
        {
            User u = (User) extras.get("currentUser");
            username.setText(u.getName());

            if (!u.isEmployer())
            {
                View divider = (View) findViewById(R.id.divider);

                extraButtons.setVisibility(View.INVISIBLE);
                extraButtons.setVisibility(View.GONE);

                ConstraintLayout.LayoutParams params =(ConstraintLayout.LayoutParams)divider.getLayoutParams();
                params.topMargin = 180;

                divider.setLayoutParams(params);
            }
            else if (!u.isOffline())
            {
                String URLstring = "/getAllJobOffers/" + u.getId() + "/";

                JSONArray vysledok = Requests.GET_requestARRAY(URLstring);

                try
                {
                    if (vysledok != null) u.clearList();

                    for (int i = 0; i < vysledok.length(); i++)
                        u.addJob("GET", new JobOffer(vysledok.getJSONObject(i)));
                }
                catch (Exception e) {
                    popupMessage("Error","Pracovné ponuky neboli načítané z databázy.");
                    u.setOfflineMode(true);
                    onlineButton.setVisibility(View.VISIBLE);
                }

            }

            if (!u.isOffline()) onlineButton.setVisibility(View.INVISIBLE);

            //ACTIONS
            nastaveniaTlacidlo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(HomeActivity.this, EditUserActivity.class);
                    i.putExtra("currentUser", u);
                    startActivity(i);
                }
            });

            vytvoreniePonuky.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(HomeActivity.this, JobOfferActivity.class);
                    i.putExtra("currentUser", u);
                    i.putExtra("currentJob",new JobOffer(u.getId()));

                    i.putExtra("edit",1);

                    startActivity(i);
                }
            });

            pouzivateliaRB.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    ponukyRB.setChecked(false);

                    searchSwitch.setTextOn("Typ (Z/P)");
                    if (searchSwitch.isChecked()) searchSwitch.setText(searchSwitch.getTextOn());

                    searchBar.setText("");
                    searchBar.setHint(searchSwitch.getText() + " používateľa");
                }
            });

            ponukyRB.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    pouzivateliaRB.setChecked(false);

                    searchSwitch.setTextOn("Oblasť");
                    if (searchSwitch.isChecked()) searchSwitch.setText(searchSwitch.getTextOn());

                    searchBar.setText("");
                    searchBar.setHint(searchSwitch.getText() + " pracovnej ponuky");
                }
            });

            searchSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) searchSwitch.setText(searchSwitch.getTextOn());
                    else searchSwitch.setText(searchSwitch.getTextOff());


                    if (pouzivateliaRB.isChecked()) searchBar.setHint(searchSwitch.getText() + " používateľa");
                    else searchBar.setHint(searchSwitch.getText() + " pracovnej ponuky");
                }
            });

            searchButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {

                    if (u.isOffline())
                    {
                        popupMessage("Info", "Je zapnutý offline mód. Nemáš prístup k verejným údajom!");
                        return;
                    }

                    String URLstring;

                    if (pouzivateliaRB.isChecked())
                    {
                        URLstring = "/findUsers/";
                        if (searchSwitch.isChecked())
                        {
                            if (searchBar.getText().toString().equals("P") || searchBar.getText().toString().equals("p")) URLstring += "W";
                            else if (searchBar.getText().toString().equals("Z") || searchBar.getText().toString().equals("z")) URLstring += "E";
                            else if (!searchBar.getText().toString().equals("")) popupMessage("Chyba!", "Nesprávny typ používateľa!\nVypíše sa každý typ používateľa.");
                        }
                    }
                    else URLstring = "/searchJobOffers/";

                    JSONArray vysledok = Requests.GET_requestARRAY(URLstring);

                    if (vysledok == null)
                    {
                        u.setOfflineMode(true);
                        onlineButton.setVisibility(View.VISIBLE);
                        popupMessage("Chyba", "Nepodarilo sa nadviazať konverzáciu so serverom.\n" + "Zapína sa offline mód.");
                        return;
                    }

                    try
                    {
                        if (pouzivateliaRB.isChecked())
                        {
                            ArrayList<User> listdata = new ArrayList<User>();
                            for (int i = 0; i < vysledok.length(); i++) listdata.add(new User(vysledok.getJSONObject(i)));

                            if (!searchSwitch.isChecked() && !searchBar.getText().toString().equals(""))
                            {
                                Predicate<User> condition = employee -> !employee.getName().contains(searchBar.getText().toString());

                                listdata.removeIf(condition);
                            }

                            customAdapter.setUserList(listdata);
                        }
                        else
                        {
                            ArrayList<JobOffer> listdata = new ArrayList<JobOffer>();
                            for (int i = 0; i < vysledok.length(); i++) listdata.add(new JobOffer(vysledok.getJSONObject(i)));


                            if (searchSwitch.isChecked() && !searchBar.getText().toString().equals(""))
                            {
                                Predicate<JobOffer> condition = offer -> !offer.getField().contains(searchBar.getText().toString());

                                listdata.removeIf(condition);
                            }
                            else if (!searchBar.getText().toString().equals(""))
                            {
                                Predicate<JobOffer> condition = offer -> !offer.getName().contains(searchBar.getText().toString());

                                listdata.removeIf(condition);
                            }

                            customAdapter.setOfferList(listdata);
                        }



                        customAdapter.notifyDataSetChanged();
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }

                }
            });

            sucasnePonuky.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pouzivateliaRB.setChecked(false);
                    ponukyRB.setChecked(true);

                    searchSwitch.setTextOn("Oblasť");
                    if (searchSwitch.isChecked()) searchSwitch.setText(searchSwitch.getTextOn());

                    searchBar.setText("");
                    searchBar.setHint(searchSwitch.getText() + " pracovnej ponuky");


                    customAdapter.setOfferList(u.getMYoffers());
                    customAdapter.notifyDataSetChanged();
                }
            });

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView <? > parent, View view, int position, long id)
                {

                    if (pouzivateliaRB.isChecked())
                    {
                        User item = (User) list.getItemAtPosition( position );

                        Intent i = new Intent(HomeActivity.this, ViewUserActivity.class);
                        i.putExtra("currentUser", u);
                        i.putExtra("shownuser",item);

                        startActivity(i);
                    }
                    else
                    {
                        JobOffer item = (JobOffer) list.getItemAtPosition( position );
                        Intent i = new Intent(HomeActivity.this, JobOfferActivity.class);
                        i.putExtra("currentUser", u);
                        i.putExtra("currentJob",item);

                        if (u.isEmployer() && item.getEmployerID().equals(u.getId())) i.putExtra("edit",2);
                        else i.putExtra("edit",0);

                        startActivity(i);
                    }

                }
            });

            onlineButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(Requests.GET_request("/findUsers/") == null) {
                        popupMessage("Chyba", "Nepodarilo sa obnoviť pripojenie ku serveru.");
                        return;
                    }

                    String odpoved = "Pripojenie bolo úspešne obnovené.";

                    if (u.getPovodneUdaje() != null)
                    {
                        String urlString;

                        if (u.isEmployer()) urlString = "/putEmployer/" + u.getPovodneUdaje() + "/";
                        else urlString = "/putWorker/" + u.getPovodneUdaje() + "/";

                        String[] parts = u.getPovodneUdaje().split("/");

                        if (!parts[0].equals(u.getName())) urlString += "name=" + u.getName() + "/";
                        if (!parts[1].equals(u.getPassword())) urlString += "password=" + u.getPassword() + "/";

                        if (u.getBirthday().length() > 0) {
                            urlString += "date=" + u.getBirthday() + "/";
                        }
                        if (u.getEmail().length() > 0) {
                            urlString += "email=" + u.getEmail() + "/";
                        }

                        if (u.getPhone().length() > 0) {
                            urlString += "phone=" + u.getPhone() + "/";
                        }

                        String response = Requests.OTHER_request("PUT", urlString);
                        int responseCode = Integer.parseInt(response);

                        if (responseCode >= 400)
                        {
                            odpoved = "Vaše údaje o účte sa nemohli nahrať na server.";

                            if (responseCode == 403) odpoved += "\nMeno zadané v offline móde už existuje, zmeny vašich údajov neboli nahrané.";
                            else odpoved += "\nHTTP error kod: " + response;

                            if (responseCode == 408) odpoved += "\nSpojenie so serverom nebolo nadviazane";


                            popupMessage("Chyba", odpoved);
                            return;
                        }
                        else odpoved += "\nVaše údaje zadané v offline móde sa úspešne nahrali na server.";

                    }

                    if (u.getZivotopisURI() != null)
                    {
                        String [] res = u.uploadPDF(u.getZivotopisURI());
                        u.setZivotopisURI(null);

                        if (res[0].equals("Úspech")) odpoved += "\n" + res[1];
                        else
                        {
                            popupMessage(res[0], res[1]);
                            return;
                        }

                    }

                    Integer failed = u.executeURLs();
                    if (!failed.equals(0))
                    {
                        popupMessage("Chyba", "Nepodarilo sa ulozit ponuky. Pocet neuspesnych pokusov = " + failed);
                        return;
                    }
                    else odpoved += "\nVsetky pracovne ponuky boli aktualizovane.";

                    u.setOfflineMode(false);
                    onlineButton.setVisibility(View.INVISIBLE);
                    popupMessage("Úspech", odpoved);
                }
            });


        }
        else Log.e("ERROR","Screen 'Homepage' could not be initialized.");

    }
}
