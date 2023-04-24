package com.vauthenticator.server.account.welcome

import com.vauthenticator.server.account.AccountNotFoundException
import com.vauthenticator.server.oauth2.clientapp.ClientApplicationRepository
import com.vauthenticator.server.oauth2.clientapp.Scope
import com.vauthenticator.server.role.PermissionValidator
import com.vauthenticator.server.support.SecurityFixture.principalFor
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.util.*

@ExtendWith(MockKExtension::class)
internal class WelcomeMailEndPointTest {
    lateinit var mokMvc: MockMvc

    @MockK
    lateinit var sayWelcome: SayWelcome

    @MockK
    lateinit var clientApplicationRepository: ClientApplicationRepository

    @BeforeEach
    internal fun setUp() {
        mokMvc = MockMvcBuilders.standaloneSetup(WelcomeMailEndPoint(PermissionValidator(clientApplicationRepository), sayWelcome))
            .build()
    }

    @Test
    internal fun `happy path`() {
        val principal = principalFor(
            "A_CLIENT_APP_ID",
            "email@domain.com",
            listOf("VAUTHENTICATOR_ADMIN"),
            listOf(Scope.WELCOME.content)
        )
        every { sayWelcome.welcome("email@domain.com") } just runs

        mokMvc.perform(put("/api/sign-up/mail/email@domain.com/welcome")
            .principal(principal))
            .andExpect(status().isNoContent)

        verify { sayWelcome.welcome("email@domain.com") }
    }

    @Test
    internal fun `no account found`() {
        every { sayWelcome.welcome("email@domain.com") } throws AccountNotFoundException("")

        mokMvc.perform(put("/api/sign-up/mail/email@domain.com/welcome"))
            .andExpect(status().isNotFound)
    }
}