package com.goit.restapiexample.notes;

import com.goit.restapiexample.notes.dto.create.CreateNoteRequest;
import com.goit.restapiexample.notes.dto.create.CreateNoteResponse;
import com.goit.restapiexample.users.User;
import com.goit.restapiexample.users.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteService noteService;


    @BeforeEach
    void setUp() {
        noteService = spy(noteService);
    }

    @Test
    void testCreateNoteSuccess() {
        String username = "testUser";
        CreateNoteRequest request = new CreateNoteRequest("title", "content");
        User mockUser = new User();
        Note mockNote = Note.builder().id(1L).user(mockUser).title("title").content("content").build();

        doReturn(Optional.empty()).when(noteService).validateCreateFields(request);
        when(userService.findByUsername(username)).thenReturn(mockUser);
        when(noteRepository.save(any(Note.class))).thenReturn(mockNote);

        CreateNoteResponse response = noteService.create(username, request);

        assertEquals(CreateNoteResponse.Error.OK, response.getError());
        assertEquals(1L, response.getCreatedNoteId());
    }
}