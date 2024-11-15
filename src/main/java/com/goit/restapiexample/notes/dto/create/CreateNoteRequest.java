package com.goit.restapiexample.notes.dto.create;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateNoteRequest {
    private String title;
    private String content;
}