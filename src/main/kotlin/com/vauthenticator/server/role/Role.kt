package com.vauthenticator.server.role

data class Role(val name: String, val description: String)

class DefaultRoleDeleteException(message: String) : RuntimeException(message)
