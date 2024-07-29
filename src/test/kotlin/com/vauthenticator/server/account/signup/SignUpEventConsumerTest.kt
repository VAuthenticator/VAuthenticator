package com.vauthenticator.server.account.signup

import com.vauthenticator.server.account.Email
import com.vauthenticator.server.events.SignUpEvent
import com.vauthenticator.server.oauth2.clientapp.ClientAppFixture
import com.vauthenticator.server.password.Password
import com.vauthenticator.server.password.PasswordHistoryRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Instant

@ExtendWith(MockKExtension::class)
class SignUpEventConsumerTest {

    @MockK
    lateinit var passwordHistoryRepository: PasswordHistoryRepository

    @Test
    fun `when upon a sign up event the password history is updated`() {
        val password = Password("A_PASSWORD")
        val userName = Email("AN_EMAIL")
        val event = SignUpEvent(userName, ClientAppFixture.aClientAppId(), Instant.now(), password)

        val uut = SignUpEventConsumer(passwordHistoryRepository)

        every { passwordHistoryRepository.store(userName.content, password) } just runs

        uut.accept(event)

        verify { passwordHistoryRepository.store(userName.content, password) }
    }
}