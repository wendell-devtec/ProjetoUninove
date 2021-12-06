package com.tw4rs.notesuni9.model;

public class NoteModel {


    String message;
    String id;

    public NoteModel() {
    }

    public NoteModel(String message, String id) {
        this.message = message;
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
