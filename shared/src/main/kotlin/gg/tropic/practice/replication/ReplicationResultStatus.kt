package gg.tropic.practice.replication

enum class ReplicationResultStatus
{
    Completed,
    Unavailable
}

data class ReplicationResult(
    val status: ReplicationResultStatus,
    val message: String? = null
)
