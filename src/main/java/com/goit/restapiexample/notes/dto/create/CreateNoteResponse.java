package com.goit.restapiexample.notes.dto.create;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreateNoteResponse {
    private Error error;

    private long createdNoteId;

    public enum Error {
        OK,
        INVALID_TITLE,
        invalidContent
    }

    public static CreateNoteResponse success(long createdNoteId) {
        return builder().error(Error.OK).createdNoteId(createdNoteId).build();
    }

    public static CreateNoteResponse failed(Error error) {
        return builder().error(error).createdNoteId(-1L).build();
    }
}