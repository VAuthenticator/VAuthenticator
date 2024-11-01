package com.vauthenticator.server.keys.adapter.local

import com.vauthenticator.server.extentions.decoder
import com.vauthenticator.server.keys.domain.MasterKid
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.Security
import java.security.spec.RSAKeyGenParameterSpec
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec


class KeyCryptographicOperations(
    private val repository: BouncyCastleKeyGeneratorMasterKeyRepository,
) {
    companion object {
        init {
            Security.addProvider(BouncyCastleProvider());
        }
    }

    fun generateRSAKeyPair(): KeyPair {
        val keyPair: KeyPair
        try {
            val keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC")
            keyPairGenerator.initialize(RSAKeyGenParameterSpec(2048, RSAKeyGenParameterSpec.F4))
            keyPair = keyPairGenerator.generateKeyPair()
        } catch (ex: Exception) {
            throw IllegalStateException(ex)
        }
        return keyPair
    }

    fun encryptKeyWith(masterKid: MasterKid, encodedPlainText: ByteArray): ByteArray {
        val masterKey = repository.maskerKeyFor(masterKid);
        val key = SecretKeySpec(masterKey.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        return cipher.doFinal(decoder.decode(encodedPlainText))
    }

    fun decryptKeyWith(masterKid: MasterKid, encodedEncryptedText: ByteArray): ByteArray {
        val masterKey = repository.maskerKeyFor(masterKid);
        val key = SecretKeySpec(masterKey.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, key)
        return cipher.doFinal(decoder.decode(encodedEncryptedText))
    }


}