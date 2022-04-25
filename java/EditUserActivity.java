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

import org.json.JSONObject;
import java.text.Format;
import java.text.SimpleDateFormat;




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
                Format formatter = new SimpleDateFormat("yyyy-MM-dd");
                String dateString = formatter.format(u.getBirthday());
                date.setHint(dateString);
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

                    String response = objekt.OTHER_request("PUT", urlString);

                    int responseCode = Integer.parseInt(response);

                    if (responseCode >= 400) {
                        //popupMessage("Chyba!", "Pri aktualizovaní údajov došlo ku chybe");
                        popupMessage("Chyba", "HTTP error kod: " + response);
                    } else {
                        //popupMessage("Úspech", "Vaše údaje boli pspešne nahrané do databázy.");
                        popupMessage("Úspech", "HTTP kod: " + response);
                    }
                    //--aktualizacia objektu User

                    JSONObject pouzivatel = Requests.GET_request("/getUser/" + meno + "/" + heslo + "/");

                    if (pouzivatel == null) {
                        popupMessage("Chyba!", "Nesprávne meno alebo heslo.");
                    } else {
                        Log.i("vysledok", pouzivatel.toString());
                        try {
                            User u = new User(pouzivatel);
                            Intent i = new Intent(EditUserActivity.this, EditUserActivity.class);
                            i.putExtra("currentUser", u);
                            startActivity(i);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    Log.i("response", String.valueOf(response));

                }

            });

            Button logoutlacidlo = (Button) findViewById(R.id.button_logout);
            logoutlacidlo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(EditUserActivity.this, MainActivity.class));
                }
            });

            Button odstranitTlacidlo = (Button) findViewById(R.id.button_odstran);
            odstranitTlacidlo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Requests objekt = new Requests();
                    String type = "E";
                    if(u.getCompanyID() == -1)
                        type = "W";

                    String vysledok = objekt.OTHER_request("DELETE","/delUser/" + type + "/" + u.getName() + "/" + u.getPassword() + "/");
                    int responseCode = Integer.parseInt(vysledok);
                    if(responseCode >= 400){
                        //popupMessage("Chyba!", "Pri odstraňovaní používateľa z databázy došlo ku chybe");
                        popupMessage("Chyba!", "HTTP erroe kod: " + vysledok);
                    }
                    else{
                        startActivity(new Intent(EditUserActivity.this, MainActivity.class));
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
                            Requests objekt = new Requests();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                if(objekt.PDF_POST_request(uri.getPath(), "/postPDF/" + u.getName() + "/" + u.getPassword() + "/") <=200)
                                    popupMessage("Úspech", "Životopis bol úspešne uverejnený.");
                                else
                                    popupMessage("Chyba", "Niekde nastala chyba.");
                            }
                            else
                                popupMessage("Chyba", "Vaša verzia Androidu nepodpruje túto funkcionalitu.");
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

            Button ziadostiTlacidlo = (Button) findViewById(R.id.button_ziadosti);
            ziadostiTlacidlo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(EditUserActivity.this, HomeActivity.class);
                    i.putExtra("currentUser", u);
                    startActivity(i);

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
