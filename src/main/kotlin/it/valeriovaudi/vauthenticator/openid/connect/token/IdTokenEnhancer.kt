package it.valeriovaudi.vauthenticator.openid.connect.token

import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.server.authorization.JwtEncodingContext
import org.springframework.security.oauth2.server.authorization.OAuth2TokenCustomizer

class IdTokenEnhancer : OAuth2TokenCustomizer<JwtEncodingContext> {
    override fun customize(context: JwtEncodingContext) {
        val tokenType = context.tokenType.value

        if ("id_token" == tokenType && !context.authorizationGrantType.equals(AuthorizationGrantType.CLIENT_CREDENTIALS)) {
            val attributes = context.authorization!!.attributes
            val principle = attributes["java.security.Principal"] as Authentication
            context.claims.claim("email", principle.name)
        }
    }
}