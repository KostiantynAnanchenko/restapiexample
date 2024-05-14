package com.goit.restapiexample.notes.dto.get;

import com.goit.restapiexample.notes.Note;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class GetUserNotesResponse {
    private Error error;

    private List<Note> userNotes;

    public enum Error {
        ok
    }

    public static GetUserNotesResponse success(List<Note> userNotes) {
        return builder().error(Error.ok).userNotes(userNotes).build();
    }

    public static GetUserNotesResponse failed(Error error) {
        return builder().error(error).userNotes(null).build();
    }
}