
// Copyright 2023 StrongDM Inc
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
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.strongdm.api.v1.*;

public class DeleteWorkflowApprover {
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

            // Create an access rule for the workflow
            var accessRule = new AccessRule();
            Map<String, String> tags = new Hashmap<>();
            tags.put("env", "dev");
            accessRule.setTags(tags);

            // Create a Workflow
            var workflow = new Workflow();
            workflow.setName("Example Delete Worfklow Approver");
            workflow.setDescription("Example Workflow Description");
            workflow.setAccessRule(accessRule);
            workflow = client.workflows().create(workflow).getWorkflow();

	        // Create an approver - used for creating a workflow approver
            var user = new User();
            user.setEmail("delete-workflow-approver-example@example.com");
            user.setFirstName("example");
            user.setLastName("example");
            user = client.accounts().create(user).getAccount();

            // Create a workflow approver
            var workflowApprover = new WorkflowApprover();
            workflowApprover.setWorkflowId(workflow.getId());
            workflowApprover.setApproverId(user.getId());
            workflowApprover = client.workflowApprovers().create().getWorkflowApprover();

            // Delete a Workflow Approver
            client.workflowApprovers().delete(workflowApprover.getId());
            
            System.out.println("Successfully deleted workflow approver.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}