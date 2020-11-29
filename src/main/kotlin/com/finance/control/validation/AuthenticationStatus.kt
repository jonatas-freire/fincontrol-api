package com.finance.control.validation

enum class AuthenticationStatus {
    AUTHENTICATED,
    AUTHENTICATE_CODE_SEND,
    AUTHENTICATE_CODE_INVALID,
    AUTHENTICATION_SUCCESS,
    ERROR_SEND_EMAIL,
    ERROR,
}