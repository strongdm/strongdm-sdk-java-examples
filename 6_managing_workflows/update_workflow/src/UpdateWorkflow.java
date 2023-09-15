
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

public class UpdateWorkflow {
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

            // Define a Workflow
            var create = new Workflow();
            create.setName("Example Workflow");

            // Create the Workflow
            var createResponse = client.workflows().create(create);
            var workflow = createResponse.getWorkflow();
            System.out.println("Successfully created workflow.");
            System.out.printf("\tID: %s\n", workflow.getId());
            System.out.printf("\tName: %s\n", workflow.getName());

            // Get the Workflow
            var getResponse = client.workflows().get(workflow.getId());
            workflow = getResponse.getWorkflow();

            // Set fields
            workflow.setName("Example Workflow Updated");

            // Update the Workflow
            var updateResponse = client.workflows().update(workflow);
            workflow = updateResponse.getWorkflow();
            System.out.println("Successfully updated workflow.");
            System.out.printf("\tID: %s\n", workflow.getId());
            System.out.printf("\tName: %s\n", workflow.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}