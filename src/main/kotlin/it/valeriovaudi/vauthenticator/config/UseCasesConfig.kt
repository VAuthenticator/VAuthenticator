package it.valeriovaudi.vauthenticator.config

import it.valeriovaudi.vauthenticator.account.mailverification.DynamoDbMailVerificationTicketFactory
import it.valeriovaudi.vauthenticator.account.mailverification.MailVerificationTicketFactory
import it.valeriovaudi.vauthenticator.account.mailverification.MailVerificationUseCase
import it.valeriovaudi.vauthenticator.account.repository.AccountRepository
import it.valeriovaudi.vauthenticator.account.signup.SignUpUseCase
import it.valeriovaudi.vauthenticator.account.tiket.VerificationTicketFeatures
import it.valeriovaudi.vauthenticator.mail.MailSenderService
import it.valeriovaudi.vauthenticator.mail.NoReplyMailConfiguration
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientApplicationRepository
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ReadClientApplication
import it.valeriovaudi.vauthenticator.oauth2.clientapp.StoreClientApplication
import it.valeriovaudi.vauthenticator.security.VAuthenticatorPasswordEncoder
import it.valeriovaudi.vauthenticator.time.UtcClocker
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.time.Duration
import java.util.*

@EnableConfigurationProperties(NoReplyMailConfiguration::class)
@Configuration(proxyBeanMethods = false)
class UseCasesConfig {

    @Bean
    fun mailVerificationTicketFactory(@Value("\${vauthenticator.dynamo-db.ticket.table-name}") tableName: String,
                                      dynamoDbClient: DynamoDbClient) =
            DynamoDbMailVerificationTicketFactory(tableName,
                    dynamoDbClient,
                    { UUID.randomUUID().toString() },
                    UtcClocker(),
                    VerificationTicketFeatures(Duration.ofMinutes(5), false)
            )

    @Bean
    fun readClientApplication(clientApplicationRepository: ClientApplicationRepository) =
            ReadClientApplication(clientApplicationRepository)

    @Bean
    fun storeClientApplication(clientApplicationRepository: ClientApplicationRepository,
                               passwordEncoder: VAuthenticatorPasswordEncoder) =
            StoreClientApplication(clientApplicationRepository, passwordEncoder)

    @Bean
    fun signUpUseCase(clientAccountRepository: ClientApplicationRepository,
                      accountRepository: AccountRepository,
                      welcomeMailSender: MailSenderService,
                      vAuthenticatorPasswordEncoder: VAuthenticatorPasswordEncoder): SignUpUseCase {
        return SignUpUseCase(clientAccountRepository, accountRepository, welcomeMailSender, vAuthenticatorPasswordEncoder)
    }

    @Bean
    fun mailVerificationUseCase(clientAccountRepository: ClientApplicationRepository,
                                accountRepository: AccountRepository,
                                mailVerificationTicketFactory: MailVerificationTicketFactory,
                                verificationMailSender: MailSenderService,
                                @Value("\${vauthenticator.host}") frontChannelBaseUrl: String) =
            MailVerificationUseCase(clientAccountRepository,
                    accountRepository,
                    mailVerificationTicketFactory,
                    verificationMailSender,
                    frontChannelBaseUrl)
}