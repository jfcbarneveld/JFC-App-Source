package com.jfc;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MoreListAdapter extends ArrayAdapter<String>{
    MoreListAdapter(Context context, String[] items){
        super(context,R.layout.more_list_adapter,items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        final View customView;

        customView = inflater.inflate(R.layout.more_list_adapter,parent,false);

        ImageView imageView = (ImageView) customView.findViewById(R.id.listAdapterImageView);
        TextView textView = (TextView) customView.findViewById(R.id.listAdapterTextView);
        RelativeLayout layout = (RelativeLayout) customView.findViewById(R.id.listAdapterLayout);
        layout.setBackgroundColor(Color.parseColor(MainActivity.PRIMARY_COLOR));

        SharedPreferences sp = getContext().getSharedPreferences("MainSharedPreferences", Context.MODE_PRIVATE);

        textView.setText(sp.getString("listText"+position,""));
        textView.setTextColor(MainActivity.PRIMARY_TEXT_COLOR);

        BitmapDrawable bitmapDrawable = new BitmapDrawable(getContext().getResources(), getContext().getFilesDir().getAbsolutePath()+"/"+"listImage"+position+".png");
        imageView.setImageDrawable(bitmapDrawable);

        return customView;
    }
}
