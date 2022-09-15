package io.obolonsky.storage.database.entities

import io.obolonsky.storage.database.utils.Guid

interface Identifiable {

    val id: Guid
}