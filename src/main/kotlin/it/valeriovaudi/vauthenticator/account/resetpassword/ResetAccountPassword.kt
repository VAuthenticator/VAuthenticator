package it.valeriovaudi.vauthenticator.account.resetpassword

import it.valeriovaudi.vauthenticator.account.repository.AccountRepository
import it.valeriovaudi.vauthenticator.account.tiket.InvalidTicketException
import it.valeriovaudi.vauthenticator.account.tiket.Ticket
import it.valeriovaudi.vauthenticator.account.tiket.TicketRepository
import it.valeriovaudi.vauthenticator.account.tiket.VerificationTicket
import it.valeriovaudi.vauthenticator.password.VAuthenticatorPasswordEncoder
import java.util.*

class ResetAccountPassword(
    private val accountRepository: AccountRepository,
    private val vAuthenticatorPasswordEncoder: VAuthenticatorPasswordEncoder,

    private val ticketRepository: TicketRepository
) {
    fun resetPasswordFromMailChallenge(verificationTicket: VerificationTicket, request: ResetPasswordRequest) {
        ticketRepository.loadFor(verificationTicket).map {
            passwordResetFor(it, request)
            ticketRepository.delete(verificationTicket)
        }
            .orElseThrow { throw InvalidTicketException("Te ticket ${verificationTicket.content} is not a valid ticket it seems to be used or expired") }
    }

    private fun passwordResetFor(it: Ticket, request: ResetPasswordRequest): Optional<Unit> =
        accountRepository.accountFor(it.email).map {
            val newAccount = it.copy(password = vAuthenticatorPasswordEncoder.encode(request.newPassword))
            accountRepository.save(newAccount)
        }

}