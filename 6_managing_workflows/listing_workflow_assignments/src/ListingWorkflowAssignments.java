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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.management.relation.Role;
import javax.swing.text.html.HTML.Tag;
import javax.management.relation.Role;

import com.strongdm.api.v1.*;

public class ListingWorkflowAssignments {
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

            // Create a Workflow
            var workflow = new Workflow();
            workflow.setName("Example Create Manual Grant Worfklow");
            workflow.setDescription("Example Workflow Description");
            workflow = client.workflows().create(workflow).getWorkflow();

            System.out.println("Successfully created workflow.");
            System.out.printf("\tID: %s\n", workflow.getId());
            System.out.printf("\tName: %s\n", workflow.getName());

            // Create a resource - used for workflow assignments
            var tags = java.util.Map.of("env", "example");
            var resource = new Mysql();
            resource.setName("Example MySQL Datasource for Workflow Assignments");
            resource.setHostname("example.strongdm.com");
            resource.setPort(5432);
            resource.setUsername("example");
            resource.setPassword("example");
            resource.setDatabase("example");
            resource.setPortOverride(19999);
            resource.setTags(tags);
            
            resource = client.resources()
                        .withDeadlineAfter(30, TimeUnit.SECONDS)
                        .create(resource)
                        .getResource();
            
            // Update workflow assignments
            // For this example, since our created resource has the same tag as the access rule,
            // the workflow assignment should be created automatically
            var rule1 = new AccessRule();
            rule2.setType("mysql");
            var rule2 = new AccessRule();
            rule2.setTags(tags);
            workflow.setAccessRules(java.util.List.of(rule1, rule2));
            client.workflows().update(workflow).getWorkflow();

            var workflowId = workflow.getId();
            var resourceId = resource.getId();

            System.out.println("Successfully created workflow assignment.");
            System.out.printf("\tWorkflow ID: %s\n", workflowId);
            System.out.printf("\tResource ID: %s\n", resourceId);

	        // List WorkflowAssignments
            var filter = "resource:" + resourceId + " workflow:" + workflowId;
            Iterable<WorkflowAssignment> listResp = client.workflowAssignments().list(filter); // TODO: why does var make IDE complain?

            var assignments = new ArrayList<>();
            for (WorkflowAssignment n : listResp) {
                var workflowAssignment = n;
                System.out.println("WorkflowAssignment Resource ID: %s\n", n.getResourceId());
                System.out.println("WorkflowAssignment Workflow ID: %s\n", n.getWorkflowId());
                assignments.add(workflowAssignment);
            }
            
            if (assignments.size() !=1) {
                throw new RuntimeException("list failed: expected %d workflows, got %d",
                                            1, assignments.size());
            }
            
	        System.out.println("Successfully list WorkflowAssignment.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}