package com.bulbstudios.jobapplicator.interfaces

import com.bulbstudios.jobapplicator.BuildConfig
import com.bulbstudios.jobapplicator.classes.JobApplication
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Created by Terence Baker on 04/03/2019.
 */

interface JobApplicationService {

    companion object {

        private val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.baseURL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        fun create(): JobApplicationService = retrofit.create(JobApplicationService::class.java)
    }

    @POST("apply")
    fun apply(@Body application: JobApplication): Single<JobApplication>

}