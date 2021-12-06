package com.tw4rs.notesuni9.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tw4rs.notesuni9.R;
import com.tw4rs.notesuni9.model.NoteModel;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> {

    private List<NoteModel> list;

    public NoteAdapter(List<NoteModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_items,
                        parent,
                        false);

        return new NoteHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
        holder.message.setText(list.get(position).getMessage());


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class NoteHolder extends RecyclerView.ViewHolder{
        private TextView message;

        public NoteHolder(@NonNull View itemView) {
            super(itemView);

            message = itemView.findViewById(R.id.messageItems);

        }
    }


}
