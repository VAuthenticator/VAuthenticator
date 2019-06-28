package it.valeriovaudi.vauthenticator.openidconnect.idtoken

import it.valeriovaudi.vauthenticator.keypair.KeyPairFixture.getFileContent
import it.valeriovaudi.vauthenticator.keypair.KeyPairFixture.keyPair
import it.valeriovaudi.vauthenticator.keypair.KeyRepository
import it.valeriovaudi.vauthenticator.time.Clock
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.times
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.security.oauth2.common.OAuth2AccessToken

@RunWith(MockitoJUnitRunner::class)
class IdTokenEnhancerTest {

    @Mock
    lateinit var keyRepository: KeyRepository

    @Mock
    lateinit var clock: Clock

    @Test
    fun `when client application has openid as scope`() {
        val idTokenEnhancer = IdTokenEnhancer("AN_ISS", keyRepository, clock)

        val content = getFileContent("/keystore/keystore.jks")
        given(keyRepository.getKeyPair())
                .willReturn(keyPair(content = content))

        val actual: OAuth2AccessToken = idTokenEnhancer.enhance(TestableDefaultOAuth2AccessToken(clientAppScope = setOf("openid")), TestableOAuth2Authentication())

        assertNotNull(actual.additionalInformation["id_token"])

        verify(clock).nowInSeconds()
        verify(keyRepository).getKeyPair()
    }

    @Test
    fun `when client application does not has openid as scope`() {
        val idTokenEnhancer = IdTokenEnhancer("AN_ISS", keyRepository, clock)

        val actual: OAuth2AccessToken = idTokenEnhancer.enhance(TestableDefaultOAuth2AccessToken(), TestableOAuth2Authentication())

        assertNull(actual.additionalInformation["id_token"])

        verify(clock, times(0)).nowInSeconds()
        verify(keyRepository, times(0)).getKeyPair()
    }
}