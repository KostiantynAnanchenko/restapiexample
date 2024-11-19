package com.goit.restapiexample.notes.dto.update;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateNoteRequest {
    private long id;
    private String title;
    private String content;
}