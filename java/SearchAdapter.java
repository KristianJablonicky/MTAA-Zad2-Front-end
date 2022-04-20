package mtaa.java;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import mtaa.java.data.JobOffer;
import mtaa.java.data.User;

public class SearchAdapter extends ArrayAdapter<Object> {

    Context context;
    LayoutInflater inflater;

    ArrayList<User> userList = null;
    ArrayList<JobOffer> offerList = null;

    public SearchAdapter(Context applicationContext)
    {
        super(applicationContext,R.layout.activity_list_search);
        this.context = context;
        inflater = (LayoutInflater.from(applicationContext));
    }

    public ArrayList<User> getUserList() {
        return userList;
    }

    public void setUserList(ArrayList<User> userList) {
        this.userList = userList;
        this.offerList = null;
    }

    public ArrayList<JobOffer> getOfferList() {
        return offerList;
    }

    public void setOfferList(ArrayList<JobOffer> offerList) {
        this.userList = null;
        this.offerList = offerList;
    }


    @Override
    public int getCount() {
        if (offerList != null) return offerList.size();
        else if (userList != null) return userList.size();
        else return 0;
    }

    @Override
    public Object getItem(int i) {
        try
        {
            if (this.offerList != null) return offerList.get(i);
            else if (userList != null) return userList.get(i);
            else return null;
        }
        catch (Exception e) {
            return null;
        }
    }


    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        //LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.activity_list_search, null, true);

        TextView txt1 = (TextView) rowView.findViewById(R.id.TF1);
        TextView txt2 = (TextView) rowView.findViewById(R.id.TF2);
        TextView txt1h = (TextView) rowView.findViewById(R.id.TF1help);
        TextView txt2h = (TextView) rowView.findViewById(R.id.TF2help);

        ImageView userIcon = (ImageView) rowView.findViewById(R.id.icon);


        if (userList != null)
        {
            txt2.setVisibility(View.GONE);
            txt2h.setVisibility(View.GONE);
            userIcon.setVisibility(View.VISIBLE);

            txt1h.setText("Meno:");
            txt1.setText(userList.get(i).getName());

            if (userList.get(i).isEmployer()) userIcon.setImageResource(R.drawable.icon_employer);
            else userIcon.setImageResource(R.drawable.icon_worker);
        }
        else if (offerList != null)
        {
            txt2.setVisibility(View.VISIBLE);
            txt2h.setVisibility(View.VISIBLE);
            userIcon.setVisibility(View.GONE);

            txt1h.setText("Názov:");
            txt1.setText(offerList.get(i).getName());

            txt2h.setText("Oblasť:");
            txt2.setText(offerList.get(i).getField());
        }



        return rowView;
    }

}
