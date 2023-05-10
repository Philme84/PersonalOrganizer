package com.pascal.personalorganizer.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.pascal.personalorganizer.domain.models.Schedule
import com.pascal.personalorganizer.domain.repository.ScheduleRepository
import com.pascal.personalorganizer.presentation.ui.schedule.ScheduleViewModel
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
class ScheduleViewModelTest {

    @RelaxedMockK
    private lateinit var repository: ScheduleRepository

    private lateinit var scheduleViewModel: ScheduleViewModel


    @get:Rule
    var rule: InstantTaskExecutorRule = InstantTaskExecutorRule()


    @Before
    fun onBefore(){
        MockKAnnotations.init(this)
        scheduleViewModel = ScheduleViewModel(repository)
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @After
    fun onAfter(){
        Dispatchers.resetMain()
    }


    @Test
    fun checkWeAreGettingOurScheduleAndUpdatingIsReadyHolder() = runTest {
        //Given
        val schedule = listOf(Schedule(id = 1, title = "title", selectedDay = "selectedDay", date = 0L, reminder = false))
        coEvery { repository.getAllSchedules("1") } returns schedule
        //When
        val response = repository.getAllSchedules("1")
        scheduleViewModel.addScheduleData("1")
        //Then
        assert(response == schedule)
        assert(scheduleViewModel.uiState.isReady)
    }


}