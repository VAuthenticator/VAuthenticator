package it.valeriovaudi.vauthenticator.account

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@DataMongoTest
@RunWith(SpringRunner::class)
class MongoAccountRepositoryTest {

    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    private val sub = UUID.randomUUID().toString()
    private val account = Account(
            username = "email@domail.com",
            password = "secret",
            authorities = emptyList(),
            sub = sub,
            email = "email@domail.com",
            firstName = "A First Name",
            lastName = "A Last Name"
    )
    lateinit var mongoAccountRepository: MongoAccountRepository

    @Before
    fun setUp() {
        mongoAccountRepository = MongoAccountRepository(mongoTemplate)
    }

    @Test
    fun `find an account by email`() {
        mongoAccountRepository.save(account)

        val findByUsername: Account = mongoAccountRepository.accountFor(account.username).orElseThrow()
        assertThat(findByUsername, equalTo(account))
    }


}