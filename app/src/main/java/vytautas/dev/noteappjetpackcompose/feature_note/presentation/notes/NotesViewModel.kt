package vytautas.dev.noteappjetpackcompose.feature_note.presentation.notes

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import vytautas.dev.noteappjetpackcompose.feature_note.domain.use_cases.NoteUseCases
import javax.inject.Inject
import androidx.compose.runtime.State
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import vytautas.dev.noteappjetpackcompose.feature_note.domain.model.Note
import vytautas.dev.noteappjetpackcompose.feature_note.domain.util.NoteOrder
import vytautas.dev.noteappjetpackcompose.feature_note.domain.util.OrderType


@HiltViewModel
class NotesViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases
) : ViewModel() {

    private val _state = mutableStateOf(NotesState())
    val state: State<NotesState> = _state

    private var recentlyDeletedNote: Note? = null

    private var getNotesJob: Job? = null


    init {
        getNotes(NoteOrder.Date(OrderType.Descending))
    }

    fun onEvent(event: NotesEvent) {
        when (event) {
            is NotesEvent.Order -> {
                if (state.value.noteOrder::class == event.noteOrder::class &&
                    state.value.noteOrder.orderType == event.noteOrder.orderType
                ) {
                    return
                }
                getNotes(event.noteOrder)
            }
            is NotesEvent.DeleteNote -> {
                viewModelScope.launch {
                    noteUseCases.deleteNote(event.note)
                    recentlyDeletedNote = event.note
                }
            }
            is NotesEvent.RestoreNote -> {
                viewModelScope.launch {
                    noteUseCases.addNote(recentlyDeletedNote ?: return@launch)
                    recentlyDeletedNote = null
                }
            }
            is NotesEvent.ToggleOrderSection -> {
                _state.value = state.value.copy(
                    isOrderSectionVisible = !state.value.isOrderSectionVisible
                )
            }
        }
    }

    private fun getNotes(noteOrder: NoteOrder) {
        getNotesJob?.cancel()
        getNotesJob = noteUseCases.getNotes(noteOrder)
            .onEach { newNotes ->
                _state.value = state.value.copy(
                    notes = newNotes,
                    noteOrder = noteOrder
                )
            }
            .launchIn(viewModelScope)
    }
}