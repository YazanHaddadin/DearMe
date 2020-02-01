package com.me.dear;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VoiceNotesAdapter extends ArrayAdapter<VoiceNotesInfo> {
    private List<VoiceNotesInfo> items;

    VoiceNotesAdapter(Context context, ArrayList<VoiceNotesInfo> objects) {
        super(context, 0, objects);

        this.items = objects;
    }

    public View getView (int position, View convertView, ViewGroup parent) {
        VoiceNotesInfo item = items.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.view_item, parent, false);
        }

        TextView listItemName  = (TextView) convertView.findViewById(R.id.itemName);
        TextView listItemPrice  = (TextView) convertView.findViewById(R.id.price);
        final ImageView  listItemImage    = (ImageView) convertView.findViewById(R.id.image);

        listItemName.setText(item.getName());
        listItemPrice.setText(item.getPrice());
        listItemImage.setImageResource(item.getImageId());

        listItemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer lI[] = {R.drawable.ic_launcher_background, R.drawable.ic_launcher_foreground, R.mipmap.ic_launcher};
                Random rand = new Random();
                listItemImage.setImageResource(lI[rand.nextInt(lI.length)]);
            }
        });

        return convertView;
    }
}
