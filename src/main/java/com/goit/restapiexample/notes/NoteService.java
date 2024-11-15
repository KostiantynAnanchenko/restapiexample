package com.goit.restapiexample.notes;

import com.goit.restapiexample.notes.dto.create.CreateNoteRequest;
import com.goit.restapiexample.notes.dto.create.CreateNoteResponse;
import com.goit.restapiexample.notes.dto.delete.DeleteNoteResponse;
import com.goit.restapiexample.notes.dto.get.GetUserNotesResponse;
import com.goit.restapiexample.notes.dto.update.UpdateNoteRequest;
import com.goit.restapiexample.notes.dto.update.UpdateNoteResponse;
import com.goit.restapiexample.users.User;
import com.goit.restapiexample.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NoteService {
    private static final int MAX_TITLE_LENGTH = 100;
    private static final int MAX_CONTENT_LENGTH = 1000;

    private final UserService userService;
    private final NoteRepository repository;

    public CreateNoteResponse create(String username, CreateNoteRequest request) {
        Optional<CreateNoteResponse.Error> validationError = validateCreateFields(request);

        if (validationError.isPresent()) {
            return CreateNoteResponse.failed(validationError.get());
        }

        User user = userService.findByUsername(username);

        Note createdNote = repository.save(Note.builder()
                .user(user)
                .title(request.getTitle())
                .content(request.getContent())
                .build());

        return CreateNoteResponse.success(createdNote.getId());
    }

    public GetUserNotesResponse getUserNotes(String username) {
        List<Note> userNotes = repository.getUserNotes(username);

        return GetUserNotesResponse.success(userNotes);
    }

    public UpdateNoteResponse update(String username, UpdateNoteRequest request) {
        Optional<Note> optionalNote = repository.findById(request.getId());

        if (optionalNote.isEmpty()) {
            return UpdateNoteResponse.failed(UpdateNoteResponse.Error.invalidNoteId);
        }

        Note note = optionalNote.get();

        boolean isNotUserNote = isNotUserNote(username, note);

        if (isNotUserNote) {
            return UpdateNoteResponse.failed(UpdateNoteResponse.Error.insufficientPrivileges);
        }

        Optional<UpdateNoteResponse.Error> validationError = validateUpdateFields(request);

        if (validationError.isPresent()) {
            return UpdateNoteResponse.failed(validationError.get());
        }

        note.setTitle(request.getTitle());
        note.setContent(request.getContent());

        repository.save(note);

        return UpdateNoteResponse.success(note);
    }

    public DeleteNoteResponse delete(String username, long id) {
        Optional<Note> optionalNote = repository.findById(id);

        if (optionalNote.isEmpty()) {
            return DeleteNoteResponse.failed(DeleteNoteResponse.Error.invalidNoteId);
        }

        Note note = optionalNote.get();

        boolean isNotUserNote = isNotUserNote(username, note);

        if (isNotUserNote) {
            return DeleteNoteResponse.failed(DeleteNoteResponse.Error.insufficientPrivileges);
        }

        repository.delete(note);

        return DeleteNoteResponse.success();
    }

    Optional<CreateNoteResponse.Error> validateCreateFields(CreateNoteRequest request) {
        if (Objects.isNull(request.getTitle()) || request.getTitle().length() > MAX_TITLE_LENGTH) {
            return Optional.of(CreateNoteResponse.Error.INVALID_TITLE);
        }

        if (Objects.isNull(request.getContent()) || request.getContent().length() > MAX_CONTENT_LENGTH) {
            return Optional.of(CreateNoteResponse.Error.INVALID_TITLE);
        }

        return Optional.empty();
    }

    private Optional<UpdateNoteResponse.Error> validateUpdateFields(UpdateNoteRequest request) {
        if (Objects.nonNull(request.getTitle()) && request.getTitle().length() > MAX_TITLE_LENGTH) {
            return Optional.of(UpdateNoteResponse.Error.invalidTitleLength);
        }

        if (Objects.nonNull(request.getContent()) && request.getContent().length() > MAX_CONTENT_LENGTH) {
            return Optional.of(UpdateNoteResponse.Error.invalidTitleLength);
        }

        return Optional.empty();
    }

    private boolean isNotUserNote(String username, Note note) {
        return !note.getUser().getUserId().equals(username);
    }
}