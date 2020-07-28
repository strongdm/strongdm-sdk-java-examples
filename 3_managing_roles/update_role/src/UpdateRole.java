
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

public class UpdateRole {
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
            var client = new Client(apiAccessKey, apiSecretKey, opts);

            // Define a role
            var create = new Role();
            create.setName("Example Role");

            // Create the role
            var createResponse = client.roles().create(create);
            var role = createResponse.getRole();
            System.out.println("Successfully created role.");
            System.out.printf("    ID: %s\n", role.getId());
            System.out.printf("  Name: %s\n", role.getName());

            // Get the role
            var getResponse = client.roles().get(role.getId());
            role = getResponse.getRole();

            // Set fields
            role.setName("Example Role Updated");

            // Update the role
            var updateResponse = client.roles().update(role);
            role = updateResponse.getRole();
            System.out.println("Successfully updated role.");
            System.out.printf("    ID: %s\n", role.getId());
            System.out.printf("  Name: %s\n", role.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}