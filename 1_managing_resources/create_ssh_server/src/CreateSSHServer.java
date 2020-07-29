
// Copyright 2020 StrongDM Inc
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
import java.util.concurrent.TimeUnit;

import com.strongdm.api.v1.*;

public class CreateSSHServer {
    public static void main(String[] args) {
        // Load the SDM API keys from the environment.
        // If these values are not set in your environment,
        // please follow the documentation here:
        // https://www.strongdm.com/docs/admin-guide/api-credentials/
        var apiAccessKey = System.getenv("SDM_API_ACCESS_KEY");
        var apiSecretKey = System.getenv("SDM_API_SECRET_KEY");
        if (apiAccessKey == null || apiSecretKey == null) {
            System.out.println("SDM_API_ACCESS_KEY and SDM_API_SECRET_KEY must be provided");
            return;
        }

        try {
            // Create the SDM Client
            var opts = new ClientOptions();
            var client = new Client(apiAccessKey,apiSecretKey, opts);

            // Define an SSH server
            var server = new SSH();
            server.setName("Example SSH Server");
            server.setHostname("example.strongdm.com");
            server.setUsername("example");
            server.setPort(22);
            
            // Create the server
            var response = client.resources()
                .withDeadlineAfter(30, TimeUnit.SECONDS)
                .create(server);


            var serverResponse = (SSH)response.getResource();

            System.out.println("Successfully created SSH server.");
            System.out.printf("\tID: %s\n", serverResponse.getId());
            System.out.printf("\tName: %s\n", serverResponse.getName());
            System.out.printf("\tPublic Key: %s\n", serverResponse.getPublicKey());
        } catch (Exception e) {
            e.printStackTrace();
        }

        
    }
}