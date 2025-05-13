
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

public class SetPassword {
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
            var client = new Client(apiAccessKey, apiSecretKey, opts);

            // Create an account
            // Setting a password when creating an account is not supported
            var create = new User();
            create.setEmail("java-set-password-example@example.com");
            create.setFirstName("example");
            create.setLastName("example");
            create.setPermissionLevel(PermissionLevel.USER);

            var response = client.accounts().create(create);
            var user = (User) response.getAccount();
            System.out.println("Successfully created user.");
            System.out.printf("\tID: %s\n", user.getId());
            System.out.printf("\tEmail: %s\n", user.getEmail());

            // Password is a write-only field
            // The current password is never returned in any responses
            assert user.getPassword().equals("");

            // Get the account
            var getResponse = client.accounts().get(user.getId());
            user = (User) getResponse.getAccount();
            assert user.getPassword().equals("");

            // Set new password according to organization password complexity requirements
            var newPassword = "correct horse battery staple";
            user.setPassword(newPassword);

            // Update the account
            var updateResponse = client.accounts().update(user);
            user = (User) updateResponse.getAccount();
            assert user.getPassword().equals("");
            System.out.println("Successfully updated password.");
            System.out.printf("\tID: %s\n", user.getId());
            System.out.printf("\tNew password: %s\n", newPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}