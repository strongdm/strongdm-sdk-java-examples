
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
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.time.Duration;

import com.strongdm.api.Client;
import com.strongdm.api.ClientOptions;
import com.strongdm.api.Permission;
import com.strongdm.api.Token;

public class CreateToken {
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
            var opts = new ClientOptions();
            var client = new Client(apiAccessKey,apiSecretKey, opts);

            // Define a Token
            var token = new Token();
            token.setName("java-test-create-token");
            token.setDuration(Duration.ofHours(1));
            ArrayList<String> permissions = new ArrayList<String>();
            permissions.add(Permission.ROLE_LIST);
            token.setPermissions(permissions);
            token.setAccountType("api");
            
            // Create the Token
            var response = client.accounts().create(token);
            var tokenResponse = (Token)response.getAccount();


            System.out.println("Successfully created token.");
            System.out.printf("\tID: %s\n", tokenResponse.getId());
            System.out.printf("\tName: %s\n", tokenResponse.getName());
            System.out.printf("\tAccess Key: %s\n", response.getAccessKey());
            System.out.printf("\tSecret Key: %s\n", response.getSecretKey());
        } catch (Exception e) {
            e.printStackTrace();
        }

        
    }
}