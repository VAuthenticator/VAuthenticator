package com.vauthenticator.server.events

import com.vauthenticator.server.account.Email
import com.vauthenticator.server.oauth2.clientapp.ClientAppId
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.authentication.event.AuthenticationSuccessEvent
import java.time.Instant

object EventFixture {
    val vauthenticatorAuthEvent = VAuthenticatorAuthEvent(
        Email("anemail@domain.com"),
        ClientAppId("A_CLIENT_APP_ID"),
        Instant.now(),
        AuthenticationSuccessEvent(UsernamePasswordAuthenticationToken("anemail@domain.com", "*******"))
    )
}