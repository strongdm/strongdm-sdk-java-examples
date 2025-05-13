
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

import com.strongdm.api.Client;
import com.strongdm.api.ClientOptions;
import com.strongdm.api.PermissionLevel;
import com.strongdm.api.User;

public class AssignManagerToUsers {
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

            // Create second user and assign manager
            User user2 = new User();
            user2.setEmail("java-create-user-example2@example.com");
            user2.setFirstName("example2");
            user2.setLastName("example2");
            user2.setManagerId(userResponse.getId());

            var createResp2 = client.accounts().create(user2);
            var createdUser2 = (User) createResp2.getAccount();

            System.out.println("Successfully created user with manager assignment.");
            System.out.printf("\tID: %s%n", createdUser2.getId());
            System.out.printf("\tManagerID: %s%n", createdUser2.getManagerId());

            // Get Resolved Manager ID of user and SCIM metadata if present.
            // Resolved Manager ID will be set to the manager ID of the user, if present,
            // and will be resolved from manager information from SCIM metadata otherwise.
            // If no manager information can be resolved from SCIM metadata and manager ID is not set, 
            // Resolved Manager ID will have no value.
            var getResp = client.accounts().get(createdUser2.getId());
            var fetchedUser = (User) getResp.getAccount();

            System.out.println("Successfully fetched user.");
            System.out.printf("\tID: %s%n", fetchedUser.getId());
            System.out.printf("\tManagerID: %s%n", fetchedUser.getManagerId());
            System.out.printf("\tResolvedManagerID: %s%n", fetchedUser.getResolvedManagerId());
            System.out.printf("\tSCIM Metadata: %s%n", fetchedUser.getSCIM());

            // Clear manager assignment and update user
            fetchedUser.setManagerId("");
            var updateResp = client.accounts().update(fetchedUser);
            User updatedUser = (User) updateResp.getAccount();

            System.out.println("Successfully updated user.");
            System.out.printf("\tID: %s%n", updatedUser.getId());
            System.out.printf("\tManagerID: %s%n", updatedUser.getManagerId());
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }
}