package com.bulbstudios.jobapplicator.enums

/**
 * Created by Terence Baker on 04/03/2019.
 */
enum class Team(val rawValue: String) {

    android("android"),
    ios("ios"),
    backend("backend"),
    frontend("frontend"),
    design("design");

    companion object {

        fun with(rawValue: String) = Team.values().firstOrNull { rawValue == it.rawValue }
    }
}