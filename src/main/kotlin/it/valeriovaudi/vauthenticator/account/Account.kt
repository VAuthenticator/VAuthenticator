package it.valeriovaudi.vauthenticator.account

data class Account(var accountNonExpired: Boolean = false,
                   var accountNonLocked: Boolean = false,
                   var credentialsNonExpired: Boolean = false,
                   var enabled: Boolean = true,

                   var username: String,
                   var password: String,
                   var authorities: List<String>,

                   var sub: String,

        // needed for email oidc profile
                   var email: String,
        //todo should be changed when account
                   var emailVerified: Boolean = true,

        // needed for profile oidc profile

                   var firstName: String,
                   var lastName: String
)