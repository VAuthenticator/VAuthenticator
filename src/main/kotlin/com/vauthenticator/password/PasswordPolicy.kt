package com.vauthenticator.password

import org.springframework.boot.context.properties.ConfigurationProperties

interface PasswordPolicy {

    fun accept(password: String)
}

class PasswordPolicyViolation(message: String) : RuntimeException(message)

class NoPasswordPolicy : PasswordPolicy {
    override fun accept(password: String) {

    }

}

class SpecialCharacterPasswordPolicy(private val minSpecialSymbol: Int) : PasswordPolicy {
    private val pattern = "^[A-Za-z0-9\\s]+".toRegex()
    override fun accept(password: String) {
        val counter = password.count { c -> !pattern.matches(c.toString()) }
        if (counter < minSpecialSymbol) {
            throw PasswordPolicyViolation("the password should has at least $minSpecialSymbol special character like '!£\$%&/()=?. In your password you have $counter'")
        }
    }
}

class MinimumCharacterPasswordPolicy(private val size: Int) : PasswordPolicy {
    override fun accept(password: String) {
        if (password.length < size) {
            throw PasswordPolicyViolation("the password size should be at least $size")
        }
    }
}


class CompositePasswordPolicy(private val passwordPolicies: Set<PasswordPolicy>) : PasswordPolicy {
    override fun accept(password: String) {
        passwordPolicies.forEach { it.accept(password) }
    }
}

@ConfigurationProperties(prefix = "password.policy")
data class PasswordPolicyConfigProp(
    val passwordMinSize: Int = 0,
    val passwordMinSpecialSymbol: Int = 0
)