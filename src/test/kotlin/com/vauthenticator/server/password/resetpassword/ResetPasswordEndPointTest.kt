package com.vauthenticator.server.password.resetpassword

import com.fasterxml.jackson.databind.ObjectMapper
import com.vauthenticator.server.account.ticket.VerificationTicket
import com.vauthenticator.server.clientapp.A_CLIENT_APP_ID
import com.vauthenticator.server.clientapp.ClientAppFixture.aClientApp
import com.vauthenticator.server.oauth2.clientapp.ClientAppId
import com.vauthenticator.server.oauth2.clientapp.ClientApplicationRepository
import com.vauthenticator.server.oauth2.clientapp.Scope
import com.vauthenticator.server.role.PermissionValidator
import com.vauthenticator.server.support.EMAIL
import com.vauthenticator.server.support.SecurityFixture.principalFor
import com.vauthenticator.server.support.VAUTHENTICATOR_ADMIN
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.util.*

@ExtendWith(MockKExtension::class)
internal class ResetPasswordEndPointTest {
    private val objectMapper = ObjectMapper()

    lateinit var mokMvc: MockMvc

    val clientAppId = ClientAppId(A_CLIENT_APP_ID)


    @MockK
    lateinit var sendResetPasswordMailChallenge: SendResetPasswordMailChallenge

    @MockK
    lateinit var clientApplicationRepository: ClientApplicationRepository

    @MockK
    lateinit var resetAccountPassword: ResetAccountPassword

    @BeforeEach
    internal fun setUp() {
        mokMvc =
            MockMvcBuilders.standaloneSetup(
                ResetPasswordEndPoint(
                    PermissionValidator(clientApplicationRepository),
                    sendResetPasswordMailChallenge,
                    resetAccountPassword
                )
            )
                .build()
    }

    @Test
    internal fun `when a challenge is sent`() {
        val principal = principalFor(
            A_CLIENT_APP_ID,
            EMAIL,
            listOf(VAUTHENTICATOR_ADMIN),
            listOf(Scope.RESET_PASSWORD.content)
        )
        every { sendResetPasswordMailChallenge.sendResetPasswordMailFor(EMAIL) } just runs

        mokMvc.perform(
            put("/api/reset-password-challenge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(mapOf("mail" to EMAIL)))
                .principal(principal)
        )
            .andExpect(status().isNoContent)
    }

    @Test
    internal fun `when a password is reset as anonymous but starting from ui`() {
        every { sendResetPasswordMailChallenge.sendResetPasswordMailFor(EMAIL) } just runs
        every { clientApplicationRepository.findOne(clientAppId) } returns Optional.of(
            aClientApp(
                ClientAppId(
                    A_CLIENT_APP_ID
                )
            )
        )

        mokMvc.perform(
            put("/api/reset-password-challenge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(mapOf("mail" to EMAIL)))
                .sessionAttr("clientId", A_CLIENT_APP_ID)
        ).andExpect(status().isNoContent)
    }

    @Test
    internal fun `when a password is reset`() {
        val request = ResetPasswordRequest("A_NEW_PSWD")
        val ticket = "A_TICKET"

        every { resetAccountPassword.resetPasswordFromMailChallenge(VerificationTicket(ticket), request) } just runs
        mokMvc.perform(
            put("/api/reset-password/{ticket}", ticket)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request))
        )
            .andExpect(status().isNoContent)
    }

}