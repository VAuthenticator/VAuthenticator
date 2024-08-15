package com.vauthenticator.server.support

import com.vauthenticator.server.mfa.domain.MfaMethod
import com.vauthenticator.server.ticket.Ticket
import com.vauthenticator.server.ticket.TicketContext
import com.vauthenticator.server.ticket.TicketId

object TicketFixture {

    fun ticketContext(email: String, selfAssociation: String = "false", mfaDeviceId: String = "A_MFA_DEVICE_ID") = TicketContext(
        mapOf(
            "mfaDeviceId" to mfaDeviceId,
            "mfaChannel" to email,
            "mfaMethod" to MfaMethod.EMAIL_MFA_METHOD.name,
            "selfAssociation" to selfAssociation
        )
    )

    fun ticketFor(verificationTicketValue: String, email: String, clientAppId: String): Ticket {

        return Ticket(
            TicketId(verificationTicketValue), email, clientAppId, 200,
            ticketContext(email, mfaDeviceId = "A_MFA_DEVICE_ID")
        )
    }
}