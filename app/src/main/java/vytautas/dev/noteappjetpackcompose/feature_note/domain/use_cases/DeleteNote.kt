package vytautas.dev.noteappjetpackcompose.feature_note.domain.use_cases

import vytautas.dev.noteappjetpackcompose.feature_note.domain.model.Note
import vytautas.dev.noteappjetpackcompose.feature_note.domain.repository.NoteRepository

class DeleteNote(
    private val repository: NoteRepository
) {

    suspend operator fun invoke(note: Note) {
        repository.deleteNote(note)
    }
}