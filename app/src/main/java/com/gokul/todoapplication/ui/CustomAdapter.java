package com.gokul.todoapplication.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.core.content.ContextCompat;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gokul.todoapplication.R;
import com.gokul.todoapplication.db.DatabaseHelper;
import com.gokul.todoapplication.db.Note;

import java.util.ArrayList;



import static com.gokul.todoapplication.R.drawable.ic_delete_black;
import static com.gokul.todoapplication.R.drawable.ic_done_black;

class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
    ArrayList<Note> notes;
    Context context;
    DatabaseHelper databaseHelper;




    public CustomAdapter(Context context, ArrayList<Note> notes, DatabaseHelper databaseHelper){
        this.context=context;
        this.databaseHelper=databaseHelper;

        this.notes=notes;

    }



    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note_card, parent, false);
        // set the view's size, margins, paddings and layout parameters
        MyViewHolder myViewHolder = new MyViewHolder(view); // pass the view to View Holder
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Note note = notes.get(position);
        Log.d("asdfprioritu",note.getName()+" "+note.getPriority());
        if(note.getStatus()==1) {
            holder.undo.setVisibility(View.GONE);
            holder.status.setBackground(ContextCompat.getDrawable(context, ic_done_black));
            holder.name.setText(note.getName());
        }
        else {
            holder.status.setBackground(ContextCompat.getDrawable(context, ic_delete_black));
            holder.undo.setVisibility(View.VISIBLE);
            holder.name.setText(note.getName());
            holder.name.setPaintFlags(holder.name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        if(note.getPriority()!=1){
            Log.d("asdf","Priority called");
            holder.priority.setVisibility(View.GONE);}

        holder.undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(databaseHelper.updateStatus(note.getId(),note.getName(),note.getPriority(),1))
                    trigger();
                else
                    Toast.makeText(context, "Undo operation Failed", Toast.LENGTH_SHORT).show();

            }
        });

        holder.status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int status = notes.get(position).getStatus();
                boolean flag;
                if(status==1){
                    flag = databaseHelper.updateStatus(note.getId(),note.getName(),note.getPriority(),0);
                    if(flag){
                        Toast.makeText(context, "Note Updated Successfully", Toast.LENGTH_SHORT).show();
                        trigger();
                    }
                    else
                        Toast.makeText(context, "Note Update Failed", Toast.LENGTH_SHORT).show();

                }

                else {
                    flag = databaseHelper.deteleNote(note.getId());
                    if(flag){
                        Toast.makeText(context, "Note Deleted Successfully", Toast.LENGTH_SHORT).show();
                        trigger();
                    }
                    else
                        Toast.makeText(context, "Note Delete Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.priority.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "This is a High Priority Note!", Toast.LENGTH_SHORT).show();
            }
        });




    }
    @Override
    public int getItemCount() {
        return notes.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;// init the item view's
        Button undo, status,priority;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_note_text);
            priority=itemView.findViewById(R.id.btn_priority);
            undo = itemView.findViewById(R.id.btn_undo);
            status = itemView.findViewById(R.id.btn_status);
        }
    }
    public void trigger(){
        Intent intent = new Intent("trigger");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
