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

public class AutograntApprovalWorkflow {
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
            approvalWorkflow.setName("Example Autogrant Approval Workflow");
            approvalWorkflow.setApprovalMode("automatic");
            ApprovalWorkflowCreateResponse createResp = client.approvalWorkflows().create(approvalWorkflow);
            approvalWorkflow = createResp.getApprovalWorkflow();
            
            System.out.println("Successfully created approval workflow.");
            System.out.printf("\tID: %s\n", approvalWorkflow.getId());
            System.out.printf("\tName: %s\n", approvalWorkflow.getName());

            // Update the approval workflow name
            approvalWorkflow.setName("Example New Name");
            approvalWorkflow = client.approvalWorkflows().update(approvalWorkflow).getApprovalWorkflow();
            
            System.out.println("Successfully updated approval workflow name.");
            System.out.printf("\tID: %s\n", approvalWorkflow.getId());
            System.out.printf("\tName: %s\n", approvalWorkflow.getName());

            // Update the approval workflow description
            approvalWorkflow.setDescription("Example New Description");
            approvalWorkflow = client.approvalWorkflows().update(approvalWorkflow).getApprovalWorkflow();
            
            System.out.println("Successfully updated approval workflow description.");
            System.out.printf("\tID: %s\n", approvalWorkflow.getId());
            System.out.printf("\tDescription: %s\n", approvalWorkflow.getDescription());

            // Update the approval workflow approval mode
            approvalWorkflow.setApprovalMode("manual");
            approvalWorkflow = client.approvalWorkflows().update(approvalWorkflow).getApprovalWorkflow();
            
            System.out.println("Successfully updated approval workflow approval mode.");
            System.out.printf("\tID: %s\n", approvalWorkflow.getId());
            System.out.printf("\tApproval Mode: %s\n", approvalWorkflow.getApprovalMode());

            // Delete the approval workflow
            client.approvalWorkflows().delete(approvalWorkflow.getId());
            System.out.println("Successfully deleted approval workflow.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}
