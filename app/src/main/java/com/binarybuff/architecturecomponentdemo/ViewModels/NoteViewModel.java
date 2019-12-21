package com.binarybuff.architecturecomponentdemo.ViewModels;

import android.app.Application;

import com.binarybuff.architecturecomponentdemo.Models.Note;
import com.binarybuff.architecturecomponentdemo.Repositories.NoteRepository;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class NoteViewModel extends AndroidViewModel {

    private LiveData<List<Note>> allNotes;
    private NoteRepository repository;

    public NoteViewModel(@NonNull Application application) {
        super(application);
        repository = new NoteRepository(application);
        allNotes = repository.getAllNotes();
    }

    public void insert(Note note) {
        repository.insert(note);
    }

    public void update(Note note) {
        repository.update(note);
    }

    public void delete(Note note) {
        repository.delete(note);
    }

    public LiveData<List<Note>> getAllNotes(){
        return repository.getAllNotes();
    }

    public void deleteAllNotes() {
        repository.deleteAllNotes();
    }

}
