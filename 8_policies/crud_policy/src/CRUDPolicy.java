// Copyright 2024 StrongDM Inc
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

public class CRUDPolicy {
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

            var policy = new Policy();
            policy.setName("forbid-everything");
            policy.setDescription("Forbid everything");
            policy.setPolicy("forbid ( principal, action, resource );");

            var createResp = client.policies().create(policy);
            System.out.println("Successfully created a policy to forbid all actions.");
            System.out.printf("\tID: %s\n", createResp.getPolicy().getId());
            System.out.printf("\tName: %s\n", createResp.getPolicy().getName());

            // Note: The `policy` we from `createResp` can also be used to
            // make an update. However, we'll load it from the API to
            // demonstrate `get`.

            var getResp = client.policies().get(createResp.getPolicy().getId());

            var updatePolicy = getResp.getPolicy();
            updatePolicy.setName("forbid-one-thing");
            updatePolicy.setDescription("forbid connecting to the bad resource");
            updatePolicy.setPolicy("forbid ( principal, action == StrongDM::Action::\"connect\", resource == StrongDM::Resource::\"rs-123d456789\");");

            // Execute the update.
            var updateResp = client.policies().update(updatePolicy);
            System.out.println("Successfully updated policy.");
            System.out.printf("\tID: %s\n", updateResp.getPolicy().getId());
            System.out.printf("\tName: %s\n", updateResp.getPolicy().getName());
            System.out.printf("\tDescription: %s\n", updateResp.getPolicy().getDescription());
            System.out.printf("\tPolicy: %s\n", updateResp.getPolicy().getPolicy());

            // We'll now delete the policy
            client.policies().delete(createResp.getPolicy().getId());

            // And then try to retrieve our newly deleted policy, expecting
            // a NotFoundException...
            try {
                client.policies().get(createResp.getPolicy().getId());
            } catch (NotFoundException e) {
                // We're expecting this.
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
