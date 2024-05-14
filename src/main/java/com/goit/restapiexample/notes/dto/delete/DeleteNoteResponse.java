package com.goit.restapiexample.notes.dto.delete;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DeleteNoteResponse {
    private Error error;

    public enum Error {
        ok,
        insufficientPrivileges,
        invalidNoteId
    }

    public static DeleteNoteResponse success() {
        return builder().error(Error.ok).build();
    }

    public static DeleteNoteResponse failed(Error error) {
        return builder().error(error).build();
    }
}