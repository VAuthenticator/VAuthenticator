package com.vauthenticator.server.login

import com.fasterxml.jackson.databind.ObjectMapper
import com.vauthenticator.server.extentions.hasEnoughScopes
import com.vauthenticator.server.extentions.oauth2ClientId
import com.vauthenticator.server.oauth2.clientapp.ClientAppId
import com.vauthenticator.server.oauth2.clientapp.ClientApplicationFeatures
import com.vauthenticator.server.oauth2.clientapp.ClientApplicationRepository
import com.vauthenticator.server.oauth2.clientapp.Scope
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpSession
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.SessionAttributes
import java.util.*


@Controller
@SessionAttributes("clientId", "features")
class LoginPageController(
    val clientApplicationRepository: ClientApplicationRepository,
    val objectMapper: ObjectMapper
) {
    val logger: Logger = LoggerFactory.getLogger(LoginPageController::class.java)

    @GetMapping("/login")
    fun loginPage(session: HttpSession, model: Model, httpServletRequest: HttpServletRequest): String {
        val clientId = session.oauth2ClientId()

        val features = defaultFeature()
        clientAppFeaturesFor(clientId, model, features)

        val errors = errorMessageFor(httpServletRequest)

        model.addAttribute("errors", objectMapper.writeValueAsString(errors))
        model.addAttribute("features", objectMapper.writeValueAsString(features))
        model.addAttribute("assetBundle", "login_bundle.js")

        return "template"
    }

    private fun clientAppFeaturesFor(
        clientId: Optional<ClientAppId>,
        model: Model,
        features: MutableMap<String, Boolean>
    ) {
        clientId.ifPresent {
            model.addAttribute("clientId", it.content)
            clientApplicationRepository.findOne(it)
                .map { clientApp ->
                    logger.debug("clientApp.scopes.content: ${clientApp.scopes.content}")
                    features[ClientApplicationFeatures.SIGNUP.value] = clientApp.hasEnoughScopes(Scope.SIGN_UP)
                    features[ClientApplicationFeatures.RESET_PASSWORD.value] =
                        clientApp.hasEnoughScopes(Scope.RESET_PASSWORD)
                }
        }
    }

    private fun errorMessageFor(httpServletRequest: HttpServletRequest) =
        if (hasBadLoginFrom(httpServletRequest)) {
            mapOf("login" to "Something goes wrong during your login request")
        } else {
            emptyMap()
        }

    private fun hasBadLoginFrom(httpServletRequest: HttpServletRequest) =
        !Optional.ofNullable(httpServletRequest.session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION")).isEmpty
                && httpServletRequest.parameterMap.contains("error")


    private fun defaultFeature() =
        mutableMapOf(
            ClientApplicationFeatures.SIGNUP.value to false,
            ClientApplicationFeatures.RESET_PASSWORD.value to false
        )

}