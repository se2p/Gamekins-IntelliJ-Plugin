package org.plugin.plugin.data

import kotlinx.serialization.Serializable

@Serializable
data class StoreChallenge(val job: String, val challengeName: String)