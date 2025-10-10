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

public class CreateManualGrantWorkflow {
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

            // Create an approver account that will participate in the manual approval flow.
            var user = new User();
            user.setEmail("create-workflow-approver-example@example.com");
            user.setFirstName("example");
            user.setLastName("example");
            user = (User)client.accounts().create(user).getAccount();

        	// Build a manual ApprovalWorkflow that references the approver created above.
            var flowApprover = new ApprovalFlowApprover();
            flowApprover.setAccountId(user.getId());
            var approvalStep = new ApprovalFlowStep();
            approvalStep.setQuantifier("any");
            approvalStep.setApprovers(java.util.List.of(flowApprover));
            var approvalFlow = new ApprovalWorkflow();
            approvalFlow.setName("Example Manual Approval Workflow");
            approvalFlow.setDescription("Manual approval flow to back the access workflow example");
            approvalFlow.setApprovalMode("manual");
            approvalFlow.setApprovalWorkflowSteps(java.util.List.of(approvalStep));
            approvalFlow = client.approvalWorkflows().create(approvalFlow).getApprovalWorkflow();

            System.out.println("Successfully created approval workflow.");
            System.out.printf("\tID: %s\n", approvalFlow.getId());
            System.out.printf("\tName: %s\n", approvalFlow.getName());

            // Create an access rule for the workflow
            var accessRule = new AccessRule();
            accessRule.setTags(java.util.Map.of("env", "example"));

            // Create a Workflow
            var workflow = new Workflow();
            workflow.setName("Example Create Manual Grant Workflow");
            workflow.setDescription("Example Workflow Description");
            workflow.setApprovalFlowId(approvalFlow.getId());
            workflow.setAccessRules(java.util.List.of(accessRule));

            workflow = client.workflows().create(workflow).getWorkflow();

            System.out.println("Successfully created workflow.");
            System.out.printf("\tID: %s\n", workflow.getId());
            System.out.printf("\tName: %s\n", workflow.getName());

            // To allow users access to the resources managed by this workflow, you must
            // add workflow roles to the workflow.
            // Two steps are needed to add a workflow role:
            // Step 1: create a Role
            // Step 2: create a WorkflowRole

            // Create a Role
            var role = new Role();
            role.setName("Example Role for Auto Grant Workflow");
            role = client.roles().create(role).getRole();

            // Create a workflow role
            var workflowRole = new WorkflowRole();
            workflowRole.setWorkflowId(workflow.getId());
            workflowRole.setRoleId(role.getId());
            workflowRole = client.workflowRoles().create(workflowRole).getWorkflowRole();
            
            System.out.println("Successfully created workflow role.");
            System.out.printf("\tWorkflow ID: %s\n", workflowRole.getWorkflowId());
            System.out.printf("\tRole ID: %s\n", workflowRole.getRoleId());
                
            // You can enable this workflow after adding workflow approvers.
            
            // Update Workflow Enabled
            workflow.setEnabled(true);
            workflow = client.workflows().update(workflow).getWorkflow();

            System.out.println("Successfully updated workflow enabled.");
            System.out.printf("\tID: %s\n", workflow.getId());
            System.out.printf("\tEnabled: %s\n", workflow.getEnabled());

        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}