package com.gokul.todoapplication.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
    private DatabaseHelper databaseHelper;
    private EditText noteEditText,editText;

    private Switch aSwitch;

    private Button saveNoteButton;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private CustomAdapter customAdapter;
    private Context context;
    private int priorityValue;
    private SwipeRefreshLayout swipeContainer;



    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//--------------Initialization----------------------------------------------------------------
        //creating instance of SQLiteDatabase
        databaseHelper = new DatabaseHelper(this);
        context = this;
        //Instance of SwipeRefresh Layout
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        //Setting up ToolBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        setSupportActionBar(toolbar);

        //Setting up recyclerview adapter
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        //Registering Broadcast listener to listen change in Note
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("trigger"));

//---------------Handling UI -------------------
        //Displaying all Notes
        populate();

        //Add Note on toolbar
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

                //save note button inside dialog box
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

                            //Creating note calling database helper
                            boolean noteCreated = databaseHelper.insertNote(note,priorityValue);
                            //Handling responce from Database Helper
                            if(noteCreated==true){
                                Toast.makeText(MainActivity.this, "Note created successfully", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                                viewGroup.removeView(dialogView);
                                populate();
                            }
                            else{
                                Toast.makeText(MainActivity.this, "Note creation Failed", Toast.LENGTH_SHORT).show();

                            }

                        }
                    }
                }); }});

        //handling Search notes button on Toolbar
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

            //Handling Find priority notes button on Toolbar
            findViewById(R.id.btn_priority_find).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getPriorityNotes();
                }
            });

            //Refreshing notes when clicked on Toolbar Title
            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    populate();
                }
            });

            //Swipe down to refresh the Recycler view adapter
            swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    populate();
                    if (swipeContainer.isRefreshing()) {
                        swipeContainer.setRefreshing(false);
                    }
                }
            });

    }
//-------------------- FUNCTIONS-----------------------------------------    //
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

    //Function to display all notes
    private void populate(){
        Log.d("asdf","populate called");
        Cursor cursor = databaseHelper.getAllData();
        if(cursor.getCount()==0)
            Toast.makeText(context, "Uh oh! No notes found.", Toast.LENGTH_SHORT).show();
        ArrayList<Note> notes = new ArrayList<>();
        while (cursor.moveToNext()){
            Note note = new Note(cursor.getInt(0),cursor.
                    getString(1),cursor.getInt(2),cursor.getInt(3));
            notes.add(note);
        }
        customAdapter = new CustomAdapter(MainActivity.this,notes,databaseHelper);
        recyclerView.setAdapter(customAdapter);

    }

    //Function to display Searched Notes
    private void searchData(String text){
        Cursor cursor = databaseHelper.searchNotes(text);
        if(cursor.getCount()==0){
            Toast.makeText(context, "No Notes found for this search", Toast.LENGTH_SHORT).show();
            populate();}
        else{
        ArrayList<Note> notes = new ArrayList<>();
        while (cursor.moveToNext()){
            Note note = new Note(cursor.getInt(0),cursor.
                    getString(1),cursor.getInt(2),cursor.getInt(3));
            notes.add(note);
        }
        customAdapter = new CustomAdapter(MainActivity.this,notes,databaseHelper);
        recyclerView.setAdapter(customAdapter);
        Toast.makeText(context, "Displaying Search Results", Toast.LENGTH_SHORT).show();
        }
    }

    //Function to Display Priority Notes
    private void getPriorityNotes(){
        Cursor cursor = databaseHelper.getPriorityNotes();
        if(cursor.getCount()==0){
            Toast.makeText(context, "No Priority Notes Found", Toast.LENGTH_SHORT).show();
            populate();}
        else{
        ArrayList<Note> notes = new ArrayList<>();
        while (cursor.moveToNext()){
            Note note = new Note(cursor.getInt(0),cursor.
                    getString(1),cursor.getInt(2),cursor.getInt(3));
            notes.add(note);
        }
        customAdapter = new CustomAdapter(MainActivity.this,notes,databaseHelper);
        recyclerView.setAdapter(customAdapter);
        Toast.makeText(context, "Displaying Priority Notes", Toast.LENGTH_SHORT).show();}
    }

    //Populate elements when broadcast is received
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            populate();
        }
    };




}
