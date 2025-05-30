package App.howmuchsix.localeDataStorage.project

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Project(
    val title: String,
    val description: String,
    var id: String? = null,
) {

    fun setUUID(uuid: UUID) {
        id = uuid.toString()
    }

}