package com.example.roadsplit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roadsplit.EndpointConnector;
import com.example.roadsplit.R;
import com.example.roadsplit.model.finanzen.AusgabenReport;
import com.example.roadsplit.requests.DownloadRequest;

import java.util.List;

import okhttp3.Callback;

public class DokumenteRecyclerAdapter extends RecyclerView.Adapter<DokumenteRecyclerAdapter.RecentsViewHolder> {

    private Context context;
    private List<String> fileNames;

    private RecentsViewHolder viewHolder;

    private ProgressBar dokumentProgressBar;
    private AusgabenReport ausgabenReport;
    private Callback callback;


    public DokumenteRecyclerAdapter(Context context, List<String> fileNames, ProgressBar dokumentProgressBar, AusgabenReport ausgabenReport, Callback callback) {
        this.context = context;
        this.fileNames = fileNames;
        this.dokumentProgressBar = dokumentProgressBar;
        this.ausgabenReport = ausgabenReport;
        this.callback = callback;
    }

    @NonNull
    @Override
    public DokumenteRecyclerAdapter.RecentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.packlistenitem, parent, false);
        this.viewHolder = new DokumenteRecyclerAdapter.RecentsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DokumenteRecyclerAdapter.RecentsViewHolder holder, int position) {
        String fileName = fileNames.get(position);

        holder.download.setOnClickListener(view -> {
            dokumentProgressBar.setVisibility(View.VISIBLE);
            DownloadRequest downloadRequest = new DownloadRequest();
            downloadRequest.setReiseId(ausgabenReport.getReise().getId());
            downloadRequest.setFilename(fileName);
            EndpointConnector.downloadFile(downloadRequest, callback, context);
        });

        holder.name.setText(fileName);
        if (fileName.endsWith(".pdf")) {
            holder.image.setImageResource(R.drawable.bookmarkicon);
        } else {
            holder.image.setImageResource(R.drawable.camera);
        }

        holder.download.setForeground(context.getResources().getDrawable(R.drawable.download_black_24dp));

    }

    @Override
    public int getItemCount() {
        return fileNames.size();
    }

    public List<String> getFileNames() {
        return fileNames;
    }

    public static final class RecentsViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView image;
        Button download;

        public RecentsViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.packlistenItemName);
            image = itemView.findViewById(R.id.packlistenImageView);
            download = itemView.findViewById(R.id.deletePacklistenItem);
        }
    }
}