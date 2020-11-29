package com.finance.control.cloudinary

class CloudinaryModel (
    val url: String?,
    val format: String?,
    val overwritten: Boolean?,
) {
    companion object  {
        fun fromMap( map: Map< Any, Any> ): CloudinaryModel {
            return CloudinaryModel(
                    (map["url"] ?: null) as String?,
                    (map["format"] ?: null) as String?,
                    (map["overwritten"] ?: null) as Boolean?,
            )
        }
    }
}