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
import org.json.JSONObject;

public class SSHReplay {
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
        try {
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
                    System.out.printf(
                            "Skipping encrypted query made by %s at %s\n",
                            user.getEmail(), q.getTimestamp());
                    System.out.println(
                            "See encrypted_query_replay for an example of query decryption.");
                } else if (q.getReplayable()) {
                    System.out.printf(
                            "Replaying query made by %s at %s\n",
                            user.getEmail(), q.getTimestamp());
                    Iterable<ReplayChunk> replayChunks = client.replays().list("id:?", q.getId());
                    for (ReplayChunk chunk : replayChunks) {
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
}
