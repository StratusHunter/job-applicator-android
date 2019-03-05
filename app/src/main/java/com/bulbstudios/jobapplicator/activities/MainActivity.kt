package com.bulbstudios.jobapplicator.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.bulbstudios.jobapplicator.R
import com.bulbstudios.jobapplicator.classes.APIResult
import com.bulbstudios.jobapplicator.classes.JobApplication
import com.bulbstudios.jobapplicator.enums.APIResultType
import com.bulbstudios.jobapplicator.viewmodels.MainViewModel
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.view.enabled
import com.jakewharton.rxbinding2.widget.textChanges
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.withLatestFrom
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import java.util.concurrent.TimeUnit

class MainActivity : RxAppCompatActivity() {

    private data class InputData(
            val name: String,
            val email: String,
            val team: String,
            val about: String,
            val url: String
    )

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    override fun onResume() {

        super.onResume()

        val inputDataObservable = Observables.combineLatest(nameText.textChanges(), emailText.textChanges(),
                teamText.textChanges(), aboutText.textChanges(), urlText.textChanges()) { name, email, team, about, url ->

            return@combineLatest InputData(name.toString(), email.toString(), team.toString(), about.toString(), url.toString())
        }
                .share()
                .replay()
                .autoConnect()

        setupValidationObserver(inputDataObservable)
        setupButtonObserver(inputDataObservable)
    }

    @SuppressLint("CheckResult")
    private fun setupValidationObserver(inputDataObservable: Observable<InputData>) {

        inputDataObservable
                .debounce(300, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .map { viewModel.validateApplication(it.name, it.email, it.team, it.about, it.url) }
                .bindToLifecycle(this)
                .subscribe(submitButton.enabled())
    }

    private fun setupButtonObserver(inputDataObservable: Observable<InputData>) {

        submitButton.clicks()
                .debounce(300, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .withLatestFrom(inputDataObservable) { _, input -> input }
                .switchMapSingle {

                    val application = viewModel.createApplication(it.name, it.email, it.team, it.about, it.url)
                    return@switchMapSingle viewModel.performApplyRequest(application)
                }
                .bindToLifecycle(this)
                .subscribeBy(onNext = { handleApplicationResponse(it) })
    }

    private fun handleApplicationResponse(result: APIResult<JobApplication>) {

        val response = when (result.type) {

            APIResultType.success -> {

                Timber.i("${getString(R.string.application_received)} ${result.result}")
                R.string.application_received
            }
            APIResultType.error -> {

                Timber.w("${getString(R.string.application_error)} ${result.throwable}")
                R.string.application_error
            }
        }

        Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
    }
}
