package com.binarybuff.architecturecomponentdemo.Views;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.binarybuff.architecturecomponentdemo.Adapters.NotesAdapter;
import com.binarybuff.architecturecomponentdemo.Models.Note;
import com.binarybuff.architecturecomponentdemo.R;
import com.binarybuff.architecturecomponentdemo.ViewModels.NoteViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    private NoteViewModel noteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        RecyclerView noteRecyclerView = findViewById(R.id.note_recycler_view);
        noteRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final NotesAdapter notesAdapter = new NotesAdapter();
        noteRecyclerView.setAdapter(notesAdapter);
        setSupportActionBar(toolbar);
        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                notesAdapter.setNotes(notes);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                noteViewModel.delete(notesAdapter.getNoteAt(viewHolder.getAdapterPosition()));
                                Snackbar.make(viewHolder.itemView, "Note has been deleted", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }).attachToRecyclerView(noteRecyclerView);

        notesAdapter.setOnItemClickListener(new NotesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                openUpdateNoteDialog(note);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
/*                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                openAddNoteDialog();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete_all) {
            noteViewModel.deleteAllNotes();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openAddNoteDialog() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View addNoteDialogView = factory.inflate(R.layout.dialog_add_note, null);
        final AlertDialog insertDialog = new AlertDialog.Builder(this).create();
        insertDialog.setView(addNoteDialogView);
        addNoteDialogView.findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etTitle = addNoteDialogView.findViewById(R.id.edit_text_title);
                EditText etDescription = addNoteDialogView.findViewById(R.id.edit_text_description);
                EditText editTextPriority = addNoteDialogView.findViewById(R.id.edit_text_priority);
                if (etTitle.getText().toString().trim().isEmpty() || etDescription.getText().toString().trim().isEmpty() || editTextPriority.getText().toString().trim().isEmpty()) {
                    Snackbar.make(etTitle.getRootView(), "Please fill the required fields!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    noteViewModel.insert(new Note(etTitle.getText().toString(), etDescription.getText().toString(), Integer.parseInt(editTextPriority.getText().toString())));
                    Snackbar.make(etTitle.getRootView(), "Note Saved", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    insertDialog.dismiss();
                }

            }
        });

        insertDialog.show();
    }

    private void openUpdateNoteDialog(final Note note) {
        LayoutInflater factory = LayoutInflater.from(this);
        final View addNoteDialogView = factory.inflate(R.layout.dialog_add_note, null);
        final AlertDialog updateDialog = new AlertDialog.Builder(this).create();
        updateDialog.setView(addNoteDialogView);

        final EditText etTitle = addNoteDialogView.findViewById(R.id.edit_text_title);
        final EditText etDescription = addNoteDialogView.findViewById(R.id.edit_text_description);
        final EditText editTextPriority = addNoteDialogView.findViewById(R.id.edit_text_priority);
        etTitle.setText(note.getTitle());
        etDescription.setText(note.getDescription());
        editTextPriority.setText(String.valueOf(note.getPriority()));

        addNoteDialogView.findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etTitle.getText().toString().trim().isEmpty() || etDescription.getText().toString().trim().isEmpty() || editTextPriority.getText().toString().trim().isEmpty()) {
                    Snackbar.make(etTitle.getRootView(), "Please fill the required fields!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                   Note updatedNote= new Note(etTitle.getText().toString(), etDescription.getText().toString(), Integer.parseInt(editTextPriority.getText().toString()));
                    updatedNote.setId(note.getId());
                   noteViewModel.update(updatedNote);
                    Snackbar.make(etTitle.getRootView(), "Note Saved", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    updateDialog.dismiss();
                }

            }
        });

        updateDialog.show();
    }
}
