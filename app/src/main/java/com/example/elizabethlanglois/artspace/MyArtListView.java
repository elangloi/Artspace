package com.example.elizabethlanglois.artspace;


import android.app.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Color;


public class MyArtListView extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] maintitle;
    private final String[] subtitle;
    private final Integer[] imgid;

    public MyArtListView(Activity context, String[] maintitle,String[] subtitle, Integer[] imgid) {
        super(context, R.layout.my_list_item, maintitle);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.maintitle=maintitle;
        this.subtitle=subtitle;
        this.imgid=imgid;

    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.my_list_item, null,true);

        TextView titleText = (TextView) rowView.findViewById(R.id.artTitle);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView subtitleText = (TextView) rowView.findViewById(R.id.subtitle);

        titleText.setText(maintitle[position]);
        imageView.setImageResource(imgid[0]);
        subtitleText.setText(subtitle[position]);

        if(position %2 == 1)
        {
            // Set a background color for ListView regular row/item
            rowView.setBackgroundColor(Color.parseColor("#DCDCDC"));
        }else{
            rowView.setBackgroundColor(Color.parseColor("#778899"));
            titleText.setTextColor(Color.parseColor("#FFFFFF"));
            subtitleText.setTextColor(Color.parseColor("#FFFFFF"));

        }

        return rowView;

    };
}

/*credit:
https://www.javatpoint.com/android-custom-listview
 */