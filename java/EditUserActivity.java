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

    private void uploadPDF(Uri uri, User u)
    {
        String [] response = u.uploadPDF(uri);

        popupMessage(response[0], response[1]);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {

            TextView menoView = (TextView) findViewById(R.id.textInput_menoEdit);
            TextView hesloView = (TextView) findViewById(R.id.textInput_hesloEdit);
            TextView emailView = (TextView) findViewById(R.id.textInput_emailEdit);
            TextView phoneView = (TextView) findViewById(R.id.textInput_phoneEdit);
            TextView dateView = (TextView) findViewById(R.id.textInput_dateEdit);

            User u = (User) extras.get("currentUser");

            menoView.setHint(u.getName());
            hesloView.setHint(u.getPassword());
            if (u.getEmail() != null)
                emailView.setHint(u.getEmail());
            if (u.getPhone() != null)
                phoneView.setHint(u.getPhone());
            if (u.getBirthday() != null) {
                dateView.setHint(u.getBirthday());
            }

            Button saveTlacidlo = (Button) findViewById(R.id.button_save);
            saveTlacidlo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // najaktualnejsie udaje pouzivatela pred potencialnym odpojenim
                    if(!u.isOffline()) u.setPovodneUdaje(u.getName() + "/" + u.getPassword());

                    String urlString;

                    if (!u.isEmployer()) urlString = "/putWorker/" + u.getName() + "/" + u.getPassword() + "/";
                    else urlString = "/putEmployer/" + u.getName() + "/" + u.getPassword() + "/";

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

                    // aktualizacia objektu User
                    if (meno.length()>0)  u.setName(meno);
                    if (heslo.length()>0) u.setPassword(heslo);
                    if (email.length()>0) u.setEmail(email);
                    if (phone.length()>0) u.setPhone(phone);
                    if (date.length()>0) u.setBirthday(date);

                    menoView.setText("");
                    hesloView.setText("");
                    emailView.setText("");
                    phoneView.setText("");
                    dateView.setText("");

                    menoView.setHint(u.getName());
                    hesloView.setHint(u.getPassword());
                    if (u.getEmail() != null) emailView.setHint(u.getEmail());
                    if (u.getPhone() != null) phoneView.setHint(u.getPhone());
                    if (u.getBirthday() != null) dateView.setHint(u.getBirthday());


                    //inputy nacitane, ide sa poslat request
                    Log.i("url", urlString);

                    if (!u.isOffline())
                    {
                        String response = Requests.OTHER_request("PUT", urlString);
                        int responseCode = Integer.parseInt(response);

                        if (responseCode >= 400)
                        {
                            if (responseCode == 408)
                            {
                                u.setOfflineMode(true);
                                popupMessage("Chyba", "Nepodarilo sa nadviazať konverzáciu so serverom.\n" + "Zapína sa offline mód.");
                            }
                            else popupMessage("Chyba", "HTTP error kod: " + response);
                        }
                        else popupMessage("Úspech", "HTTP kod: " + response);
                    }

                    /*
                    else {
                        popupMessage("Info:", "Ste v offline móde, skúste najskôr obnoviť pripojenie.\n" +
                                "Po úspešnom obnovení sa vaše zmeny automaticky nahrajú na server.");
                    }
                    */
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
