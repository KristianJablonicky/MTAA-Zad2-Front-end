package mtaa.java;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterWorkerActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_register_worker);

        Button registerTlacidlo = (Button)findViewById(R.id.button_createCompany);
        registerTlacidlo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Switch workerType = (Switch) findViewById(R.id.switch_zamestnavatel);

                String urlString = "/postUser/";

                if(workerType.isChecked()){
                    Log.i("zamestnavatel", "TRUE");
                    urlString += "E/";
                }
                else {
                    Log.i("zamestnavatel", "FALSE");
                    urlString += "W/";
                }

                EditText menoInput = (EditText) findViewById(R.id.textInput_menoRegistracia);
                String meno = menoInput.getText().toString();
                if(meno.length() < 1){
                    popupMessage("Chyba!", "??daj Meno mus?? by?? vlpnen??");
                    return;
                }
                else{
                    urlString += meno + "/";
                }

                EditText hesloInput = (EditText) findViewById(R.id.textInput_hesloRegistracia);
                String heslo = hesloInput.getText().toString();
                if(heslo.length() < 1){
                    popupMessage("Chyba!", "??daj Heslo mus?? by?? vlpnen??");
                    return;
                }
                else{
                    urlString += heslo + "/";
                }

                EditText companyInput = (EditText) findViewById(R.id.textInput_company);
                String company = companyInput.getText().toString();

                if (workerType.isChecked()){
                    if(company.length() < 1){
                        popupMessage("Chyba!", "??daj Solo??nos?? mus?? by?? vlpnen??,\n" +
                                "v pr??pade, ??e pou????vate?? je zamestn??vate??");
                        return;
                    }
                    else{
                        urlString += company + "/";
                    }
                }

                EditText dateInput = (EditText) findViewById(R.id.textInput_date);
                String date = dateInput.getText().toString();
                if(date.length() > 0){
                    urlString += "date=" + date + "/";
                }

                EditText emailInput = (EditText) findViewById(R.id.textInput_email);
                String email = emailInput.getText().toString();
                if(email.length() > 0){
                    urlString += "email=" + email + "/";
                }

                EditText phoneInput = (EditText) findViewById(R.id.textInput_phone);
                String phone = phoneInput.getText().toString();
                if(phone.length() > 0){
                    urlString += "phone=" + phone + "/";
                }

                //inputy nacitane, ide sa poslat request

                Requests objekt = new Requests();
                String response = objekt.OTHER_request("POST", urlString);
                int responseCode = Integer.parseInt(response);

                if (responseCode == 404 && workerType.isChecked())
                    popupMessage("Chyba!", "Spolo??nos?? s n??zvom " + company + " nie je evidovan?? v datab??ze.\n" +
                            "Pridajte ju do na??ej datab??zy, alebo skontrolujte, ??i ste n??zov zadali spr??vne.");

                else if (responseCode >= 400){
                    popupMessage("Chyba!", "Pou????vate?? s menom " + meno + " u?? existuje," +
                            "\nalebo ste zadali nespr??vne udaje.");
                }
                else {
                    startActivity(new Intent(RegisterWorkerActivity.this, MainActivity.class));
                }

                Log.i("response", String.valueOf(response));

            }
        });

        Button spolocnostTlacidlo = (Button)findViewById(R.id.button_registerCompany);
        spolocnostTlacidlo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterWorkerActivity.this, RegisterCompanyActivity.class));
            }
        });
    }
}
