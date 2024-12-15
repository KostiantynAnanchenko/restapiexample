package com.goit.restapiexample.notes;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.goit.restapiexample.notes.dto.create.CreateNoteRequest;
import com.goit.restapiexample.notes.dto.create.CreateNoteResponse;
import com.goit.restapiexample.notes.dto.delete.DeleteNoteResponse;
import com.goit.restapiexample.notes.dto.get.GetUserNotesResponse;
import com.goit.restapiexample.notes.dto.update.UpdateNoteRequest;
import com.goit.restapiexample.notes.dto.update.UpdateNoteResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NoteController.class)
class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NoteService noteService;

    @Test
    void testCreateNote() throws Exception {
        CreateNoteRequest request = new CreateNoteRequest("Test Title", "Test Content");

        CreateNoteResponse response = new CreateNoteResponse(CreateNoteResponse.Error.OK, 1L);

        when(noteService.create(anyString(), any(CreateNoteRequest.class))).thenReturn(response);

        mockMvc.perform(post("/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request))
                        .with(csrf())
                        .with(user("testUser")))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(response)));
    }

    @Test
    void testGetUserNotesResponse() throws Exception {
        // Arrange
        Note note1 = Note.builder().id(1L).title("Test 1").content("Content 1").build();
        Note note2 = Note.builder().id(2L).title("Test 2").content("Content 2").build();
        List<Note> userNotes = List.of(note1, note2);

        GetUserNotesResponse response = GetUserNotesResponse.builder()
                .error(GetUserNotesResponse.Error.ok)
                .userNotes(userNotes)
                .build();

        when(noteService.getUserNotes(anyString())).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/notes/getUserNotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("testUser"))) // Додаємо аутентифікацію
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value("ok"))
                .andExpect(jsonPath("$.userNotes.length()").value(2))
                .andExpect(jsonPath("$.userNotes[0].title").value("Test 1"));
    }

    @Test
    void testUpdate() throws Exception {
        Note updatedNote = Note.builder()
                .id(1L)
                .title("Test 1")
                .content("Content 1")
                .created_at(LocalDateTime.now())
                .build();
        UpdateNoteResponse response = UpdateNoteResponse.builder().error(UpdateNoteResponse.Error.ok).updatedNote(updatedNote).build();
        UpdateNoteRequest request = new UpdateNoteRequest(2L,"Test2","Content2");

        when(noteService.update((anyString()), any(UpdateNoteRequest.class))).thenReturn(response);

                mockMvc.perform(patch("/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request))
                        .with(csrf())
                        .with(user("testUser")))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(response)));

    }

    @Test
    void testDelete() throws Exception {
        DeleteNoteResponse response= DeleteNoteResponse
                .builder()
                .error(DeleteNoteResponse.Error.ok)
                .build();

        when(noteService.delete(eq("testUser"), eq(1L))).thenReturn(response);

        mockMvc.perform(delete("/notes")
                        .param("id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .with(user("testUser")))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(response)));
    }

}
