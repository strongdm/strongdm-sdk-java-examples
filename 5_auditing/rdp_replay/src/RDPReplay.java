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
import java.nio.file.*;
import java.util.*;
import java.io.*;
import org.json.JSONObject;
import org.json.JSONArray;

public class RDPReplay {
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

            // You'll need an RDP resource that has had queries made against it, provide its name:
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
                } else if ("rdp".equals(q.getResourceType()) && !q.getDuration().isZero()) {
                    System.out.printf(
                            "Found RDP query made by %s at %s\n",
                            user.getEmail(), q.getTimestamp());
                    Iterable<ReplayChunk> replayChunks = client.replays().list("id:?", q.getId());

                    Path tempDir = Files.createTempDirectory(q.getId());
                    List<String> logFiles = new ArrayList<>();

                    // Write the postStart query as a node log file (https://www.strongdm.com/docs/admin/logs/references/post-start/)
                    JSONObject queryJson = new JSONObject();
                    queryJson.put("type", "postStart");
                    queryJson.put("uuid", q.getId());
                    queryJson.put("query", JSONObject.valueToString(new JSONObject(q.getQueryBody())));
                    
                    Path queryFile = tempDir.resolve("relay.0000000000.log");
                    Files.writeString(queryFile, queryJson.toString());
                    logFiles.add(queryFile.toString());

                    // Write each replay chunk as a node log file (https://www.strongdm.com/docs/admin/logs/references/replay-chunks/)
                    int chunkId = 1;
                    for (ReplayChunk chunk : replayChunks) {
                        JSONArray eventsArray = new JSONArray();
                        for (ReplayChunkEvent event : chunk.getEvents()) {
                            JSONObject eventJson = new JSONObject();
                            eventJson.put("data", Base64.getEncoder().encodeToString(event.getData()));
                            eventJson.put("duration", event.getDuration().getSeconds());
                            eventsArray.put(eventJson);
                        }

                        JSONObject chunkJson = new JSONObject();
                        chunkJson.put("type", "chunk");
                        chunkJson.put("uuid", q.getId());
                        chunkJson.put("chunkId", chunkId);
                        chunkJson.put("events", eventsArray);

                        Path chunkfile = tempDir.resolve(String.format("relay.%010d.log", chunkId));
                        Files.writeString(chunkfile, chunkJson.toString());
                        logFiles.add(chunkfile.toString());
                        chunkId++;
                    }

                    // Run the sdm CLI to render the captured data (make sure path includes sdm)
                    List<String> command = new ArrayList<>();
                    command.add("sdm");
                    command.add("replay");
                    command.add("rdp");
                    command.add(q.getId());
                    command.addAll(logFiles);

                    ProcessBuilder pb = new ProcessBuilder(command);
                    pb.redirectErrorStream(true);
                    Process process = pb.start();

                    // This will print out the location of the rendered session
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (line.startsWith("render complete:")) {
                                System.out.println(line);
                            }
                        }
                    }

                    int exitCode = process.waitFor();
                    if (exitCode != 0) {
                        throw new RuntimeException("sdm replay command failed with exit code " + exitCode);
                    }

                    // Clean up tempDir
                    Files.walk(tempDir)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
