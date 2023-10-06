package com.vauthenticator.server.account.welcome

import com.vauthenticator.server.events.EventConsumer
import com.vauthenticator.server.events.SignUpEvent
import com.vauthenticator.server.events.VAuthenticatorEvent
import org.springframework.stereotype.Service

@Service
class SendWelcomeMailUponSignUpEventConsumer(
    private val sayWelcome: SayWelcome
) : EventConsumer {
    override fun accept(event: VAuthenticatorEvent) {
        sayWelcome.welcome(event.userName.content)
    }

    override fun handleable(event: VAuthenticatorEvent): Boolean = event is SignUpEvent

}