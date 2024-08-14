package com.vauthenticator.server.mfa

import com.hubspot.jinjava.Jinjava
import com.vauthenticator.document.repository.DocumentRepository
import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.email.*
import com.vauthenticator.server.keys.KeyDecrypter
import com.vauthenticator.server.keys.KeyRepository
import com.vauthenticator.server.keys.MasterKid
import com.vauthenticator.server.mask.SensitiveEmailMasker
import com.vauthenticator.server.mfa.adapter.dynamodb.DynamoMfaAccountMethodsRepository
import com.vauthenticator.server.mfa.domain.*
import com.vauthenticator.server.ticket.TicketCreator
import com.vauthenticator.server.ticket.TicketRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.util.*

@Configuration(proxyBeanMethods = false)
class MfaConfig {

    @Bean
    fun mfaAccountMethodsRepository(
        keyRepository: KeyRepository,
        dynamoDbClient: DynamoDbClient,
        @Value("\${key.master-key}") masterKey: String,
        @Value("\${vauthenticator.dynamo-db.mfa-account-methods.table-name}") mfaAccountMethodTableName: String,
        @Value("\${vauthenticator.dynamo-db.default-mfa-account-methods.table-name}") defaultMfaAccountMethodTableName: String
    ): MfaAccountMethodsRepository =
        DynamoMfaAccountMethodsRepository(
            mfaAccountMethodTableName,
            defaultMfaAccountMethodTableName,
            dynamoDbClient,
            keyRepository,
            MasterKid(masterKey)
        ) { MfaDeviceId(UUID.randomUUID().toString()) }

    @Bean
    fun sensitiveEmailMasker() = SensitiveEmailMasker()

    @Bean
    fun mfaMethodsEnrolmentAssociation(
        ticketRepository: TicketRepository,
        mfaAccountMethodsRepository: MfaAccountMethodsRepository,
        otpMfaVerifier: OtpMfaVerifier
    ) =
        MfaMethodsEnrollmentAssociation(ticketRepository, mfaAccountMethodsRepository, otpMfaVerifier)

    @Bean
    fun mfaMethodsEnrollment(
        mfaSender: OtpMfaSender,
        ticketCreator: TicketCreator,
        accountRepository: AccountRepository,
        mfaAccountMethodsRepository: MfaAccountMethodsRepository
    ) = MfaMethodsEnrollment(accountRepository, ticketCreator, mfaSender, mfaAccountMethodsRepository)

    @Bean
    fun otpMfa(
        keyRepository: KeyRepository,
        keyDecrypter: KeyDecrypter,
        mfaAccountMethodsRepository: MfaAccountMethodsRepository,
        otpConfigurationProperties: OtpConfigurationProperties
    ) = TaimosOtpMfa(
        keyDecrypter,
        keyRepository,
        mfaAccountMethodsRepository,
        otpConfigurationProperties
    )

    @Bean
    fun otpMfaSender(
        accountRepository: AccountRepository,
        otpMfa: OtpMfa,
        mfaMailSender: EMailSenderService,
        mfaAccountMethodsRepository: MfaAccountMethodsRepository
    ) = OtpMfaEmailSender(accountRepository, otpMfa, mfaMailSender, mfaAccountMethodsRepository)

    @Bean
    fun otpMfaVerifier(
        otpMfa: OtpMfa,
        accountRepository: AccountRepository,
        mfaAccountMethodsRepository: MfaAccountMethodsRepository,
    ) = AccountAwareOtpMfaVerifier(accountRepository, otpMfa, mfaAccountMethodsRepository)

    @Bean
    fun mfaMailSender(
        javaMailSender: JavaMailSender,
        documentRepository: DocumentRepository,
        noReplyEMailConfiguration: NoReplyEMailConfiguration
    ) =
        JavaEMailSenderService(
            documentRepository,
            javaMailSender,
            JinjavaMailTemplateResolver(Jinjava()),
            SimpleEMailMessageFactory(
                noReplyEMailConfiguration.from,
                noReplyEMailConfiguration.mfaEMailSubject,
                EMailType.MFA
            )
        )
}

@ConfigurationProperties("mfa.otp")
data class OtpConfigurationProperties(val length: Int, val timeToLiveInSeconds: Int)