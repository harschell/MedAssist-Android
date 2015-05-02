package com.example.root.medassist.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.root.medassist.R;
import com.example.root.medassist.app.AppController;
import com.example.root.medassist.model.Doctor;

import java.util.List;

public class CustomListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Doctor> docItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CustomListAdapter(Activity activity, List<Doctor> movieItems) {
        this.activity = activity;
        this.docItems = movieItems;
    }

    @Override
    public int getCount() {
        return docItems.size();
    }

    @Override
    public Object getItem(int location) {
        return docItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) convertView
                .findViewById(R.id.thumbnail);
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView rating = (TextView) convertView.findViewById(R.id.rating);
        TextView speciality = (TextView) convertView.findViewById(R.id.speciality);
        TextView consultation = (TextView) convertView.findViewById(R.id.consultation);
        TextView registered = (TextView) convertView.findViewById(R.id.registered);
        //TextView address = (TextView) convertView.findViewById(R.id.address);
        // getting movie data for the row
        Doctor m = docItems.get(position);

        // thumbnail image
        thumbNail.setImageUrl(m.getThumbnailUrl(), imageLoader);

        // title
        title.setText(m.getTitle());

        // rating
        rating.setText("Rating: " + String.valueOf(m.getRating()));

        // genre
        /*String specialityStr = "";
        for (String str : m.getSpeciality()) {
            specialityStr += str + ", ";
        }
        specialityStr = specialityStr.length() > 0 ? specialityStr.substring(0,
                specialityStr.length() - 2) : specialityStr;
        speciality.setText(specialityStr);
        */
        speciality.setText("Speciality: " + m.getSpeciality());
        // release year
        //address.setText(String.valueOf(m.getAddress()));
        consultation.setText("Consultation Fee: " + String.valueOf(m.getConsultation()));
        //phoneNum.setText(String.valueOf(m.getPhone()));
        registered.setText("Verified: " + String.valueOf(m.getRegistration()));
        return convertView;
    }

}