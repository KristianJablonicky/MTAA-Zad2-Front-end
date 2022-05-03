package mtaa.java;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import mtaa.java.data.User;

public class EditUserActivity extends AppCompatActivity {

    public void popupMessage(String nadpis, String sprava){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(sprava);
        alertDialogBuilder.setTitle(nadpis);
        alertDialogBuilder.setNegativeButton("ok", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(nadpis,"Ok button stlaceny");
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void uploadPDF(Uri uri, User u) {
        if(!u.isOffline()) {
            Requests objekt = new Requests();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (objekt.PDF_POST_request(uri.getPath(), "/postPDF/" + u.getName() + "/" + u.getPassword() + "/") <= 200)
                    popupMessage("Úspech", "Životopis bol úspešne uverejnený.");
                else
                    popupMessage("Chyba", "Niekde nastala chyba.");
            } else
                popupMessage("Chyba", "Vaša verzia Androidu nepodpruje túto funkcionalitu.");
        }
        else{
            u.setZivotopisURI(uri);
            popupMessage("Info:", "Zvolený súbor sa uverejní, až keď obnovíte pripojenie.");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {

            TextView meno = (TextView) findViewById(R.id.textInput_menoEdit);
            TextView heslo = (TextView) findViewById(R.id.textInput_hesloEdit);
            TextView email = (TextView) findViewById(R.id.textInput_emailEdit);
            TextView phone = (TextView) findViewById(R.id.textInput_phoneEdit);
            TextView date = (TextView) findViewById(R.id.textInput_dateEdit);

            User u = (User) extras.get("currentUser");

            meno.setHint(u.getName());
            heslo.setHint(u.getPassword());
            if (u.getEmail() != null)
                email.setHint(u.getEmail());
            if (u.getPhone() != null)
                phone.setHint(u.getPhone());
            if (u.getBirthday() != null) {
                date.setHint(u.getBirthday());
            }

            Button goOnlineTlacidlo = (Button) findViewById(R.id.button_editGoOnline);
            if(!u.isOffline()){ //tlacidlo na obnovenie pripojenia je viditelne iba ak je pouzivatel offline
                goOnlineTlacidlo.setVisibility(View.INVISIBLE);
                goOnlineTlacidlo.setVisibility(View.GONE);
            }

            Button saveTlacidlo = (Button) findViewById(R.id.button_save);
            saveTlacidlo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String urlString = null;

                    boolean zamestnavatel = true;
                    if (u.getCompanyID() == -1)
                        zamestnavatel = false;

                    if (!zamestnavatel) {
                        urlString = "/putWorker/" + u.getName() + "/" + u.getPassword() + "/";
                    } else {
                        urlString = "/putEmployer/" + u.getName() + "/" + u.getPassword() + "/";
                    }

                    if(!u.isOffline()) // najaktualnejsie udaje pouzivatela pred potencialnym odpojenim
                        u.setPovodnyURL(urlString);

                    EditText menoInput = (EditText) findViewById(R.id.textInput_menoEdit);
                    String meno = menoInput.getText().toString();
                    if (meno.length() > 0) {
                        urlString += "name=" + meno + "/";
                    }
                    else
                        meno = u.getName();

                    EditText hesloInput = (EditText) findViewById(R.id.textInput_hesloEdit);
                    String heslo = hesloInput.getText().toString();
                    if (heslo.length() > 0) {
                        urlString += "password=" + heslo + "/";
                    }
                    else
                        heslo = u.getPassword();

                    EditText dateInput = (EditText) findViewById(R.id.textInput_dateEdit);
                    String date = dateInput.getText().toString();
                    if (date.length() > 0) {
                        urlString += "date=" + date + "/";
                    }

                    EditText emailInput = (EditText) findViewById(R.id.textInput_emailEdit);
                    String email = emailInput.getText().toString();
                    if (email.length() > 0) {
                        urlString += "email=" + email + "/";
                    }

                    EditText phoneInput = (EditText) findViewById(R.id.textInput_phoneEdit);
                    String phone = phoneInput.getText().toString();
                    if (phone.length() > 0) {
                        urlString += "phone=" + phone + "/";
                    }

                    //inputy nacitane, ide sa poslat request

                    Requests objekt = new Requests();

                    Log.i("url", urlString);

                    String response = null;
                    if(!u.isOffline()){
                        response = objekt.OTHER_request("PUT", urlString);
                        int responseCode = Integer.parseInt(response);

                        if (responseCode >= 400) {
                            if (responseCode == 408){
                                goOnlineTlacidlo.setVisibility(View.VISIBLE);
                                u.setOfflineMode(true);
                                popupMessage("Chyba", "Nepodarilo sa nadviazať konverzáciu so serverom.\n" +
                                        "Zapína sa offline mód.");
                            }
                            else
                                popupMessage("Chyba", "HTTP error kod: " + response);
                        } else {
                            //popupMessage("Úspech", "Vaše údaje boli pspešne nahrané do databázy.");
                            popupMessage("Úspech", "HTTP kod: " + response);
                        }
                    }
                    else {
                        popupMessage("Info:", "Ste v offline móde, skúste najskôr obnoviť pripojenie.\n" +
                                "Po úspešnom obnovení sa vaše zmeny automaticky nahrajú na server.");
                    }

                    //--aktualizacia objektu User

                    if (meno.length()>0)
                        u.setName(meno);
                    if(heslo.length()>0)
                        u.setPassword(heslo);
                    if(email.length()>0)
                        u.setEmail(email);
                    if(phone.length()>0)
                        u.setPhone(phone);
                    if(date.length()>0)
                        u.setBirthday(date);
                }

            });

            Button logoutlacidlo = (Button) findViewById(R.id.button_logout);
            logoutlacidlo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!u.isOffline())
                        startActivity(new Intent(EditUserActivity.this, MainActivity.class));
                    else
                        popupMessage("Chyba", "Ste v offline režime.");
                }
            });

            Button odstranitTlacidlo = (Button) findViewById(R.id.button_odstran);
            odstranitTlacidlo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!u.isOffline()) {
                        Requests objekt = new Requests();
                        String type = "E";
                        if (u.getCompanyID() == -1)
                            type = "W";

                        String vysledok = objekt.OTHER_request("DELETE", "/delUser/" + type + "/" + u.getName() + "/" + u.getPassword() + "/");
                        int responseCode = Integer.parseInt(vysledok);
                        if (responseCode >= 400) {
                            popupMessage("Chyba!", "HTTP error kod: " + vysledok);
                        } else {
                            startActivity(new Intent(EditUserActivity.this, MainActivity.class));
                        }
                    }
                    else{
                        popupMessage("Chyba", "Ste v offline režime, ak si chcete naozaj odstrániť účet tak obnovte svoje pripojenie.");
                    }
                }
            });

            Button zivotopisTlacidlo = (Button) findViewById(R.id.button_PDF);
            if(u.getCompanyID() != -1) {
                zivotopisTlacidlo.setVisibility(View.INVISIBLE);
                zivotopisTlacidlo.setVisibility(View.GONE);
            }

            final int READ_EXTERNAL_STORAGE = 112;
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE);
            } else {
                Log.i("Permission", "Permissions were granted.");
            }


            ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                    new ActivityResultCallback<Uri>() {
                        @Override
                        public void onActivityResult(Uri uri) {
                            uploadPDF(uri, u);
                        }
                    });

            zivotopisTlacidlo.setOnClickListener(new View.OnClickListener() {

                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View v) {

                    if(u.getCompanyID() != -1){
                        popupMessage("Chyba!", "Životopis môžu uverejniť iba zamestnanci.");
                        return;
                    }

                    mGetContent.launch("application/pdf");
                }
            });

            goOnlineTlacidlo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(Requests.GET_request("/findUsers/") == null){
                        popupMessage("Chyba", "Nepodarilo sa obnoviť pripojenie ku serveru.");
                        return;
                    }
                    u.setOfflineMode(false);
                    goOnlineTlacidlo.setVisibility(View.INVISIBLE);
                    goOnlineTlacidlo.setVisibility(View.GONE);

                    String urlString = u.getPovodnyURL();
                    urlString += "name=" + u.getName() + "/";
                    urlString += "password=" + u.getPassword() + "/";
                    if (u.getBirthday().length() > 0) {
                        urlString += "date=" + u.getBirthday() + "/";
                    }
                    if (u.getEmail().length() > 0) {
                        urlString += "email=" + u.getEmail() + "/";
                    }

                    if (u.getPhone().length() > 0) {
                        urlString += "phone=" + u.getPhone() + "/";
                    }

                    String odpoved = "Pripojenie bolo úspešne obnovené.";
                    String response = Requests.OTHER_request("PUT", urlString);
                    int responseCode = Integer.parseInt(response);

                    if (responseCode >= 400) {
                        odpoved += "\nVaše údaje zadané v offline móde sa nomohli nahrať na server.";
                        if(responseCode == 403)
                            popupMessage("Chyba", "Meno zadané v offline móde už existuje, zmeny vašich údajov neboli nahrané.");
                        else popupMessage("Chyba", "HTTP error kod: " + response);

                        if (responseCode == 408){
                            goOnlineTlacidlo.setVisibility(View.VISIBLE);
                            u.setOfflineMode(true);
                            return;
                        }
                    } else {
                        odpoved += "\nVaše údaje zadané v offline móde sa úspešne nahrali na server.";
                    }
                    if(u.getZivotopisURI() != null)
                        odpoved += "\nŽivotopis bol úspešne zverejnený.";

                    popupMessage("Úspech", odpoved);

                    if(u.getZivotopisURI() != null)
                        uploadPDF(u.getZivotopisURI(), u);
                    u.setZivotopisURI(null); // nabuduce sa uz zivotopis nemusi uploadovat


                }
            });
            Button spatTlacidlo = (Button) findViewById(R.id.button_edit_back);
            spatTlacidlo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(EditUserActivity.this, HomeActivity.class);
                    i.putExtra("currentUser", u);
                    startActivity(i);
                }
            });
        }
        else Log.e("ERROR","Screen 'Homepage' could not be initialized.");

    }
}
