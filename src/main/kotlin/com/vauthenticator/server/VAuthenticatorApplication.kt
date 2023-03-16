package com.vauthenticator.server

import com.vauthenticator.server.events.EventConsumerConfig
import com.vauthenticator.server.mail.NoReplyMailConfiguration
import com.vauthenticator.server.mfa.OtpConfigurationProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(
    NoReplyMailConfiguration::class,
    OtpConfigurationProperties::class,
    EventConsumerConfig::class
)
class VAuthenticatorApplication

fun main(args: Array<String>) {
    runApplication<VAuthenticatorApplication>(*args)
}