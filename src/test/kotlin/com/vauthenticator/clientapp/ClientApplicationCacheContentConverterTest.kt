package com.vauthenticator.clientapp

import com.fasterxml.jackson.databind.ObjectMapper
import com.vauthenticator.oauth2.clientapp.ClientApplicationCacheContentConverter
import it.valeriovaudi.vauthenticator.security.registeredclient.RegisteredClientRepositoryFixture.aClientApplication
import it.valeriovaudi.vauthenticator.support.JsonUtils.prettifyInOneLineJsonFrom
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ClientApplicationCacheContentConverterTest {
    val underTest = ClientApplicationCacheContentConverter(ObjectMapper())
    private val clientApplication = aClientApplication().get()
    private val testableResource = "clientapplications/clientApp.json"

    @Test
    fun `when a complete account from cache is loaded`() {
        val aCompleteAccountCacheContent = prettifyInOneLineJsonFrom(testableResource)
        val actual = underTest.getObjectFromCacheContentFor(aCompleteAccountCacheContent)
        val expected = clientApplication

        Assertions.assertEquals(expected, actual)
    }


    @Test
    fun `when a complete account is made ready for the cache`() {
        val actual = underTest.loadableContentIntoCacheFor(clientApplication)
        val expected = prettifyInOneLineJsonFrom(testableResource)

        Assertions.assertEquals(expected, actual)
    }

}