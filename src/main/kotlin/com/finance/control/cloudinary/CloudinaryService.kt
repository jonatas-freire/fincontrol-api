package com.finance.control.cloudinary

import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import org.springframework.stereotype.Service

@Service
class CloudinaryService {

    fun upload(
            image: String,
            options: Map< String, Any >? = null
    ): CloudinaryModel? {
        return try {
            val cloudinary = Cloudinary("cloudinary://743857935125836:pmJHwVHdboe9WwsezMUZRHtMUYY@jonatas-place" )
            val response = cloudinary.uploader().upload(image, options ?: ObjectUtils.emptyMap())

            CloudinaryModel.fromMap(response as Map<Any, Any>)
        } catch ( e: Exception ) {
            null
        }

    }
}