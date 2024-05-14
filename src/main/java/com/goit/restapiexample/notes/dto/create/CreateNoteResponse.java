package com.goit.restapiexample.notes.dto.create;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreateNoteResponse {
    private Error error;

    private long createdNoteId;

    public enum Error {
        ok,
        invalidTitle,
        invalidContent
    }

    public static CreateNoteResponse success(long createdNoteId) {
        return builder().error(Error.ok).createdNoteId(createdNoteId).build();
    }

    public static CreateNoteResponse failed(Error error) {
        return builder().error(error).createdNoteId(-1L).build();
    }
}