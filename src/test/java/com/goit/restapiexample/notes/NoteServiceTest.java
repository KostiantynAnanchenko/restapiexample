package com.goit.restapiexample.notes;

import com.goit.restapiexample.notes.dto.create.CreateNoteRequest;
import com.goit.restapiexample.notes.dto.create.CreateNoteResponse;
import com.goit.restapiexample.notes.dto.get.GetUserNotesResponse;
import com.goit.restapiexample.notes.dto.update.UpdateNoteRequest;
import com.goit.restapiexample.notes.dto.update.UpdateNoteResponse;
import com.goit.restapiexample.users.User;
import com.goit.restapiexample.users.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.List;
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
    void testCreateNote_withValidationError() {
        // Arrange
        String username = "testUser";
        CreateNoteRequest request = new CreateNoteRequest(null, "content");

        // Мокаємо метод валідації, щоб він повертав помилку
        when(noteService.validateCreateFields(request)).thenReturn(Optional.of(CreateNoteResponse.Error.INVALID_TITLE));

        // Act
        CreateNoteResponse response = noteService.create(username, request);

        // Assert
        assertEquals(CreateNoteResponse.Error.INVALID_TITLE, response.getError());
        assertEquals(-1L, response.getCreatedNoteId());
        verify(noteRepository, never()).save(any());
        verify(userService, never()).findByUsername(any());
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


    @Test
    void testGetUserNotes() {
        String username = "testUser";
        User mockUser = new User();

        Note note1 = new Note();
        Note note2 = new Note();
        List<Note> mockUserNotes = List.of(note1, note2);


        when(noteRepository.getUserNotes(username)).thenReturn(mockUserNotes);

        GetUserNotesResponse response = noteService.getUserNotes(username);
        ;

        assertEquals(GetUserNotesResponse.Error.ok, response.getError());
        assertEquals(List.of(note1, note2), response.getUserNotes());


    }

    @Test
    void testUpdateNoteResponse() {
        //Arrange
        String username = "testUser";
        User mockUser = new User();
        UpdateNoteRequest request = new UpdateNoteRequest(1L, "title", "content");

        Note mockNote = new Note();
        mockNote.setId(1L);
        mockNote.setTitle("oldTitle");
        mockNote.setContent("oldContent");


        when(noteRepository.findById(request.getId())).thenReturn(Optional.of(mockNote));
        doReturn(false).when(noteService).isNotUserNote(username, mockNote);
        doReturn(Optional.empty()).when(noteService).validateUpdateFields(request);

        //Act
        UpdateNoteResponse response = noteService.update(username, request);
        //Assert
        assertEquals("title", mockNote.getTitle());
        assertEquals("content", mockNote.getContent());
        verify(noteRepository).save(mockNote);

    }


}