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
import java.util.UUID;

import javax.management.relation.Role;

import com.strongdm.api.*;

public class ManualApprovalWorkflow {
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
            var role2 = new Role();
            role2.setName("Example Role for Approval Workflow Approver");
            role2 = client.roles().create(role2).getRole();

            // Configure approval workflow steps
            List<ApprovalFlowStep> approvalWorkflowSteps = new ArrayList<>();

            // Configure an approval workflow approver
            var approver1 = new ApprovalFlowApprover();
            approver1.setAccountId(user.getId());
            var approver2 = new ApprovalFlowApprover();
            approver2.setRoleId(role.getId());
            List<ApprovalFlowApprover> step1Approvers = new ArrayList<>();
            approvers.add(apr1);
            approvers.add(apr2);
            // Add Approval step to configuration
            var step1 = new ApprovalFlowStep();
            step1.setQuantifier("any");
            step1.setSkipAfter(Duration.ofHours(1));
            step1.setApprovers(step1Approvers);
            approvalWorkflowSteps.add(step1);

            // Configure an approval workflow approver
            var approver3 = new ApprovalFlowApprover();
            approver3.setAccountId(user.getId());
            List<ApprovalFlowApprover> step2Approvers = new ArrayList<>();
            approvers.add(approver3);
            // Add Approval step to configuration
            var step2 = new ApprovalFlowStep();
            step2.setQuantifier("any");
            step2.setSkipAfter(Duration.ofHours(1));
            step2.setApprovers(step2Approvers);
            approvalWorkflowSteps.add(step2);

            // Configure approval workflow
            var approvalWorkflow = new ApprovalWorkflow();
            approvalWorkflow.setName("example manual approval flow");
            approvalWorkflow.setApprovalMode("manual");
            approvalWorkflow.setDescription("sample approval workflow");
            approvalWorkflow.setApprovalWorkflowSteps(wfs);
            var createResp = client.approvalWorkflows().create(approvalWorkflow);
            approvalWorkflow = createResp.getApprovalWorkflow();
            
            System.out.println("Successfully created approval workflow.");
            System.out.printf("\tID: %s\n", approvalWorkflow.getId());
            System.out.printf("\tName: %s\n", approvalWorkflow.getName());
            System.out.printf("\tDescription: %s\n", approvalWorkflow.getDescription());
            System.out.printf("\tApproval Mode: %s\n", approvalWorkflow.getApprovalMode());
            System.out.printf("\tNumber of Approval Steps: %s\n", approvalWorkflow.getApprovalWorkflowSteps().size());

            // Update the approval workflow
            // Configure approval workflow steps
            List<ApprovalFlowStep> updatedApprovalSteps = new ArrayList<>();

            // Configure an approval workflow approver
            var updatedApprover1 = new ApprovalFlowApprover();
            updatedApprover1.setAccountId(user.getId());
            List<ApprovalFlowApprover> updatedStep1Approvers = new ArrayList<>();
            updatedStep1Approvers.add(updatedApprover1);
            // Add Approval step to configuration
            var updatedStep1 = new ApprovalFlowStep();
            updatedStep1.setQuantifier("all");
            updatedStep1.setApprovers(updatedStep1Approvers);
            updatedApprovalSteps.add(updatedStep1);

            // Add Approval step to configuration
            var updatedApprover2 = new ApprovalFlowApprover();
            updatedApprover2.setRoleId(role.getId());
            List<ApprovalFlowApprover> updatedStep2Approvers = new ArrayList<>();
            updatedStep2Approvers.add(updatedApprover2);
            var updatedStep2 = new ApprovalFlowStep();
            updatedStep2.setQuantifier("any");
            updatedStep2.setSkipAfter(Duration.ofHours(3));
            updatedStep2.setApprovers(updatedStep2Approvers);
            updatedApprovalSteps.add(updatedStep2);

            // Add Approval step to configuration
            var updatedApprover3 = new ApprovalFlowApprover();
            updatedApprover3.setRoleId(role2.getId());
            List<ApprovalFlowApprover> updatedStep3Approvers = new ArrayList<>();
            updatedStep3Approvers.add(updatedApprover3);
            var updatedStep3 = new ApprovalFlowStep();
            updatedStep3.setQuantifier("any");
            updatedStep3.setSkipAfter(Duration.ofHours(2));
            updatedStep3.setApprovers(updatedStep3Approvers);
            updatedApprovalSteps.add(updatedStep3);

            var updatedApprovalWorkflowConfig = new ApprovalWorkflow();
            updatedApprovalWorkflowConfig.setId(approvalWorkflow.getId()); // id is required for update
            updatedApprovalWorkflowConfig.setName("Example New Name");
            updatedApprovalWorkflowConfig.setDescription("example new  approval workflow description");
            updatedApprovalWorkflowConfig.setApprovalMode("manual");
            updatedApprovalWorkflowConfig.setApprovalWorkflowSteps(updatedApprovalSteps);
            var updatedApprovalFlow = client.approvalWorkflows().update(updatedApprovalWorkflowConfig).getApprovalWorkflow();
            
            System.out.println("Successfully updated approval workflow name.");
            System.out.printf("\tID: %s\n", updatedApprovalFlow.getId());
            System.out.printf("\tName: %s\n", updatedApprovalFlow.getName());
            System.out.printf("\tDescription: %s\n", updatedApprovalFlow.getDescription());
            System.out.printf("\tApproval Mode: %s\n", updatedApprovalFlow.getApprovalMode());
            System.out.printf("\tNumber of Approval Steps: %s\n", updatedApprovalFlow.getApprovalWorkflowSteps().size());

            // Get Approval Workflow
            var getResp = client.approvalWorkflows().get(updatedApprovalFlow.getId());
            var gotApprovalWorkflow = getResp.getApprovalWorkflow();
            System.out.println("Successfully got approval workflow name.");
            System.out.printf("\tID: %s\n", gotApprovalWorkflow.getId());
            System.out.printf("\tName: %s\n", gotApprovalWorkflow.getName());
            System.out.printf("\tDescription: %s\n", gotApprovalWorkflow.getDescription());
            System.out.printf("\tApproval Mode: %s\n", gotApprovalWorkflow.getApprovalMode());
            System.out.printf("\tNumber of Approval Steps: %s\n", gotApprovalWorkflow.getApprovalWorkflowSteps().size());

            // Delete Approval Workflow
            client.approvalWorkflows().delete(gotApprovalWorkflow.getId());
            System.out.println("Successfully deleted approval workflow.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}
