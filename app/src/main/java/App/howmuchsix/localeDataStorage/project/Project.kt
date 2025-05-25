package App.howmuchsix.localeDataStorage.project

import kotlinx.serialization.Serializable

@Serializable
data class Project(
    val title: String,
    val description: String,
    val program : Program,
    val id: Int  = 0,
)
