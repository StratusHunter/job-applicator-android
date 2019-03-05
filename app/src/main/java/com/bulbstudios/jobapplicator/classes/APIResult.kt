package com.bulbstudios.jobapplicator.classes

import com.bulbstudios.jobapplicator.enums.APIResultType

/**
 * Created by Terence Baker on 04/03/2019.
 */
data class APIResult<T>(val result: T? = null, val throwable: Throwable? = null) {

    val type = if (result != null) APIResultType.success else APIResultType.error
}