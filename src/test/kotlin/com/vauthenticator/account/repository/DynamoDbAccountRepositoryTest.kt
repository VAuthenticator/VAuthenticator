package com.vauthenticator.account.repository

import com.vauthenticator.account.Account
import com.vauthenticator.role.Role
import com.vauthenticator.support.DatabaseUtils.dynamoAccountRoleTableName
import com.vauthenticator.support.DatabaseUtils.dynamoAccountTableName
import com.vauthenticator.support.DatabaseUtils.dynamoDbClient
import com.vauthenticator.support.DatabaseUtils.resetDatabase
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

internal class DynamoDbAccountRepositoryTest {

    private val account = com.vauthenticator.account.AccountTestFixture.anAccount(listOf(Role("role", "description")))
    private lateinit var accountRepository: DynamoDbAccountRepository

    @BeforeEach
    fun setUp() {
        accountRepository = DynamoDbAccountRepository(
                dynamoDbClient,
                dynamoAccountTableName,
                dynamoAccountRoleTableName
        )
    }

    @AfterEach
    fun tearDown() {
        resetDatabase()
    }


    @Test
    fun `find an account by email`() {
        accountRepository.save(account)
        val findByUsername: Account = accountRepository.accountFor(account.username).orElseThrow()

        assertEquals(findByUsername, account)
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
    fun `find all accounts`() {
        val anAccount = account.copy()
        val anotherAccount = account.copy(
                email = "anotheremail@domain.com",
                username = "anotheremail@domain.com",
                firstName = "A_NEW_FIRSTNAME",
                lastName = "A_NEW_LASTNAME"
        )
        accountRepository.save(anAccount)
        accountRepository.save(anotherAccount)

        val findAll = accountRepository.findAll(true)
        assertTrue(findAll.contains(anAccount))
        assertTrue(findAll.contains(anotherAccount))
    }

    @Test
    fun `find all accounts without autorities`() {
        val anAccount = account.copy()
        val anotherAccount = account.copy(
                email = "anotheremail@domain.com",
                username = "anotheremail@domain.com",
                firstName = "A_NEW_FIRSTNAME",
                lastName = "A_NEW_LASTNAME"
        )
        accountRepository.save(anAccount)
        accountRepository.save(anotherAccount)

        val findAll = accountRepository.findAll()
        assertTrue(findAll.contains(anAccount))
        assertTrue(findAll.contains(anotherAccount))
    }

    @Test
    fun `when overrides authorities to an accounts`() {
        val anAccount = account.copy()
        val anotherAccount = account.copy(
                authorities = listOf("A_ROLE")
        )
        accountRepository.save(anAccount)
        accountRepository.save(anotherAccount)

        assertEquals(accountRepository.findAll(), listOf(anotherAccount))
    }

    @Test
    internal fun `when a new account is created`() {
        val anAccount = account.copy()
        accountRepository.create(anAccount)

        assertEquals(accountRepository.findAll(), listOf(anAccount))
    }

    @Test
    internal fun `when a new account is created then once`() {
        val anAccount = account.copy()

        assertThrows(AccountRegistrationException::class.java) {
            accountRepository.create(anAccount)
            accountRepository.create(anAccount)
        }

    }
}