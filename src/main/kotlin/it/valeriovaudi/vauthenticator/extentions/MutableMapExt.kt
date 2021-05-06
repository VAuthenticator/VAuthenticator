package it.valeriovaudi.vauthenticator.extentions

import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.util.*

fun MutableMap<String, AttributeValue>.valueAsStringFor(key: String): String =
    this[key]?.s()!!

fun MutableMap<String, AttributeValue>.valuesAsStringFor(key: String): List<String> =
    this[key]?.ss()!!

fun MutableMap<String, AttributeValue>.valueAsBoolFor(key: String): Boolean =
    this[key]?.bool()!!

fun MutableMap<String, AttributeValue>.valueAsIntFor(key: String): Int =
    this[key]?.n()!!.toInt()


fun MutableMap<String, AttributeValue>.filterEmptyAccountMetadata() =
    if (this.isEmpty()) {
        Optional.empty()
    } else {
        Optional.of(this)
    }