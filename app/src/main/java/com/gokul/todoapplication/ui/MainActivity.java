package com.gokul.todoapplication.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.gokul.todoapplication.R;
import com.gokul.todoapplication.db.DatabaseHelper;
import com.gokul.todoapplication.db.Note;

import org.w3c.dom.Text;

import java.util.ArrayList;



public class MainActivity extends AppCompatActivity {
    DatabaseHelper databaseHelper;
    EditText noteEditText,editText;

    Switch aSwitch;

    Button saveNoteButton;

    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    CustomAdapter customAdapter;
    Context context;
    int priorityValue;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);
        context = this;
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        setSupportActionBar(toolbar);
        // get the reference of RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        populate();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("trigger"));

        findViewById(R.id.btn_add_note).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                final ViewGroup viewGroup = findViewById(android.R.id.content);
                final View dialogView = LayoutInflater.from(context).inflate(R.layout.custom_dialogbox, viewGroup, false);
                builder.setView(dialogView);
                saveNoteButton = dialogView.findViewById(R.id.savenote);
                noteEditText = dialogView.findViewById(R.id.note_text);
                aSwitch = dialogView.findViewById(R.id.switch_priority);
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();
                noteEditText.setText("");

                saveNoteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String note = noteEditText.getText().toString().trim();

                        if(note.equals(""))
                            Toast.makeText(MainActivity.this, "Note is Empty", Toast.LENGTH_SHORT).show();
                        else{
                            if(aSwitch.isChecked())
                                priorityValue=1;
                            else
                                priorityValue=0;
                            Log.d("asdfNoteCreation",note+" "+priorityValue);
                            boolean noteCreated = databaseHelper.insertNote(note,priorityValue);

                            if(noteCreated==true){
                                Toast.makeText(MainActivity.this, "Note created successfully", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                                populate();
                            }
                            else{
                                Toast.makeText(MainActivity.this, "Note creation Failed", Toast.LENGTH_SHORT).show();

                            }

                        }
                    }
                }); }});

            findViewById(R.id.btn_search).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    final ViewGroup viewGroup = findViewById(android.R.id.content);
                    final View searchdialogView = LayoutInflater.from(context).inflate(R.layout.custom_dialogbox_search, viewGroup, false);
                    builder.setView(searchdialogView);
                    final AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    editText= searchdialogView.findViewById(R.id.et_search_note);
                    editText.setText("");
                    searchdialogView.findViewById(R.id.btn_search).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String text = editText.getText().toString().trim();
                            if(text.equals(""))
                                Toast.makeText(MainActivity.this, "Type something!", Toast.LENGTH_SHORT).show();

                            else{
                                alertDialog.dismiss();
                                viewGroup.removeView(searchdialogView);
                                searchData(text);

                            }
                        }
                    });
                }
            });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        populate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        populate();
    }

    void populate(){
        Log.d("asdf","populate called");
        Cursor cursor = databaseHelper.getAllData();
        ArrayList<Note> notes = new ArrayList<>();
        while (cursor.moveToNext()){
            Note note = new Note(cursor.getInt(0),cursor.
                    getString(1),cursor.getInt(2),cursor.getInt(3));
            notes.add(note);
        }
        customAdapter = new CustomAdapter(MainActivity.this,notes,databaseHelper);
        recyclerView.setAdapter(customAdapter);

    }
    void searchData(String text){
        Cursor cursor = databaseHelper.searchNotes(text);
        ArrayList<Note> notes = new ArrayList<>();
        while (cursor.moveToNext()){
            Note note = new Note(cursor.getInt(0),cursor.
                    getString(1),cursor.getInt(2),cursor.getInt(3));
            notes.add(note);
        }
        customAdapter = new CustomAdapter(MainActivity.this,notes,databaseHelper);
        recyclerView.setAdapter(customAdapter);
    }
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            populate();
        }
    };




}
