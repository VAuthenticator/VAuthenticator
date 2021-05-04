package it.valeriovaudi.vauthenticator.config

import it.valeriovaudi.vauthenticator.account.AccountRepository
import it.valeriovaudi.vauthenticator.account.database.JdbcAccountRepository
import it.valeriovaudi.vauthenticator.account.dynamo.DynamoDbAccountRepository
import it.valeriovaudi.vauthenticator.role.JdbcRoleRepository
import it.valeriovaudi.vauthenticator.extentions.VAuthenticatorPasswordEncoder
import it.valeriovaudi.vauthenticator.keypair.KeyPairConfig
import it.valeriovaudi.vauthenticator.keypair.RestKeyRepository
import it.valeriovaudi.vauthenticator.oauth2.clientapp.DynamoClientApplicationConverter
import it.valeriovaudi.vauthenticator.oauth2.clientapp.DynamoDbClientApplicationRepository
import it.valeriovaudi.vauthenticator.oauth2.clientapp.JdbcClientApplicationRepository
import it.valeriovaudi.vauthenticator.openid.connect.userinfo.UserInfoFactory
import it.valeriovaudi.vauthenticator.role.DynamoDbRoleRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.client.RestTemplate
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import javax.sql.DataSource


@Configuration
class RepositoryConfig {

    @Bean
    fun userInfoFactory(accountRepository: AccountRepository) =
            UserInfoFactory(accountRepository)

    @Bean
    @ConfigurationProperties(prefix = "key-store")
    fun keyPairConfig() = KeyPairConfig()

    @Bean("keyRepository")
    fun keyRepository(@Value("\${repository-service.baseUrl}") repositoryServiceUrl: String,
                      @Value("\${repository-service.serviceRegistrationName}") registrationName: String,
                      keyPairConfig: KeyPairConfig): RestKeyRepository =
            RestKeyRepository(RestTemplate(), repositoryServiceUrl, registrationName, keyPairConfig)

}

@Configuration
@ConditionalOnProperty(name = ["repository.type.database"], havingValue = "database")
class DatabaseRepositoryConfig {

    @Bean
    fun accountRepository(dataSource: DataSource) =
            JdbcAccountRepository(JdbcTemplate(dataSource))

    @Bean
    fun roleRepository(dataSource: DataSource) =
            JdbcRoleRepository(JdbcTemplate(dataSource))

    @Bean
    fun clientApplicationRepository(dataSource: DataSource, vAuthenticatorPasswordEncoder: VAuthenticatorPasswordEncoder) =
            JdbcClientApplicationRepository(JdbcTemplate(dataSource))

}

@Configuration
@ConditionalOnProperty(prefix = "repository.type.database", havingValue = "dyanamo")
class DynamoDbRepositoryConfig {

    @Bean
    fun dynamoDbClient() = DynamoDbClient.builder()
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build()

    @Bean
    fun accountRepository(dynamoDbClient: DynamoDbClient,
                          @Value("\${dynamo-db.account.table-name}") accountTableName: String,
                          @Value("\${dynamo-db.account.role.table-name}") accountRoleTableName: String
    ) = DynamoDbAccountRepository(dynamoDbClient, accountTableName, accountRoleTableName)

    @Bean
    fun roleRepository(dynamoDbClient: DynamoDbClient,
                       @Value("\${dynamo-db.role.table-name}") roleTableName: String) =
            DynamoDbRoleRepository(dynamoDbClient, roleTableName)

    @Bean
    fun clientApplicationRepository(dynamoDbClient: DynamoDbClient,
                                    @Value("\${dynamo-db.client-application.table-name}") clientAppTableName: String) =
            DynamoDbClientApplicationRepository(dynamoDbClient, clientAppTableName)

}