package com.bulbstudios.jobapplicator

import android.util.Patterns
import android.webkit.URLUtil
import androidx.lifecycle.ViewModel
import com.bulbstudios.jobapplicator.classes.APIResult
import com.bulbstudios.jobapplicator.classes.JobApplication
import com.bulbstudios.jobapplicator.enums.Team
import com.bulbstudios.jobapplicator.interfaces.JobApplicationService
import io.reactivex.Single

/**
 * Created by Terence Baker on 04/03/2019.
 */
class MainViewModel : ViewModel() {

    private val service = JobApplicationService.create()

    private fun createTeamList(team: String): List<Team?> {

        return team.split(",")
            .filter { it.isEmpty() }
            .map { it.replace(" ", "") }
            .map { Team.with(it) }
    }

    private fun createURLList(url: String): List<String?> {

        return url.split("\n").map { if (URLUtil.isValidUrl(it)) it else null }
    }

    fun validateApplication(name: String, email: String, teams: String, about: String, urls: String): Boolean {

        val emailValid = email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()

        val teamList = createTeamList(teams)
        val teamsValid = teamList.isNotEmpty() && !teamList.contains(null)

        val urlList = createURLList(urls)
        val urlsValid = urlList.isNotEmpty() && !urlList.contains(null)

        return name.isNotEmpty() && emailValid && teamsValid && about.isNotEmpty() && urlsValid
    }

    fun createApplication(name: String, email: String, teams: String, about: String, urls: String): JobApplication {

        val teamList = createTeamList(teams).filterNotNull().map { it.rawValue }
        val urlList = createURLList(urls).filterNotNull()

        return JobApplication(name, email, about, urlList, teamList)
    }

    fun performApplyRequest(application: JobApplication): Single<APIResult<JobApplication>> {

        return service.apply(application)
            .map { APIResult(it) }
            .onErrorReturn { APIResult(null, it) }
    }
}