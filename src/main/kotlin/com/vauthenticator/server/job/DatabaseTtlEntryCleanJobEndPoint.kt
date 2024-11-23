package com.vauthenticator.server.job

import org.springframework.boot.actuate.endpoint.annotation.Endpoint
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation
import org.springframework.http.ResponseEntity

@Endpoint(id = "database-clean-up")
class DatabaseTtlEntryCleanJobEndPoint(
    private val databaseTtlEntryCleanJob: DatabaseTtlEntryCleanJob
) {

    @WriteOperation
    fun cleanUp(): ResponseEntity<Unit> {
        databaseTtlEntryCleanJob.execute()
        return ResponseEntity.noContent().build()
    }
}
