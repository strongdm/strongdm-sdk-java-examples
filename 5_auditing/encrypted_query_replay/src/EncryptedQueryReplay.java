// Copyright 2025 StrongDM Inc
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
import com.strongdm.api.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Duration;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;
import org.json.JSONArray;
import org.json.JSONObject;

public class EncryptedQueryReplay {
    public static void main(String[] args) {
        // Load the SDM API keys from the environment.
        // If these values are not set in your environment,
        // please follow the documentation here:
        // https://www.strongdm.com/docs/api/api-keys/
        var apiAccessKey = System.getenv("SDM_API_ACCESS_KEY");
        var apiSecretKey = System.getenv("SDM_API_SECRET_KEY");
        if (apiAccessKey == null || apiSecretKey == null) {
            System.out.println("SDM_API_ACCESS_KEY and SDM_API_SECRET_KEY must be provided");
            return;
        }

        // Load the private key for query and replay decryption.
        // This environment variable should contain the path to the private
        // encryption key configured for StrongDM remote log encryption.
        var privateKeyFile = System.getenv("SDM_LOG_PRIVATE_KEY_FILE");
        if (privateKeyFile == null) {
            System.out.println("SDM_LOG_PRIVATE_KEY_FILE must be provided for this example");
            return;
        }

        try {
            PrivateKey privateKey = loadPrivateKeyFromFile(privateKeyFile);

            // Create the SDM Client
            ClientOptions opts = new ClientOptions();
            Client client = new Client(apiAccessKey, apiSecretKey, opts);

            // You'll need an SSH resource that has had queries made against it, provide its name:
            String resourceName = "Example";
            Iterable<Resource> resourceIterator = client.resources().list("name:?", resourceName);
            Resource resource = null;
            for (Resource r : resourceIterator) {
                resource = r;
            }
            if (resource == null) {
                throw (new Exception("Couldn't find resource named " + resourceName));
            }
            System.out.printf("Queries made against %s:\n", resourceName);

            Iterable<Query> queryIterator =
                    client.queries().list("resource_id:?", resource.getId());
            for (Query q : queryIterator) {
                AccountGetResponse accountResponse =
                        client.snapshotAt(q.getTimestamp()).accounts().get(q.getAccountId());
                User user = (User) accountResponse.getAccount();

                if (q.getEncrypted()) {
                    System.out.println("Decrypting encrypted query");
                    q.setQueryBody(
                            decryptQueryData(
                                    privateKey,
                                    q.getQueryKey(),
                                    Base64.getDecoder().decode(q.getQueryBody())));
                    q.setReplayable(
                            new JSONObject(q.getQueryBody()).optString("type").equals("shell"));
                }

                if (q.getReplayable()) {
                    System.out.printf(
                            "Replaying query made by %s at %s\n",
                            user.getEmail(), q.getTimestamp());
                    Iterable<ReplayChunk> replayChunks = client.replays().list("id:?", q.getId());
                    for (ReplayChunk chunk : replayChunks) {
                        if (q.getEncrypted()) {
                            JSONArray events =
                                    new JSONArray(
                                            decryptQueryData(
                                                    privateKey, q.getQueryKey(), chunk.getData()));
                            for (int i = 0; i < events.length(); i++) {
                                JSONObject e = events.getJSONObject(i);
                                ReplayChunkEvent event = new ReplayChunkEvent();
                                event.setData(Base64.getDecoder().decode(e.getString("data")));
                                event.setDuration(Duration.ofMillis(e.getLong("duration")));
                                chunk.getEvents().add(event);
                            }
                        }

                        for (ReplayChunkEvent ev : chunk.getEvents()) {
                            // Some characters may not be printed cleanly by this method
                            String data = new String(ev.getData(), StandardCharsets.UTF_8);
                            System.out.print(data);
                            Thread.sleep(ev.getDuration().toMillis());
                        }
                        System.out.println();
                    }
                } else {
                    String command = new JSONObject(q.getQueryBody()).optString("command");
                    System.out.printf(
                            "Command run by %s at %s: %s\n",
                            user.getEmail(), q.getTimestamp(), command);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static PrivateKey loadPrivateKeyFromFile(String privateKeyFile) throws Exception {
        // This code assumes the private file is in PEM-encoded PKCS#8 format
        // (beginning with "BEGIN PRIVATE KEY").
        // To convert a key file from the more common PKCS#1 format (beginning
        // with "BEGIN RSA PRIVATE KEY") the following command can be used:
        //   openssl pkcs8 -topk8 -inform PEM -outform PEM -in key-pkcs1.pem -out key-pkcs8.pem
        // -nocrypt
        byte[] privateKeyBytes =
                Base64.getDecoder()
                        .decode(
                                Files.readString(Path.of(privateKeyFile))
                                        .replaceAll("-----(BEGIN|END) PRIVATE KEY-----", "")
                                        .replaceAll(System.lineSeparator(), ""));
        return KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
    }

    // This method demonstrates how to decrypt encrypted query/replay data.
    private static String decryptQueryData(
            PrivateKey privateKey, String encryptedQueryKey, byte[] encryptedData)
            throws Exception {
        // Use the organization's private key to decrypt the symmetric key
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(
                Cipher.DECRYPT_MODE,
                privateKey,
                new OAEPParameterSpec(
                        "SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT));
        byte[] symmetricKeyBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedQueryKey));
        SecretKey symmetricKey =
                new SecretKeySpec(symmetricKeyBytes, 0, symmetricKeyBytes.length, "AES");

        // Use the symmetric key to decrypt the data
        cipher = Cipher.getInstance("AES/CBC/NoPadding");
        byte[] iv = Arrays.copyOfRange(encryptedData, 0, cipher.getBlockSize());
        byte[] ciphertext =
                Arrays.copyOfRange(encryptedData, cipher.getBlockSize(), encryptedData.length);
        cipher.init(Cipher.DECRYPT_MODE, symmetricKey, new IvParameterSpec(iv));

        byte[] plaintext = cipher.doFinal(ciphertext);
        return new String(plaintext).replaceAll("\u0000+$", "");
    }
}
