package mtaa.java;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.text.Format;
import java.text.SimpleDateFormat;

import mtaa.java.data.User;

public class ViewUserActivity extends AppCompatActivity {

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
    public void onCreate(@Nullable Bundle savedInstanceState) {

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_view_profile);

            User u = (User) extras.get("currentUser");
            User uview = (User) extras.get("shownuser");

            TextView meno = (TextView) findViewById(R.id.text_view_meno);
            TextView email = (TextView) findViewById(R.id.text_view_email);
            TextView date = (TextView) findViewById(R.id.text_view_date);
            TextView phone = (TextView) findViewById(R.id.text_view_phone);
            TextView company = (TextView) findViewById(R.id.text_view_company);

            if(u != null && uview != null) {
                try {
                    User viewCiel = uview;

                    meno.setText(viewCiel.getName());
                    if(viewCiel.getEmail() != null)
                        email.setText(viewCiel.getEmail());
                    if(viewCiel.getPhone() != null)
                        phone.setText(viewCiel.getPhone());
                    if(viewCiel.getCompanyID() != -1) {
                        JSONObject spolocnost = Requests.GET_request("/getCompany/" + extras.get("id") + "/");
                        String spolocnostString = (String) spolocnost.get("name");
                        Log.i("Spolocnost", spolocnostString);
                        company.setText(spolocnostString);
                    }
                    if (viewCiel.getBirthday() != null) {

                        Format formatter = new SimpleDateFormat("yyyy-MM-dd");
                        String dateString = formatter.format(viewCiel.getBirthday());
                        date.setText(dateString);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else{
                Log.i("Error", "pouzivatel s danym ID neexistuje");
                return;
            }

            Button pdfTlacidlo = (Button) findViewById(R.id.button_view_PDF);
            if(uview.getCompanyID() != -1) {
                pdfTlacidlo.setVisibility(View.INVISIBLE);
                pdfTlacidlo.setVisibility(View.GONE);
            }
            pdfTlacidlo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Requests request = new Requests();
                    if(request.PDF_GET_request("/getPDF/" + uview.getId().toString() + "/", uview.getName()))
                        popupMessage("Úspech", "Životopis bol úspešne stiahnutý.");
                    else
                        popupMessage("Chyba", "Používateľ nemá zverejnený životopis," +
                                "\nalebo nastala chyba pri sťahovaní súboru.");


                }
            });

        }
        else
            Log.i("Error", "extras = null");
    }

}
