
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
            create.setName("Example Update Workflow");
            create.setDescription("Example Workflow Description");

            // Create the Workflow
            var createResponse = client.workflows().create(create);
            var workflow = createResponse.getWorkflow();
            System.out.println("Successfully created workflow.");
            System.out.printf("\tID: %s\n", workflow.getId());
            System.out.printf("\tName: %s\n", workflow.getName());

            // Get the Workflow
            var getResponse = client.workflows().get(workflow.getId());
            workflow = getResponse.getWorkflow();

	        // Update Workflow Name
            var newName = "Example New Name";
            workflow.setName(newName);
            workflow = client.workflows().update(workflow).getWorkflow();

            System.out.println("Successfully updated workflow name.");
            System.out.printf("\tID: %s\n", workflow.getId());
            System.out.printf("\tName: %s\n", workflow.getName());

	        // Update Workflow Description
            var newDescription = "Example New Description";
            workflow.setDescription(newDescription);
            workflow = client.workflows().update(workflow).getWorkflow();

            System.out.println("Successfully updated workflow description.");
            System.out.printf("\tID: %s\n", workflow.getId());
            System.out.printf("\tDescription: %s\n", workflow.getDescription());
            
	        // Update Workflow Weight
            var oldWeight = workflow.getWeight();
            workflow.setWeight(oldWeight + 20);
            workflow = client.workflows().update(workflow).getWorkflow();

            System.out.println("Successfully updated workflow weight.");
            System.out.printf("\tID: %s\n", workflow.getId());
            System.out.printf("\tWeight: %s\n", workflow.getWeight());
            
	        // Update Workflow AutoGrant
            var isAutoGrant = workflow.getAutoGrant();
            workflow.setAutoGrant(!isAutoGrant);
            workflow = client.workflows().update(workflow).getWorkflow();
            
            System.out.println("Successfully updated workflow auto grant.");
            System.out.printf("\tID: %s\n", workflow.getId());
            System.out.printf("\tAuto Grant: %s\n", workflow.getAutoGrant());
            
            // Update Workflow Enabled
            // The requirements to enable a workflow are that the workflow must be either set
            // up with auto grant enabled or have one or more WorkflowApprovers created for
            // the workflow.
            workflow.setAutoGrant(true);
            workflow.setEnabled(true);
            workflow = client.workflows().update(workflow).getWorkflow();

            System.out.println("Successfully updated workflow auto grant.");
            System.out.printf("\tID: %s\n", workflow.getId());
            System.out.printf("\tEnabled: %s\n", workflow.getEnabled());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}