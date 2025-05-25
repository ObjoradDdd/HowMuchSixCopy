package App.howmuchsix.localeDataStorage.project

import kotlinx.serialization.Serializable


@Serializable
data class Program(
    val listOfBlocks : List<BlockDB>
)
