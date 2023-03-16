package com.vauthenticator.server.web

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ModelAttribute

@ControllerAdvice
class BaseUiModelInjector(@Value("\${assetServer.baseUrl:http://localhost:3000/asset}") private val assetServerBaseUrl: String) {

    @ModelAttribute("assetServerBaseUrl")
    fun assetServerBaseUrl() = assetServerBaseUrl

}