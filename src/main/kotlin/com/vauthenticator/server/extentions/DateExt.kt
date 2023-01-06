package com.vauthenticator.server.extentions

import com.vauthenticator.server.account.Date
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

fun Date.asDynamoAttribute() : AttributeValue = AttributeValue.builder().s(this.formattedDate()).build()