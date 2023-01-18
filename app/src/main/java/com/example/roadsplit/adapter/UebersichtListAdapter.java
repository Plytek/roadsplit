package com.example.roadsplit.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.roadsplit.R;
import com.example.roadsplit.model.Reise;
import java.util.List;
import java.util.Map;

public class UebersichtListAdapter extends ArrayAdapter<Reise> {

    private Map<Reise, Bitmap> reisenWithImages;

    // View lookup cache
    private static class ViewHolder {
        TextView name;
        ImageView image;
    }

    public UebersichtListAdapter(List<Reise> data, Context context, Map<Reise, Bitmap> urls) {
        super(context, R.layout.reiseuebersichtlist, data);
        this.reisenWithImages = urls;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Reise reise = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        UebersichtListAdapter.ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {
            viewHolder = new UebersichtListAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.reiseuebersichtlist, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.uebersichtTextView);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.locationImageView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (UebersichtListAdapter.ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(reise.getName());
        if(reisenWithImages.get(reise) != null) {
            viewHolder.image.setImageBitmap(reisenWithImages.get(reise));
        }
        // Return the completed view to render on screen
        return convertView;
    }

}
