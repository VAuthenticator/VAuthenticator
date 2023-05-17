package com.vauthenticator.server.account.repository

import com.vauthenticator.server.account.Account
import com.vauthenticator.server.account.AccountTestFixture.anAccount
import com.vauthenticator.server.role.DynamoDbRoleRepository
import com.vauthenticator.server.role.Role
import com.vauthenticator.server.support.DatabaseUtils.dynamoAccountTableName
import com.vauthenticator.server.support.DatabaseUtils.dynamoDbClient
import com.vauthenticator.server.support.DatabaseUtils.dynamoRoleTableName
import com.vauthenticator.server.support.DatabaseUtils.resetDatabase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

internal class DynamoDbAccountRepositoryTest {

    private val role = Role("role", "description")
    private val anotherRole = Role("another_role", "description")
    private val account = anAccount(setOf(role))
    private lateinit var accountRepository: DynamoDbAccountRepository
    private lateinit var roleRepository: DynamoDbRoleRepository

    @BeforeEach
    fun setUp() {
        resetDatabase()

        roleRepository = DynamoDbRoleRepository(
            dynamoDbClient,
            dynamoRoleTableName
        )

        accountRepository = DynamoDbAccountRepository(
            dynamoDbClient,
            dynamoAccountTableName,
            roleRepository
        )


        roleRepository.save(role)
        roleRepository.save(anotherRole)
    }

    @Test
    fun `find an account by email`() {
        accountRepository.save(account)
        val findByUsername: Account = accountRepository.accountFor(account.username).orElseThrow()

        assertEquals(findByUsername, account)
    }

    @Test
    fun `find an account by email after one master role delete`() {
        accountRepository.save(account)
        roleRepository.delete(role.name)

        val findByUsername: Account = accountRepository.accountFor(account.username).orElseThrow()

        println(findByUsername)
        assertEquals(findByUsername, account.copy(authorities = emptySet()))
    }

    @Test
    fun `find an account by email when an account does not exist`() {
        accountRepository.save(account)
        val findByUsername: Optional<Account> = accountRepository.accountFor("not-existing-user-name")

        val empty: Optional<Account> = Optional.empty()
        assertEquals(findByUsername, empty)
    }

    @Test
    fun `save an account by email`() {
        accountRepository.save(account)

        val findByUsername: Account = accountRepository.accountFor(account.username).orElseThrow()
        assertEquals(findByUsername, account)

        val accountUpdated = account.copy(firstName = "A_NEW_FIRSTNAME", lastName = "A_NEW_LASTNAME")
        accountRepository.save(accountUpdated)

        val updatedFindByUsername = accountRepository.accountFor(account.username).orElseThrow()
        assertEquals(updatedFindByUsername, accountUpdated)
    }


    @Test
    fun `when overrides authorities to an accounts`() {
        val anotherAccount = account.copy(
            authorities = setOf("another_role")
        )
        accountRepository.save(account)
        accountRepository.save(anotherAccount)

        assertEquals(accountRepository.accountFor(account.username), Optional.of(anotherAccount))
    }

    @Test
    internal fun `when a new account is created`() {
        accountRepository.create(account)

        assertEquals(accountRepository.accountFor(account.username), Optional.of(account))
    }

    @Test
    internal fun `when a new account is created then once`() {
        assertThrows(AccountRegistrationException::class.java) {
            accountRepository.create(account)
            accountRepository.create(account)
        }

    }
}