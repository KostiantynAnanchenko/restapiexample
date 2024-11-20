package com.goit.restapiexample.notes;

import com.goit.restapiexample.users.User;
import com.goit.restapiexample.users.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class NoteRepositoryTest {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    @Test
    void testGetUserNotes() {
        // Arrange
        User user = User.builder().userId("testUser").passwordHash("12345").name("title").age(33).build();
        userRepository.save(user);

        Note note1 = new Note();
        note1.setUser(user);
        note1.setTitle("Title 1");
        note1.setContent("Content 1");
        noteRepository.save(note1);

        Note note2 = new Note();
        note2.setUser(user);
        note2.setTitle("Title 2");
        note2.setContent("Content 2");
        noteRepository.save(note2);

        // Act
        List<Note> userNotes = noteRepository.getUserNotes("testUser");

        // Assert
        assertEquals(2, userNotes.size());
    }

    @Transactional
    @Test
    void testFindById() {
        // Arrange
        User user = User.builder().userId("testUser").passwordHash("12345").name("title").age(33).build();
        userRepository.save(user);

        Note note = new Note();
        note.setUser(user);
        note.setTitle("Title");
        note.setContent("Content");
        Note savedNote = noteRepository.save(note);

        // Act
        Optional<Note> foundNote = noteRepository.findById(savedNote.getId());

        // Assert
        assertTrue(foundNote.isPresent());
        assertEquals("Title", foundNote.get().getTitle());
    }

    @Transactional
    @Test
    void testSave() {

        //Arrange
        User user = User.builder().userId("testUser").passwordHash("12345").name("title").age(33).build();
        userRepository.save(user);

        Note note = new Note();
        note.setUser(user);
        note.setTitle("Title");
        note.setContent("Content");


        //Act
        Note savedNote = noteRepository.save(note);
        long id = savedNote.getId();

        // Assert
        assertTrue(noteRepository.findById(id).isPresent());
        assertEquals(savedNote, noteRepository.findById(id).get());

    }

    @Transactional
    @Test
    void testDelete() {
        //Arrange
        User user = User.builder().userId("testUser").passwordHash("12345").name("title").age(33).build();
        userRepository.save(user);

        Note note = new Note();
        note.setUser(user);
        note.setTitle("Title");
        note.setContent("Content");
        Note savedNote = noteRepository.save(note);
        long id = savedNote.getId();

        // Act
        noteRepository.delete(savedNote);

        // Assert
        assertFalse(noteRepository.findById(id).isPresent());
    }
}