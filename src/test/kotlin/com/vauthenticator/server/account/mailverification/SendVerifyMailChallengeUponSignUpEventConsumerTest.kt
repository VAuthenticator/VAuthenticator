package com.vauthenticator.server.account.mailverification

import com.vauthenticator.server.events.EventFixture.signUpEvent
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class SendVerifyMailChallengeUponSignUpEventConsumerTest {

    @MockK
    lateinit var mailChallenge: SendVerifyMailChallenge

    @Test
    fun `when the verify mail challenge is sent`() {
        val uut = SendVerifyMailChallengeUponSignUpEventConsumer(mailChallenge)

        every { mailChallenge.sendVerifyMail("AN_EMAIL") } just runs

        uut.accept(signUpEvent)
        assertEquals(true, uut.handleable(signUpEvent))

        verify { mailChallenge.sendVerifyMail("AN_EMAIL") }
    }
}