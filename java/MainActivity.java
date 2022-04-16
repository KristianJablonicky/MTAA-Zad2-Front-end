package mtaa.java;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.StrictMode;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import mtaa.java.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.util.Log;
import android.widget.EditText;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;


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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        Button loginTlacidlo = (Button) findViewById(R.id.button_login);
        loginTlacidlo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                EditText menoInput = (EditText) findViewById(R.id.textInput_meno);
                EditText hesloInput = (EditText) findViewById(R.id.textInput_heslo);

                String meno = menoInput.getText().toString();
                String heslo = hesloInput.getText().toString();
                // volanie za pomoci inej classy

                JSONObject vysledok = new JSONObject();

                volania getUserObjekt = new volania();
                vysledok = getUserObjekt.getUser("http://" + getUserObjekt.IP + "/getUser/" + meno + "/" + heslo + "/");

                if (vysledok == null) {
                    popupMessage("Chyba!", "Nesprávne meno alebo heslo.");
                } else {
                    //vysledok.getString("password");
                    Log.i("vysledok", vysledok.toString());
                }

            }
        });

        Button registerTlacidlo = (Button)findViewById(R.id.button_register);
        registerTlacidlo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });


    }

}
