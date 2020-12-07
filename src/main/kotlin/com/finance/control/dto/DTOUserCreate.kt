package com.finance.control.dto

import com.finance.control.model.User
import java.util.*

class DTOUserCreate (
        val name: String,
        val email : String,
        val id: String,
        val needAuthenticate: Boolean = true
)
{


    companion object {
        fun transform( user: User? ): DTOUserCreate  {
            return DTOUserCreate(
                    user!!.name,
                    user.email,
                    user.id.toString()
            )
        }
    }

}