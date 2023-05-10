package com.pascal.personalorganizer.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.pascal.personalorganizer.domain.models.Reminder
import com.pascal.personalorganizer.domain.repository.ReminderRepository
import com.pascal.personalorganizer.presentation.ui.reminders.RemindersViewModel
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


@OptIn(ExperimentalCoroutinesApi::class)
class RemindersViewModelTest {

    @RelaxedMockK
    private lateinit var repository: ReminderRepository

    private lateinit var remindersViewModel: RemindersViewModel


    @get:Rule
    var rule: InstantTaskExecutorRule = InstantTaskExecutorRule()


    @Before
    fun onBefore(){
        MockKAnnotations.init(this)
        remindersViewModel = RemindersViewModel(repository)
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @After
    fun onAfter(){
        Dispatchers.resetMain()
    }


    @Test
    fun checkWeAreGettingOurRemindersAndUpdatingIsReadyHolder() = runTest {
        //Given
        val reminders = listOf(Reminder(id = 1, title = "title",date = 0L, repeatDaily = false))
        coEvery { repository.getAllReminders() } returns reminders
        //When
        val response = repository.getAllReminders()
        remindersViewModel.getReminders()
        //Then
        assert(response == reminders)
        assert(remindersViewModel.isReady)
    }


}