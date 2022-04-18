package mtaa.java;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import mtaa.java.data.User;
import mtaa.java.databinding.ActivityHomeBinding;
import mtaa.java.databinding.ActivityMainBinding;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        TextView username = (TextView)findViewById(R.id.TFusername);
        LinearLayout extrabuttons = (LinearLayout)findViewById(R.id.LLbuttons);

        Bundle extras = getIntent().getExtras();

        if (extras != null)
        {
            User u = (User) extras.get("currentUser");
            username.setText(u.getName());

            if (!u.isEmployer())
            {
                extrabuttons.setVisibility(View.INVISIBLE);
                extrabuttons.setVisibility(View.GONE);
                //extrabuttons.setLayoutParams();
            }

        }
        else Log.e("ERROR","Screen 'Homepage' could not be initialized.");



    }
}