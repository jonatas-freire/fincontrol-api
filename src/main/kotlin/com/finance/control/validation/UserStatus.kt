package com.finance.control.validation

enum class UserStatus {
    CREATED,
    EMAIL_ALREADY_REGISTERED,
    EMAIL_NOT_AUTHENTICATED,
    ERROR_SEND_EMAIL,
    ERROR_CREATE_USER,
    EMAIL_NOT_FOUND,
    SEND_EMAIL,
    CODE_NOT_FOUND,
    ERROR_UPDATE_USER,
    PASSWORD_RESETED,
    PASSWORDS_NOT_EQUAL
}