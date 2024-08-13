package com.vauthenticator.server.account.signup

import com.vauthenticator.server.account.Account
import com.vauthenticator.server.account.Email
import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.events.SignUpEvent
import com.vauthenticator.server.events.VAuthenticatorEventsDispatcher
import com.vauthenticator.server.oauth2.clientapp.domain.ClientAppId
import com.vauthenticator.server.password.Password
import com.vauthenticator.server.password.PasswordPolicy
import com.vauthenticator.server.password.VAuthenticatorPasswordEncoder
import com.vauthenticator.server.role.Role
import java.time.Instant

open class SignUpUse(
    private val passwordPolicy: PasswordPolicy,
    private val accountRepository: AccountRepository,
    private val vAuthenticatorPasswordEncoder: VAuthenticatorPasswordEncoder,
    private val eventsDispatcher: VAuthenticatorEventsDispatcher
) {
    open fun execute(clientAppId: ClientAppId, account: Account) {
        passwordPolicy.accept(account.email, account.password)
        val encodedPassword = vAuthenticatorPasswordEncoder.encode(account.password)
        val registeredAccount = account.copy(
            authorities = account.authorities + setOf(Role.defaultRole().name),
            password = encodedPassword
        )
        accountRepository.create(registeredAccount)

        eventsDispatcher.dispatch(
            SignUpEvent(Email(account.email), clientAppId, Instant.now(), Password(encodedPassword))
        )
    }
}