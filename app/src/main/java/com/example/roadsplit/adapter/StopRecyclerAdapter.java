package com.example.roadsplit.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roadsplit.R;
import com.example.roadsplit.helperclasses.ItemMoveCallback;
import com.example.roadsplit.model.Stop;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class StopRecyclerAdapter extends RecyclerView.Adapter<StopRecyclerAdapter.RecentsViewHolder> implements ItemMoveCallback.ItemTouchHelperContract {

    private final List<Stop> recentsDataList;
    private Context context;
    private RecentsViewHolder viewHolder;

    public StopRecyclerAdapter(Context context, List<Stop> recentsDataList) {
        this.context = context;
        this.recentsDataList = recentsDataList;
    }

    @NonNull
    @Override
    public StopRecyclerAdapter.RecentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.zsuebersichtlist, parent, false);
        this.viewHolder = new RecentsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StopRecyclerAdapter.RecentsViewHolder holder, int position) {
        Stop stop = recentsDataList.get(position);

        viewHolder.name.setText(stop.getName());

        BigDecimal current = stop.getBudget();
        if (current == null) current = BigDecimal.ZERO;
        viewHolder.budget.setText(current.toString());

        current = stop.getGesamtausgaben();
        if (current == null) current = BigDecimal.ZERO;
        viewHolder.ausgaben.setText("Ausgegeben: " + current.toString());

        viewHolder.budget.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    BigDecimal budget = new BigDecimal(editable.toString());
                    stop.setBudget(budget);
                } catch (Exception e) {
                    Log.d("ZwischenstopRecycler", "Fehler beim parsen des Textfeld to BigDecimal, Stacktrace: " + e.getMessage());
                }

            }
        });

    }

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(recentsDataList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(recentsDataList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onRowSelected(RecentsViewHolder myViewHolder) {
        myViewHolder.overview.setBackgroundColor(Color.GRAY);

    }

    @Override
    public void onRowClear(RecentsViewHolder myViewHolder) {
        myViewHolder.overview.setBackgroundColor(Color.WHITE);

    }

    @Override
    public int getItemCount() {
        return recentsDataList.size();
    }

    public static final class RecentsViewHolder extends RecyclerView.ViewHolder {

        View overview;
        TextView name;
        TextView ausgaben;
        EditText budget;

        public RecentsViewHolder(@NonNull View itemView) {
            super(itemView);

            overview = itemView;
            name = itemView.findViewById(R.id.textViewZwName);
            ausgaben = itemView.findViewById(R.id.textViewZwDesc);
            budget = itemView.findViewById(R.id.textViewZwBudget);

        }
    }
}
