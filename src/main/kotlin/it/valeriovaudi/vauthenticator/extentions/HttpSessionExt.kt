package it.valeriovaudi.vauthenticator.extentions

import org.springframework.security.web.savedrequest.DefaultSavedRequest
import java.util.*
import javax.servlet.http.HttpSession

fun HttpSession.oauth2ClientId(): Optional<String> {
    val attribute = Optional.ofNullable(this.getAttribute("SPRING_SECURITY_SAVED_REQUEST"))
    return attribute.flatMap {
        when (it) {
            is DefaultSavedRequest -> whenSessionHaveA(it)
            else -> Optional.empty()
        }

    }

}

private fun whenSessionHaveA(defaultSavedRequest: DefaultSavedRequest): Optional<String> {
    return if (defaultSavedRequest.parameterNames.contains("client_id")) {
        Optional.ofNullable(defaultSavedRequest.getParameterValues("client_id").firstOrNull())
    } else {
        Optional.empty()
    }

}