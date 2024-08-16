package com.example.ecommerce.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.databinding.ViewholderSectionRouletteBinding;
import com.example.ecommerce.databinding.ViewholderUsersBinding;
import com.example.ecommerce.domain.RouletteSection;

import java.util.ArrayList;

public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.ViewHolder> {

    private ArrayList<RouletteSection> sections;
    private int selectedPosition = -1;
    private OnItemClickListener listener;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public SectionAdapter(ArrayList<RouletteSection> sections, Context context, OnItemClickListener listener) {
        this.sections = sections;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderSectionRouletteBinding binding = ViewholderSectionRouletteBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RouletteSection section = sections.get(position);
        holder.binding.gift.setText(section.getGift());
        holder.binding.percentage.setText(section.getPercentage());

        // Set the background color based on selection
        if (selectedPosition == position) {
            holder.binding.allItem.setBackgroundColor(Color.parseColor("#B4E380"));
        } else {
            holder.binding.allItem.setBackgroundColor(Color.parseColor("#c7cffb"));
        }

        // Set the click listener for the item
        holder.binding.allItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Notify the change for the previously selected item
                notifyItemChanged(selectedPosition);

                // Update the selected position and notify the change
                selectedPosition = holder.getAdapterPosition();
                notifyItemChanged(selectedPosition);

                // Notify the listener
                if (listener != null) {
                    listener.onItemClick(selectedPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return sections.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ViewholderSectionRouletteBinding binding;

        public ViewHolder(ViewholderSectionRouletteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
