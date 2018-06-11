package s.maciej.scrandroid.data

data class ModelData(
        val id: Int = 0,
        val a: Int? = null,
        val b: Int? = null,
        var content: Int? = null,
        var uuid: String = "",
        var specialUuid: String = "",
        var time: String = "",
        var blocked: Boolean = false
)

