package vytautas.dev.noteappjetpackcompose.feature_note.domain.use_cases

import vytautas.dev.noteappjetpackcompose.feature_note.domain.model.Note
import vytautas.dev.noteappjetpackcompose.feature_note.domain.repository.NoteRepository

class GetNote(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(id: Int): Note? {
        return repository.getNoteById(id)
    }
}