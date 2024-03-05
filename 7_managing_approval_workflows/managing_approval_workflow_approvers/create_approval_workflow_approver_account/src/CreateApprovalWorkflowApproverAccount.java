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

import com.strongdm.api.*;

public class CreateApprovalWorkflowApproverAccount {
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

            // Create an approval workflow
            var approvalWorkflow = new ApprovalWorkflow();
            approvalWorkflow.setName("Example Approval Workflow");
            approvalWorkflow.setApprovalMode("manual");
            ApprovalWorkflowCreateResponse createResp = client.approvalWorkflows().create(approvalWorkflow);
            approvalWorkflow = createResp.getApprovalWorkflow();

            // Create an approval workflow step
            ApprovalWorkflowStep step = new ApprovalWorkflowStep();
            step.setApprovalFlowId(approvalWorkflow.getId());
            ApprovalWorkflowStepCreateResponse stepCreateResp = client.approvalWorkflowSteps().create(step);
            step = stepCreateResp.getApprovalWorkflowStep();

            // Create an approver account - used for creating an approval workflow approver
            var user = new User();
            user.setEmail("approval-workflow-approver-example@example.com");
            user.setFirstName("example");
            user.setLastName("example");
            user = (User)client.accounts().create(user).getAccount();

            // Create an approval workflow approver
            ApprovalWorkflowApprover approver = new ApprovalWorkflowApprover();
            approver.setApprovalFlowId(approvalWorkflow.getId());
            approver.setApprovalStepId(step.getId());
            approver.setAccountId(user.getId());
            ApprovalWorkflowApproverCreateResponse createApproverResp = client.approvalWorkflowApprovers().create(approver);
            approver = createApproverResp.getApprovalWorkflowApprover();

            System.out.println("Successfully created approval workflow approver.");
            System.out.printf("\tID: %s\n", approver.getId());
            System.out.printf("\tAccount ID: %s\n", approver.getAccountId());
            System.out.printf("\tApproval Workflow ID: %s\n", approver.getApprovalFlowId());
            System.out.printf("\tApproval Step ID: %s\n", approver.getApprovalStepId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}
