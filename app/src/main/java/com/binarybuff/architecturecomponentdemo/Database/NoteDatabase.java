package com.binarybuff.architecturecomponentdemo.Database;

import android.content.Context;
import android.os.AsyncTask;

import com.binarybuff.architecturecomponentdemo.Daos.NoteDao;
import com.binarybuff.architecturecomponentdemo.Models.Note;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Note.class}, version = 1)
public abstract class NoteDatabase extends RoomDatabase {

    private static NoteDatabase instance;

    public abstract NoteDao noteDao();

    public static synchronized NoteDatabase getInstance(Context context) {
        if (null == instance) {
            instance = Room.databaseBuilder(context.getApplicationContext(), NoteDatabase.class, "note_database")
                    .fallbackToDestructiveMigration().addCallback(roomCallback).build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback= new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new AddDefaultNoteAsyncTask(instance).execute();
        }
    };

    private static class AddDefaultNoteAsyncTask extends AsyncTask<Void, Void, Void> {
        private NoteDao noteDao;
        public AddDefaultNoteAsyncTask(NoteDatabase database) {
            this.noteDao = database.noteDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            noteDao.insert(new Note("Note 1", "This is 1st note ", 2));
            noteDao.insert(new Note("Note 2", "This is 1st note ", 3));
            noteDao.insert(new Note("Note 3", "This is 3rd note having the higest priority", 1));
            return null;
        }
    }
}
