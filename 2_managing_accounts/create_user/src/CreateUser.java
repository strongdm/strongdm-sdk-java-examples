
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
import java.util.concurrent.TimeUnit;

import com.strongdm.api.Client;
import com.strongdm.api.ClientOptions;
import com.strongdm.api.PermissionLevel;
import com.strongdm.api.User;

public class CreateUser {
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

            // Define a User
            var user = new User();
            user.setEmail("java-create-user-example@example.com");
            user.setFirstName("example");
            user.setLastName("example");
            user.setPermissionLevel(PermissionLevel.TEAM_LEADER);
            
            // Create the User
            var response = client.accounts().create(user);
            var userResponse = (User)response.getAccount();


            System.out.println("Successfully created user.");
            System.out.printf("\tID: %s\n", userResponse.getId());
            System.out.printf("\tEmail: %s\n", userResponse.getEmail());
        } catch (Exception e) {
            e.printStackTrace();
        }

        
    }
}