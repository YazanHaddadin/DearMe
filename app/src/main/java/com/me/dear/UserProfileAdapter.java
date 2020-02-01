package com.me.dear;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class UserProfileAdapter extends ArrayAdapter<UserProfileInfo> {
    private List<UserProfileInfo> items;

    public UserProfileAdapter(Context context, ArrayList<UserProfileInfo> objects) {
        super(context, 0, objects);

        this.items = objects;
    }

    public View getView (int position, View convertView, ViewGroup parent) {
        UserProfileInfo item = items.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.view_user_profile, parent, false);
        }

        final TextView listTitle  = (TextView) convertView.findViewById(R.id.tvTitleOf);
        final TextView listInfo  = (TextView) convertView.findViewById(R.id.tvInfoOf);

        listTitle.setText(item.getTitle());
        listInfo.setText(item.getInfo());

        return convertView;
    }
}
