package s.maciej.scrandroid.data

data class ModelData(
        val id: String = "",
        val content: Int? = null,
        var uuid: String = "",
        var specialUuid: String = "",
        var blocked: Boolean = false
)

