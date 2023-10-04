package com.vauthenticator.server.config

import com.vauthenticator.server.events.*
import com.vauthenticator.server.password.UpdatePasswordHistoryUponSignUpEventConsumer
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
class EventsConfig(private val eventConsumerConfig: EventConsumerConfig) {

    @Bean
    fun eventsDispatcherAdapter(publisher: ApplicationEventPublisher) =
        SpringEventEventsDispatcher(publisher)

    @Bean
    fun eventsDispatcher(publisher: ApplicationEventPublisher) =
        VAuthenticatorEventsDispatcher(publisher)

    @Bean
    fun eventsCollector(
        updatePasswordHistoryUponSignUpEventConsumer: UpdatePasswordHistoryUponSignUpEventConsumer,
        loggerEventConsumer: EventConsumer
    ) =
        SpringEventsCollector(
            listOf(
                loggerEventConsumer,
                updatePasswordHistoryUponSignUpEventConsumer
            )
        )

    @Bean
    fun loggerEventConsumer() = LoggerEventConsumer(eventConsumerConfig)

}