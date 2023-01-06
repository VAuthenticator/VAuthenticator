package com.vauthenticator.keys

import com.vauthenticator.extentions.decoder
import com.vauthenticator.extentions.encoder
import software.amazon.awssdk.core.SdkBytes.fromByteArray
import software.amazon.awssdk.services.kms.KmsClient
import software.amazon.awssdk.services.kms.model.DecryptRequest

interface KeyDecrypter {
    fun decryptKey(encrypted: String): String
}

class KmsKeyDecrypter(private val kmsClient: KmsClient) : KeyDecrypter {
    override fun decryptKey(privateKey: String): String = kmsClient.decrypt(
        DecryptRequest.builder()
            .ciphertextBlob(fromByteArray(decoder.decode(privateKey)))
            .build()
    ).let {
        encoder.encode(it.plaintext().asByteArray()).decodeToString()
    }
}