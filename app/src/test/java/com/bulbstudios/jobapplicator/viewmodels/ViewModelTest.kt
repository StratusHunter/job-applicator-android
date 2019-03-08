package com.bulbstudios.jobapplicator.viewmodels

import com.bulbstudios.jobapplicator.enums.Team
import org.junit.Assert.*
import org.junit.Test

/**
 * Created by Terence Baker on 08/03/2019.
 */

class ViewModelTest {

    private val aName = "A Name"
    private val email = "test@test.com"
    private val team = Team.android.rawValue
    private val about = "About text"
    private val url = "https://test.com"

    @Test
    fun testValidateApplication() {

        val urlSuccessViewModel = MainViewModel(urlValidator = { true })
        val urlFailViewModel = MainViewModel(urlValidator = { false })

        //Valid
        assertTrue(urlSuccessViewModel.validateApplication(aName, email, team, about, url))

        //Missing Properties
        assertFalse(urlSuccessViewModel.validateApplication("", email, team, about, url))
        assertFalse(urlSuccessViewModel.validateApplication(aName, "", team, about, url))
        assertFalse(urlSuccessViewModel.validateApplication(aName, email, "", about, url))
        assertFalse(urlSuccessViewModel.validateApplication(aName, email, team, "", url))
        assertFalse(urlFailViewModel.validateApplication(aName, email, team, about, ""))

        //Incorrect Values
        assertFalse(urlSuccessViewModel.validateApplication(aName, "Not an email", team, about, url))
        assertFalse(urlSuccessViewModel.validateApplication(aName, email, "Not a team", about, url))
        assertFalse(urlFailViewModel.validateApplication(aName, email, team, about, "Not a url"))
    }

    @Test
    fun testJobApplication() {

        val urlSuccessViewModel = MainViewModel(urlValidator = { true })
        val application = urlSuccessViewModel.createApplication(aName, email,
                "$team, ${Team.ios.rawValue}", about, "$url\n$url")

        //Property Mapping
        assertEquals(application.name, aName)
        assertEquals(application.email, email)
        assertEquals(application.about, about)
        assertEquals(application.teams.first(), team)
        assertEquals(application.urls.first(), url)

        //Array counts
        assertEquals(application.teams.count(), 2)
        assertEquals(application.urls.count(), 2)
    }
}