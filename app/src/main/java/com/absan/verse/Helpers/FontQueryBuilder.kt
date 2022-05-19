package com.absan.verse.Helpers

internal class QueryBuilder(
    private val familyName: String,
    val width: Float? = null,
    private val weight: Int? = null,
    private val italic: Float? = null,
    private val besteffort: Boolean? = null
) {

    fun build(): String {
        if (weight == null && width == null && italic == null && besteffort == null) {
            return familyName
        }
        val builder = StringBuilder()
        builder.append("name=").append(familyName)
        weight?.let { builder.append("&weight=").append(weight) }
        width?.let { builder.append("&width=").append(width) }
        italic?.let { builder.append("&italic=").append(italic) }
        besteffort?.let { builder.append("&besteffort=").append(besteffort) }
        return builder.toString()
    }
}
