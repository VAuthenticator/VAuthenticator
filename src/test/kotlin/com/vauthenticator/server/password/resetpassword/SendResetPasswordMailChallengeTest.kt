package com.vauthenticator.server.password.resetpassword

import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.communication.email.EMailSenderService
import com.vauthenticator.server.oauth2.clientapp.domain.ClientAppId
import com.vauthenticator.server.support.AccountTestFixture.anAccount
import com.vauthenticator.server.ticket.TicketCreator
import com.vauthenticator.server.ticket.TicketId
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
internal class SendResetPasswordMailChallengeTest {

    lateinit var underTest: SendResetPasswordMailChallenge

    @MockK
    lateinit var accountRepository: AccountRepository

    @MockK
    lateinit var ticketCreator: TicketCreator

    @MockK
    lateinit var emailSenderService: EMailSenderService

    @BeforeEach
    internal fun setUp() {
        underTest = SendResetPasswordMailChallenge(
            accountRepository,
            ticketCreator,
            emailSenderService,
            "https://vauthenticator.com"
        )
    }

    @Test
    internal fun `when the reset password challenge is sent`() {
        val anAccount = anAccount()

        every { accountRepository.accountFor(anAccount.email) } returns Optional.of(anAccount)
        every { ticketCreator.createTicketFor(anAccount, ClientAppId.empty()) } returns TicketId("A_TICKET")
        every {
            emailSenderService.sendFor(
                anAccount,
                mapOf("resetPasswordLink" to "https://vauthenticator.com/reset-password/A_TICKET")
            )
        } just runs

        underTest.sendResetPasswordMailFor(anAccount.email)
    }

}