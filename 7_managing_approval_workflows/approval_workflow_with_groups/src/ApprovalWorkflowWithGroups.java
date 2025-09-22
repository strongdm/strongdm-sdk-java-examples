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

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import com.strongdm.api.AccountGroup;
import com.strongdm.api.ApprovalFlowApprover;
import com.strongdm.api.ApprovalFlowStep;
import com.strongdm.api.ApprovalWorkflow;
import com.strongdm.api.ApproverReference;
import com.strongdm.api.Client;
import com.strongdm.api.ClientOptions;
import com.strongdm.api.Group;
import com.strongdm.api.User;

public class ApprovalWorkflowWithGroups {
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

            System.out.println("Example showing how to create approval workflows using groups as approvers");

            // Create approver groups - these groups will be designated as approvers
            var securityGroup = new Group();
            securityGroup.setName("Security Team");
            var securityGroupResponse = client.groups().create(securityGroup);
            var securityGroupId = securityGroupResponse.getGroup().getId();
            System.out.println("Created Security Team group: " + securityGroupId);

            var adminGroup = new Group();
            adminGroup.setName("Administrators");
            var adminGroupResponse = client.groups().create(adminGroup);
            var adminGroupId = adminGroupResponse.getGroup().getId();
            System.out.println("Created Administrators group: " + adminGroupId);

            var devOpsGroup = new Group();
            devOpsGroup.setName("DevOps Team");
            var devOpsGroupResponse = client.groups().create(devOpsGroup);
            var devOpsGroupId = devOpsGroupResponse.getGroup().getId();
            System.out.println("Created DevOps Team group: " + devOpsGroupId);

            // Create some users to add to groups (demonstrating group membership)
            var securityUser = new User();
            securityUser.setEmail("security-lead@example.com");
            securityUser.setFirstName("Security");
            securityUser.setLastName("Lead");
            var securityUserResponse = client.accounts().create(securityUser);
            var securityUserId = securityUserResponse.getAccount().getId();

            var adminUser = new User();
            adminUser.setEmail("admin-user@example.com");
            adminUser.setFirstName("Admin");
            adminUser.setLastName("User");
            var adminUserResponse = client.accounts().create(adminUser);
            var adminUserId = adminUserResponse.getAccount().getId();

            // Add users to their respective groups
            var securityAccountGroup = new AccountGroup();
            securityAccountGroup.setAccountId(securityUserId);
            securityAccountGroup.setGroupId(securityGroupId);
            client.accountsGroups().create(securityAccountGroup);
            System.out.println("Added security user to Security Team group");

            var adminAccountGroup = new AccountGroup();
            adminAccountGroup.setAccountId(adminUserId);
            adminAccountGroup.setGroupId(adminGroupId);
            client.accountsGroups().create(adminAccountGroup);
            System.out.println("Added admin user to Administrators group");

            // Create a manual approval workflow with groups as approvers
            var approvalWorkflow = new ApprovalWorkflow();
            approvalWorkflow.setName("Group-Based Approval Workflow");
            approvalWorkflow.setDescription("A workflow demonstrating group-based approvers");
            approvalWorkflow.setApprovalMode("manual");

            // Step 1: Any member of the Security Team can approve
            var step1 = new ApprovalFlowStep();
            step1.setQuantifier("any");
            var step1Approvers = new ArrayList<ApprovalFlowApprover>();
            var securityApprover = new ApprovalFlowApprover();
            securityApprover.setGroupId(securityGroupId);
            step1Approvers.add(securityApprover);
            step1.setApprovers(step1Approvers);

            // Step 2: All specified groups must approve
            var step2 = new ApprovalFlowStep();
            step2.setQuantifier("all");
            step2.setSkipAfter(Duration.ofHours(2));
            var step2Approvers = new ArrayList<ApprovalFlowApprover>();
            
            var adminApprover = new ApprovalFlowApprover();
            adminApprover.setGroupId(adminGroupId);
            step2Approvers.add(adminApprover);

            var devOpsApprover = new ApprovalFlowApprover();
            devOpsApprover.setGroupId(devOpsGroupId);
            step2Approvers.add(devOpsApprover);

            var managerApprover = new ApprovalFlowApprover();
            managerApprover.setReference(ApproverReference.MANAGER_OF_REQUESTER);
            step2Approvers.add(managerApprover);

            step2.setApprovers(step2Approvers);

            // Step 3: Mixed approvers - combination of groups and references
            var step3 = new ApprovalFlowStep();
            step3.setQuantifier("any");
            step3.setSkipAfter(Duration.ofHours(1));
            var step3Approvers = new ArrayList<ApprovalFlowApprover>();

            var securityApprover3 = new ApprovalFlowApprover();
            securityApprover3.setGroupId(securityGroupId);
            step3Approvers.add(securityApprover3);

            var adminApprover3 = new ApprovalFlowApprover();
            adminApprover3.setGroupId(adminGroupId);
            step3Approvers.add(adminApprover3);

            var managerOfManagerApprover = new ApprovalFlowApprover();
            managerOfManagerApprover.setReference(ApproverReference.MANAGER_OF_MANAGER_OF_REQUESTER);
            step3Approvers.add(managerOfManagerApprover);

            step3.setApprovers(step3Approvers);

            var steps = List.of(step1, step2, step3);
            approvalWorkflow.setApprovalWorkflowSteps(steps);

            var workflowResponse = client.approvalWorkflows().create(approvalWorkflow);
            var createdWorkflow = workflowResponse.getApprovalWorkflow();

            System.out.println("\nSuccessfully created group-based approval workflow.");
            System.out.println("\tID: " + createdWorkflow.getId());
            System.out.println("\tName: " + createdWorkflow.getName());
            System.out.println("\tDescription: " + createdWorkflow.getDescription());
            System.out.println("\tNumber of Approval Steps: " + createdWorkflow.getApprovalWorkflowSteps().size());

            for (int i = 0; i < createdWorkflow.getApprovalWorkflowSteps().size(); i++) {
                var step = createdWorkflow.getApprovalWorkflowSteps().get(i);
                System.out.println("\nStep " + (i + 1) + ":");
                System.out.println("\tQuantifier: " + step.getQuantifier());
                if (step.getSkipAfter() != null && !step.getSkipAfter().isZero()) {
                    System.out.println("\tSkip After: " + step.getSkipAfter());
                }
                System.out.println("\tApprovers:");
                for (var approver : step.getApprovers()) {
                    if (approver.getAccountId() != null && !approver.getAccountId().isEmpty()) {
                        System.out.println("\t\t- Account ID: " + approver.getAccountId());
                    } else if (approver.getRoleId() != null && !approver.getRoleId().isEmpty()) {
                        System.out.println("\t\t- Role ID: " + approver.getRoleId());
                    } else if (approver.getGroupId() != null && !approver.getGroupId().isEmpty()) {
                        System.out.println("\t\t- Group ID: " + approver.getGroupId());
                    } else if (approver.getReference() != null) {
                        System.out.println("\t\t- Reference: " + approver.getReference());
                    }
                }
            }

            System.out.println("\nExample demonstrates:");
            System.out.println("  • Creating groups to act as approvers");
            System.out.println("  • Adding users to groups (group membership)");
            System.out.println("  • Using GroupId in approval workflow steps");
            System.out.println("  • Combining group approvers with other approver types");
            System.out.println("  • Different quantifiers (any/all) for group-based approval");

            // Clean up - delete the approval workflow
            client.approvalWorkflows().delete(createdWorkflow.getId());
            System.out.println("\nCleaned up approval workflow.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}