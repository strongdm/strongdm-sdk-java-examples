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
package samples;

import java.util.Map;

import javax.management.relation.Role;

import com.strongdm.api.v1.*;

public class CreateWorkflowRole {
    public static void main(String[] args) throws Exception {
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
            workflow.setName("Example Create Worfklow Approver");
            workflow.setDescription("Example Workflow Description");
            workflow.setAccessRule(accessRule);
            workflow = client.workflows().create(workflow).getWorkflow();

            // Create a Role
            var role = new Role();
            role.setName("Example Role for Auto Grant Workflow");
            role = client.roles().create(role).getRole();

            // Create a workflow role
            var workflowRole = new WorkflowRole();
            workflowRole.workflowId = workflow.getId();
            workflowRole.roleId = role.getId();
            workflowRole = client.workflowRoles().create(workflowRole).getWorkflowRole();
            
            System.out.println("Successfully created workflow role.");
            System.out.printf("\tWorkflow ID: %s\n", workflowRole.getWorkflowId());
            System.out.printf("\tRole ID: %s\n", workflowRole.getRoleId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}