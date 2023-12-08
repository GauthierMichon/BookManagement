package com.jicay.bookmanagement

import io.cucumber.java.ParameterType

class CustomParameterTypes {

    @ParameterType("true|false")
    fun boolean(param: String): Boolean {
        return param.toBoolean()
    }
}