package com.finance.control.dto

import com.finance.control.model.User
import java.util.*

class DTOUserCreate{

    lateinit var name: String
    lateinit var email : String
    lateinit var id: UUID
    val needAuthenticate: Boolean = true

    fun transform( user: User? ): DTOUserCreate  {
        this.name = user!!.name
        this.email = user.email
        this.id = user.id
        return this
    }
}