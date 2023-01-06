package com.vauthenticator.config

import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import com.vauthenticator.account.repository.AccountRepository
import com.vauthenticator.keys.KeyDecrypter
import com.vauthenticator.keys.KeyRepository
import com.vauthenticator.keys.KeysJWKSource
import com.vauthenticator.oauth2.authorizationservice.RedisOAuth2AuthorizationService
import com.vauthenticator.oauth2.clientapp.ClientApplicationRepository
import com.vauthenticator.oauth2.clientapp.Scope.Companion.AVAILABLE_SCOPES
import com.vauthenticator.oauth2.clientapp.Scope.Companion.OPEN_ID
import com.vauthenticator.oauth2.clientapp.StoreClientApplication
import com.vauthenticator.oauth2.registeredclient.ClientAppRegisteredClientRepository
import com.vauthenticator.oauth2.token.OAuth2TokenEnhancer
import com.vauthenticator.oidc.sessionmanagement.SessionManagementFactory
import com.vauthenticator.oidc.sessionmanagement.sendAuthorizationResponse
import com.vauthenticator.oidc.token.IdTokenEnhancer
import com.vauthenticator.oidc.userinfo.UserInfoEnhancer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer
import org.springframework.security.web.DefaultRedirectStrategy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint

@Configuration(proxyBeanMethods = false)
class AuthorizationServerConfig {

    @Value("\${auth.oidcIss:}")
    lateinit var oidcIss: String

    @Autowired
    lateinit var accountRepository: AccountRepository

    @Bean
    fun jwkSource(keyRepository: KeyRepository, keyDecrypter: KeyDecrypter): JWKSource<SecurityContext?> =
        KeysJWKSource(keyDecrypter, keyRepository)

    @Bean
    fun nimbusJwsEncoder(jwkSource: JWKSource<SecurityContext?>?): JwtEncoder {
        return NimbusJwtEncoder(jwkSource)
    }

    @Bean
    fun jwtCustomizer(clientApplicationRepository: ClientApplicationRepository): OAuth2TokenCustomizer<JwtEncodingContext> {
        return OAuth2TokenCustomizer { context: JwtEncodingContext ->
            OAuth2TokenEnhancer(clientApplicationRepository).customize(context)
            IdTokenEnhancer().customize(context)
        }
    }

    @Bean
    fun registeredClientRepository(
        storeClientApplication: StoreClientApplication,
        clientRepository: ClientApplicationRepository
    ): RegisteredClientRepository {
        return ClientAppRegisteredClientRepository(storeClientApplication, clientRepository)
    }

    @Bean
    fun oAuth2AuthorizationService(redisTemplate: RedisTemplate<Any, Any>): OAuth2AuthorizationService {
        return RedisOAuth2AuthorizationService(redisTemplate)
    }

    @Bean
    fun providerSettings(): AuthorizationServerSettings {
        return AuthorizationServerSettings.builder().issuer(oidcIss).build()
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    fun authorizationServerSecurityFilterChain(
        providerSettings: AuthorizationServerSettings,
        redisTemplate: RedisTemplate<String, String?>,
        http: HttpSecurity
    ): SecurityFilterChain {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http)
        http.csrf().disable().headers().frameOptions().disable()

        val userInfoEnhancer = UserInfoEnhancer(accountRepository)

        val authorizationServerConfigurer = http.getConfigurer(OAuth2AuthorizationServerConfigurer::class.java)

        authorizationServerConfigurer.oidc { configurer ->
            configurer.userInfoEndpoint { customizer ->
                customizer.userInfoMapper { context ->
                    userInfoEnhancer.oidcUserInfoFrom(context)
                }
            }.providerConfigurationEndpoint { customizer ->
                customizer.providerConfigurationCustomizer { providerConfiguration ->
                    AVAILABLE_SCOPES
                        .filter { it != OPEN_ID }
                        .forEach { providerConfiguration.scope(it.content) }
                }
            }
        }.authorizationEndpoint {
            it.authorizationResponseHandler(
                sendAuthorizationResponse(
                    redisTemplate,
                    SessionManagementFactory(providerSettings),
                    DefaultRedirectStrategy()
                )
            )
        }
        http.exceptionHandling { it.authenticationEntryPoint(LoginUrlAuthenticationEntryPoint("/login")) }
            .oauth2ResourceServer().jwt()

        return http.build()
    }


    @Bean
    fun jwtDecoder(jwkSource: JWKSource<SecurityContext?>?): JwtDecoder {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource)
    }

}
