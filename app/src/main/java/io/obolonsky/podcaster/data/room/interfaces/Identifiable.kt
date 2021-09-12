package io.obolonsky.podcaster.data.room.interfaces

import java.io.Serializable

interface Identifiable: Serializable {

    val id: Long

    val title: String

}