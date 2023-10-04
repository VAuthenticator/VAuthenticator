package com.vauthenticator.server.password

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

private const val A_USERNAME = "A_USERNAME"

@ExtendWith(MockKExtension::class)
internal class PasswordPolicyTest {

    @MockK
    private lateinit var firstPolicy: PasswordPolicy

    @MockK
    private lateinit var secondPolicy: PasswordPolicy

    @MockK
    private lateinit var passwordHistoryRepository: PasswordHistoryRepository

    @MockK
    private lateinit var passwordEncoder: VAuthenticatorPasswordEncoder

    @Test
    internal fun `when a password has no special character`() {
        val underTest = SpecialCharacterPasswordPolicy(2)
        assertThrows(PasswordPolicyViolation::class.java) { underTest.accept(A_USERNAME,"aPassword") }
    }

    @Test
    internal fun `when a password has enoguht special  character`() {
        val underTest = SpecialCharacterPasswordPolicy(2)
        underTest.accept(A_USERNAME,"aPa!%ssword")
    }

    @Test
    internal fun `when a password has not allowed special character`() {
        val underTest = MinimumCharacterPasswordPolicy(8)
        assertThrows(PasswordPolicyViolation::class.java) { underTest.accept(A_USERNAME,"1245789") }
    }

    @Test
    internal fun `when a set of password policies are invoked`() {
        val underTest = CompositePasswordPolicy(setOf(firstPolicy, secondPolicy))

        every { firstPolicy.accept(A_USERNAME,"1245789") } just runs
        every { secondPolicy.accept(A_USERNAME,"1245789") } throws PasswordPolicyViolation("")

        assertThrows(PasswordPolicyViolation::class.java) { underTest.accept(A_USERNAME,"1245789") }
    }

    @Test
    fun `when a password is used too early`() {
        val password = "A_PASSWORD"
        val passwordHistory = listOf(
            Password("A_PASSWORD"),
            Password("A_PASSWORD_1"),
            Password("A_PASSWORD_2")
        )

        val uut = ReusePreventionPasswordPolicy(
            passwordEncoder,
            passwordHistoryRepository
        )

        every { passwordEncoder.encode(password) } returns password
        every { passwordHistoryRepository.load(A_USERNAME) } returns passwordHistory

        assertThrows(PasswordPolicyViolation::class.java) { uut.accept(A_USERNAME,password) }
    }
}