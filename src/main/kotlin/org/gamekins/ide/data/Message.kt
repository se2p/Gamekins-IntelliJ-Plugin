package org.gamekins.ide.data

data class Message(
    val message: MessageDetails
)

data class MessageDetails(
    val localizedMessage: String,
    val cause: String?,
    val suppressed: List<Any>,
    val message: String,
    val kind: String
)