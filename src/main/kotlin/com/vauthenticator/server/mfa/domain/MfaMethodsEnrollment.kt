package com.vauthenticator.server.mfa.domain

import com.vauthenticator.server.account.AccountNotFoundException
import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.mask.SensitiveEmailMasker
import com.vauthenticator.server.oauth2.clientapp.domain.ClientAppId
import com.vauthenticator.server.ticket.TicketContext
import com.vauthenticator.server.ticket.TicketCreator
import com.vauthenticator.server.ticket.TicketId
import org.slf4j.LoggerFactory

class MfaMethodsEnrollment(
    private val accountRepository: AccountRepository,
    private val ticketCreator: TicketCreator,
    private val mfaSender: MfaChallengeSender,
    private val mfaAccountMethodsRepository: MfaAccountMethodsRepository,
    private val sensitiveEmailMasker: SensitiveEmailMasker,
) {

    private val logger = LoggerFactory.getLogger(MfaMethodsEnrollment::class.java)

    fun getEnrollmentsFor(userName: String, withMaskedSensibleInformation: Boolean = false): List<MfaDevice> =
        mfaAccountMethodsRepository.getDefaultDevice(userName)
            .map { defaultMfaDevice ->
                mfaAccountMethodsRepository.findAll(userName)
                    .map {
                        MfaDevice(
                            if (withMaskedSensibleInformation) sensitiveEmailMasker.mask(it.userName) else it.userName,
                            it.mfaMethod,
                            if (withMaskedSensibleInformation) sensitiveEmailMasker.mask(it.mfaChannel) else it.mfaChannel,
                            it.mdaDeviceId,
                            it.mdaDeviceId.content == defaultMfaDevice.content
                        )
                    }
            }.orElseGet { emptyList() }


    fun enroll(
        userName: String,
        mfaMethod: MfaMethod,
        mfaChannel: String,
        clientAppId: ClientAppId,
        sendChallengeCode: Boolean = true,
        ticketContextAdditionalProperties: Map<String, String> = emptyMap()
    ): TicketId {
        return accountRepository.accountFor(userName)
            .map {
                val mfaAccountMethod = mfaAccountMethodsRepository.findBy(userName, mfaMethod, mfaChannel)
                    .orElseGet { mfaAccountMethodsRepository.save(userName, mfaMethod, mfaChannel, false) }

                if (sendChallengeCode) {
                    mfaSender.sendMfaChallengeFor(mfaAccountMethod.userName, mfaAccountMethod.mdaDeviceId)
                }

                ticketCreator.createTicketFor(
                    it,
                    clientAppId,
                    TicketContext.mfaContextFor(
                        mfaDeviceId = mfaAccountMethod.mdaDeviceId.content,
                        mfaMethod = mfaMethod,
                        mfaChannel = mfaChannel,
                        ticketContextAdditionalProperties = ticketContextAdditionalProperties
                    )
                )
            }.orElseThrow {
                logger.warn("account not found")
                AccountNotFoundException("account not found")
            }
    }
}