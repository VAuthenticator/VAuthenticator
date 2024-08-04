package com.vauthenticator.server.mfa.web

import com.vauthenticator.server.i18n.I18nMessageInjector
import com.vauthenticator.server.i18n.I18nScope
import com.vauthenticator.server.mfa.domain.*
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.*

@Controller
class MfaController(
    private val i18nMessageInjector: I18nMessageInjector,
    private val publisher: ApplicationEventPublisher,
    private val nextHopeLoginWorkflowSuccessHandler: AuthenticationSuccessHandler,
    private val mfaFailureHandler: AuthenticationFailureHandler,
    private val otpMfaSender: OtpMfaSender,
    private val otpMfaVerifier: OtpMfaVerifier
) {
    private val logger = LoggerFactory.getLogger(MfaController::class.java)

    @GetMapping("/mfa-challenge/send")
    fun view(authentication: Authentication): String {
        otpMfaSender.sendMfaChallenge(authentication.name, MfaMethod.EMAIL_MFA_METHOD, authentication.name)
        return "redirect:/mfa-challenge"
    }

    @GetMapping("/mfa-challenge")
    fun view(
        model: Model,
        authentication: Authentication,
        httpServletRequest: HttpServletRequest
    ): String {
        model.addAttribute("assetBundle", "mfa_bundle.js")
        i18nMessageInjector.setMessagedFor(I18nScope.MFA_PAGE, model)

        return "template"
    }

    @PostMapping("/mfa-challenge")
    fun processSecondFactor(
        @RequestParam("mfa-code") mfaCode: String,
        @RequestParam("mfa-device-id") mfaDeviceId: String,
        @RequestParam("mfa-method") mfaMethod: MfaMethod,
        @RequestParam("mfa-channel", required = false) mfaChannel: Optional<String>,
        authentication: Authentication,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        try {
            val defaultMfaChannel = mfaChannel.orElseGet { authentication.name }

            otpMfaVerifier.verifyAssociatedMfaChallengeFor(authentication.name, mfaMethod, defaultMfaChannel, MfaChallenge(mfaCode))
            publisher.publishEvent(MfaSuccessEvent(authentication))

            nextHopeLoginWorkflowSuccessHandler.onAuthenticationSuccess(request, response, authentication)
        } catch (e: Exception) {
            logger.error(e.message, e)

            val mfaException = MfaException("Invalid mfa code")
            publisher.publishEvent(MfaFailureEvent(authentication, mfaException))

            mfaFailureHandler.onAuthenticationFailure(request, response, mfaException)
        }
    }
}

