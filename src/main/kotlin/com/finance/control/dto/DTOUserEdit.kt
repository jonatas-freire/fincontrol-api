package com.finance.control.dto

import com.finance.control.model.User

class DTOUserEdit (
        val name: String,
        val photo: String
) {
    companion object {
        fun fromUser(user: User?): DTOUserEdit {
            return DTOUserEdit( name = user!!.name, photo = user.photo)
        }
    }
}