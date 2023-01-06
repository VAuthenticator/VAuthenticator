package com.vauthenticator.config

import com.vauthenticator.keys.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.kms.KmsClient
import java.util.*

@Configuration(proxyBeanMethods = false)
class KeyConfig {

    @Bean
    fun keyGenerator(kmsClient: KmsClient): KeyGenerator = KmsKeyGenerator(kmsClient)

    @Bean
    fun keyDecrypter(kmsClient: KmsClient): KeyDecrypter = KmsKeyDecrypter(kmsClient)

    @Bean
    fun keyRepository(
        keyGenerator: KeyGenerator,
        keyDecrypter: KeyDecrypter,
        dynamoDbClient: DynamoDbClient,
        @Value("\${vauthenticator.dynamo-db.keys.signature.table-name}") signatureTableName: String,
        @Value("\${vauthenticator.dynamo-db.keys.mfa.table-name}") mfaTableName: String
    ): KeyRepository =
        AwsKeyRepository(
            { UUID.randomUUID().toString() },
            signatureTableName,
            mfaTableName,
            keyGenerator,
            dynamoDbClient
        )
}