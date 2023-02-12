package com.example.roadsplit.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roadsplit.R;
import com.example.roadsplit.model.PacklistenItem;

import java.util.List;

public class PacklistenRecyclerAdapter extends RecyclerView.Adapter<PacklistenRecyclerAdapter.RecentsViewHolder> {

    private Context context;
    private List<PacklistenItem> recentsDataList;

    private RecentsViewHolder viewHolder;

    public PacklistenRecyclerAdapter(Context context, List<PacklistenItem> recentsDataList) {
        this.context = context;
        this.recentsDataList = recentsDataList;
    }

    @NonNull
    @Override
    public PacklistenRecyclerAdapter.RecentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.packlistenitem, parent, false);
        this.viewHolder = new PacklistenRecyclerAdapter.RecentsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PacklistenRecyclerAdapter.RecentsViewHolder holder, int position) {
        PacklistenItem packlistenItem = recentsDataList.get(position);
        viewHolder.name.setText(packlistenItem.getItemname());
        if (packlistenItem.isDone()) {
            viewHolder.name.setPaintFlags(viewHolder.name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            viewHolder.name.setPaintFlags(viewHolder.name.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

    }

    @Override
    public int getItemCount() {
        return recentsDataList.size();
    }

    public static final class RecentsViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView image;

        public RecentsViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.packlistenItemName);
            image = itemView.findViewById(R.id.packlistenImageView);

        }
    }
}
