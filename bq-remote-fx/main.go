//Copyright 2024 Google LLC
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//https://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.

package bq_remotefx_decryptor

import (
	"context"
	"encoding/base64"
	"encoding/json"
	"github.com/GoogleCloudPlatform/functions-framework-go/functions"
	"github.com/tink-crypto/tink-go-gcpkms/v2/integration/gcpkms"
	"github.com/tink-crypto/tink-go/v2/aead"
	"github.com/tink-crypto/tink-go/v2/core/registry"
	"github.com/tink-crypto/tink-go/v2/keyset"
	"log"
	"net/http"
	"strings"
)

const (
	// AWS KMS, Google Cloud KMS and HashiCorp Vault are supported out of the box.
	keyURI = "gcp-kms://projects/blog-465608/locations/global/keyRings/blog/cryptoKeys/data-encrypt-decrypt"
)

func init() {
	functions.HTTP("AEADDecrypt", AEADDecrypt)
}

func AEADDecrypt(w http.ResponseWriter, r *http.Request) {
	var requestData struct {
		Calls [][]string `json:"calls"`
	}

	if err := json.NewDecoder(r.Body).Decode(&requestData); err != nil {
		http.Error(w, "Invalid JSON", http.StatusBadRequest)
		return
	}

	replies := make([]string, 0, len(requestData.Calls))

	for _, call := range requestData.Calls {
		if len(call) != 2 {
			http.Error(w, "Invalid num of args", http.StatusBadRequest)
			return
		}
		ctx := context.Background()

		// Generate a new keyset handle.
		gcpClient, err := gcpkms.NewClientWithOptions(ctx, keyURI)
		if err != nil {
			http.Error(w, "No KMS Client", http.StatusBadRequest)
			return
		}
		log.Println("GCP CLIENT CREATED WITH REFERENCE TO KMS KEY")
		registry.RegisterKMSClient(gcpClient)

		dek := aead.AES256GCMKeyTemplate()
		template, err := aead.CreateKMSEnvelopeAEADKeyTemplate(keyURI, dek)
		if err != nil {
			http.Error(w, "KEK init failed", http.StatusBadRequest)
			return
		}

		handle, err := keyset.NewHandle(template)
		if err != nil {
			http.Error(w, "Failed to create keyset handle", http.StatusBadRequest)
			return
		}
		a, err := aead.New(handle)
		if err != nil {
			http.Error(w, "Failed to create AEAD instance", http.StatusBadRequest)
			return
		}

		log.Printf("1ST ARG: %v\n", call[0])
		log.Printf("2ND ARG: %v\n", call[1])

		//1st arg is b64 encoded ciphertext
		ciphertextBytes, err := base64.StdEncoding.DecodeString(call[0])
		if err != nil {
			http.Error(w, "BASE64 DECODING OF ENCRYPTED VALUE FAILED", http.StatusBadRequest)
			return
		}
		//2nd arg is STRING, trim it
		trimmed := strings.TrimRight(call[1], " ")
		pt, err := a.Decrypt(ciphertextBytes, []byte(trimmed))
		if err != nil {
			http.Error(w, "Decryption failed", http.StatusBadRequest)
			return
		}
		replies = append(replies, string(pt))
	}

	responseData := map[string]interface{}{
		"replies": replies,
	}

	w.Header().Set("Content-Type", "application/json")
	if err := json.NewEncoder(w).Encode(responseData); err != nil {
		http.Error(w, "Failed to encode response", http.StatusInternalServerError)
		return
	}
}
