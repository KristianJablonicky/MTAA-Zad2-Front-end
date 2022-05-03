package mtaa.java;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.View;

import mtaa.java.data.User;
import mtaa.java.databinding.ActivityMainBinding;

import android.widget.Button;
import android.util.Log;
import android.widget.EditText;

import org.json.JSONObject;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

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

    private boolean checkPermission() {
        // checking of permissions.
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        // requesting permissions if not provided.
        final int PERMISSION_REQUEST_CODE = 200;
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ziskanie povoleni od pouzivatela
        final int READ_EXTERNAL_STORAGE = 112;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE);
        } else {
            Log.i("Permission", "uz boli udelene");
        }

        if (checkPermission()) {
            Log.i("Permission", "GRANTED");
        } else {
            requestPermission();
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Button registerTlacidlo = (Button)findViewById(R.id.button_register);
        Button loginTlacidlo = (Button) findViewById(R.id.button_login);

        loginTlacidlo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                EditText menoInput = (EditText) findViewById(R.id.textInput_meno);
                EditText hesloInput = (EditText) findViewById(R.id.textInput_heslo);

                String meno = menoInput.getText().toString();
                String heslo = hesloInput.getText().toString();

                JSONObject pouzivatel = Requests.GET_request("/getUser/" + meno + "/" + heslo + "/");


                if (pouzivatel == null)
                {
                    popupMessage("Chyba!", "Nesprávne meno alebo heslo.");
                }
                else
                {
                    Log.i("vysledok", pouzivatel.toString());

                    try
                    {
                        User u = new User(pouzivatel);
                        u.setOfflineMode(false);
                        u.setPovodnyURL(null);

                        Intent i = new Intent(MainActivity.this, HomeActivity.class);
                        i.putExtra("currentUser", u);
                        startActivity(i);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }


                }

            }
        });

        registerTlacidlo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegisterWorkerActivity.class));
            }
        });

    }

}
