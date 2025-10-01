package com.example;


import com.google.crypto.tink.Aead;
import com.google.crypto.tink.KmsClient;
import com.google.crypto.tink.aead.AeadConfig;
import com.google.crypto.tink.aead.AeadKeyTemplates;
import com.google.crypto.tink.aead.KmsEnvelopeAead;
import com.google.crypto.tink.integration.gcpkms.GcpKmsClient;
import java.security.GeneralSecurityException;

public class CloudKmsEnvelopeAead {

    public static Aead get(String kmsUri) throws GeneralSecurityException {
        AeadConfig.register();

        // Create a new KMS Client
        KmsClient client = new GcpKmsClient().withDefaultCredentials();

        // Create an AEAD primitive using the Cloud KMS key
        Aead gcpAead = client.getAead(kmsUri);

        // Create an envelope AEAD primitive.
        // This key should only be used for client-side encryption to ensure authenticity and integrity
        // of data.
        return new KmsEnvelopeAead(AeadKeyTemplates.AES256_GCM, gcpAead);
    }
}