package mtaa.java;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import mtaa.java.data.User;
import mtaa.java.databinding.ActivityMainBinding;

import android.widget.Button;
import android.util.Log;
import android.widget.EditText;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    //private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    public void popupMessage(String nadpis, String sprava){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(sprava);
        alertDialogBuilder.setTitle(nadpis);
        alertDialogBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener(){

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

                        Log.i("name", u.getName());
                        Log.i("pass",u.getPassword());
                        Log.i("phone",u.getPhone());
                        Log.i("email",u.getEmail());
                        Log.i("bday", String.valueOf(u.getBirthday()));

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
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });

        /*
        userSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) buttonView.setText("Pracovník");
                else buttonView.setText("Zamestnávateľ");
            }
        });
         */

    }

}
