package com.bulbstudios.jobapplicator.viewmodels

import com.bulbstudios.jobapplicator.classes.APIResult
import com.bulbstudios.jobapplicator.classes.JobApplication
import com.bulbstudios.jobapplicator.enums.Team
import com.bulbstudios.jobapplicator.interfaces.JobApplicationService
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

/**
 * Created by Terence Baker on 08/03/2019.
 */

class ViewModelTest {

    private val urlSuccessViewModel = MainViewModel(urlValidator = { true })
    private val urlFailViewModel = MainViewModel(urlValidator = { false })

    private val name = "A Name"
    private val email = "test@test.com"
    private val team = Team.android.rawValue
    private val about = "About text"
    private val url = "https://test.com"

    @Before
    fun setup() {

        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
    }

    @After
    fun tearDown() {

        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
    }

    @Test
    fun test_validateApplication_withValidData_assertTrue() {

        val validData = urlSuccessViewModel.validateApplication(name, email, team, about, url)

        assertTrue("Valid data no longer considered valid", validData)
    }

    @Test
    fun test_validateApplication_withMissingData_assertFalse() {

        val nameMissing = urlSuccessViewModel.validateApplication("", email, team, about, url)
        val emailMissing = urlSuccessViewModel.validateApplication(name, "", team, about, url)
        val teamMissing = urlSuccessViewModel.validateApplication(name, email, "", about, url)
        val aboutMissing = urlSuccessViewModel.validateApplication(name, email, team, "", url)
        val urlMissing = urlFailViewModel.validateApplication(name, email, team, about, "")

        assertFalse("Name should not be empty", nameMissing)
        assertFalse("Email should not be empty", emailMissing)
        assertFalse("Team should not be empty", teamMissing)
        assertFalse("About should not be empty", aboutMissing)
        assertFalse("URLs should not be empty", urlMissing)
    }

    @Test
    fun test_validationApplication_withIncorrectData_assertFalse() {

        val emailInvalid = urlSuccessViewModel.validateApplication(name, "Not an email", team, about, url)
        val teamInvalid = urlSuccessViewModel.validateApplication(name, email, "Not a team", about, url)
        val urlInvalid = urlFailViewModel.validateApplication(name, email, team, about, "Not a url")

        assertFalse("Email validation failing", emailInvalid)
        assertFalse("Team validation failing", teamInvalid)
        assertFalse("URL validation failing", urlInvalid)
    }

    @Test
    fun testJobApplication() {

        val application =
            urlSuccessViewModel.createApplication(name, email, "$team, ${Team.ios.rawValue}", about, "$url\n$url")

        //Property Mapping
        assertEquals(application.name, name)
        assertEquals(application.email, email)
        assertEquals(application.about, about)
        assertEquals(application.teams.first(), team)
        assertEquals(application.urls.first(), url)

        //Array counts
        assertEquals(application.teams.count(), 2)
        assertEquals(application.urls.count(), 2)
    }

    @Test
    fun testPerformRequest() {

        val application = JobApplication(name, email, about, listOf(url), listOf(team))

        val mockService = mock(JobApplicationService::class.java)
        `when`(mockService.apply(application)).thenReturn(Single.just(application))

        MainViewModel(mockService) { true }
            .performApplyRequest(application)
            .test()
            .assertResult(APIResult(application))
    }
}