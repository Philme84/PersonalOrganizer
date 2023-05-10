package com.pascal.personalorganizer.ui.viewmodel

import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.pascal.personalorganizer.domain.models.Notes
import com.pascal.personalorganizer.domain.repository.NotesRepository
import com.pascal.personalorganizer.presentation.ui.notes.NotesViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class NotesViewModelTest {

    @RelaxedMockK
    private lateinit var repository: NotesRepository

    private lateinit var notesViewModel: NotesViewModel

    @get:Rule
    var rule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun onBefore(){
        MockKAnnotations.init(this)
        notesViewModel = NotesViewModel(repository)
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @After
    fun onAfter(){
        Dispatchers.resetMain()
    }

    @Test
    fun checkWeAreGettingOurNotesAndUpdatingIsReadyHolder() = runTest {
        //Given
        val notes = listOf(Notes(id = 1, title = "title", description = "description", date = 0L, uri = Uri.EMPTY))
        coEvery { repository.getAllNotes() } returns notes
        //When
        val response = repository.getAllNotes()
        notesViewModel.getNotes()
        //Then
        assert(response == notes)
        assert(notesViewModel.isReady)
    }


}