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

import java.util.ArrayList;
import java.util.List;

import com.strongdm.api.*;

public class ListApprovalWorkflow {
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

            // Create an approver account - used for creating an approval workflow approver
            var user = new User();
            user.setEmail("approval-workflow-approver-example@example.com");
            user.setFirstName("example");
            user.setLastName("example");
            user = (User)client.accounts().create(user).getAccount();
            
            // Create an approver role - used for creating an approval workflow approver
            var role = new Role();
            role.setName("Example Role for Approval Workflow Approver");
            role = client.roles().create(role).getRole();

            // Configure approval workflow steps
            List<ApprovalFlowStep> approvalWorkflowSteps = new ArrayList<>();

            // Configure an approval workflow approver
            var approver1 = new ApprovalFlowApprover();
            approver1.setAccountId(user.getId());
            List<ApprovalFlowApprover> step1Approvers = new ArrayList<>();
            step1Approvers.add(approver1);
            // Add Approval step to configuration
            var step1 = new ApprovalFlowStep();
            step1.setQuantifier("any");
            step1.setSkipAfter(Duration.ofHours(1));
            step1.setApprovers(step1Approvers);
            approvalWorkflowSteps.add(step1);

            // Configure an approval workflow approver
            var approver2 = new ApprovalFlowApprover();
            approver2.setRoleId(role.getId());
            List<ApprovalFlowApprover> step2Approvers = new ArrayList<>();
            step2Approvers.add(approver2);
            // Add Approval step to configuration
            var step2 = new ApprovalFlowStep();
            step2.setQuantifier("any");
            step2.setApprovers(step2Approvers);
            approvalWorkflowSteps.add(step2);

            // Configure approval workflow
            var approvalWorkflow = new ApprovalWorkflow();
            approvalWorkflow.setName("List example manual approval flow");
            approvalWorkflow.setApprovalMode("manual");
            approvalWorkflow.setDescription("sample approval workflow");
            approvalWorkflow.setApprovalWorkflowSteps(approvalWorkflowSteps);
            var createResp = client.approvalWorkflows().create(approvalWorkflow);
            approvalWorkflow = createResp.getApprovalWorkflow();
            
            System.out.println("Successfully created approval workflow.");
            System.out.printf("\tID: %s\n", approvalWorkflow.getId());
            System.out.printf("\tName: %s\n", approvalWorkflow.getName());
            System.out.printf("\tDescription: %s\n", approvalWorkflow.getDescription());
            System.out.printf("\tApproval Mode: %s\n", approvalWorkflow.getApprovalMode());
            System.out.printf("\tNumber of Approval Steps: %s\n", approvalWorkflow.getApprovalWorkflowSteps().size());
            
            // Configure and create an autogrant approval workflow
            var autograntFlow = new ApprovalWorkflow();
            autograntFlow.setName("List example autogrant Approval Workflow");
            autograntFlow.setApprovalMode("automatic");
            ApprovalWorkflowCreateResponse autograntCreateResp = client.approvalWorkflows().create(approvalWorkflow);
            autograntFlow = autograntCreateResp.getApprovalWorkflow();
            
            // List approval workflows by approval workflow name
            Iterable<ApprovalWorkflow> resp = client.approvalWorkflows().list("name:?", "List*");
            List<ApprovalWorkflow> gotFlows = new ArrayList<>();
            for (ApprovalWorkflow n : resp) {
                ApprovalWorkflow w = n;
                gotFlows.add(w);
            }
            System.out.printf("Successfully got %s approval workflows filtering by name\n", gotFlows.size());
            
            // List approval workflows by approval workflow id
            Iterable<ApprovalWorkflow> listByIdResp = client.approvalWorkflows().list("id:?", approvalWorkflow.getId());
            List<ApprovalWorkflow> gotFlowsById = new ArrayList<>();
            for (ApprovalWorkflow n : listByIdResp) {
                ApprovalWorkflow w = n;
                gotFlowsById.add(w);
            }
            System.out.printf("Successfully got %s approval workflows filtering by id\n", gotFlowsById.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}
