// Copyright 2024 StrongDM Inc
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

public class USHost {
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
            // Create the client with the US host.
            String[] hostAndPort = APIHost.US.split(":");
            var opts = new ClientOptions();
            opts.withHostAndPort(hostAndPort[0], Integer.parseInt(hostAndPort[1]));
            var client = new Client(apiAccessKey, apiSecretKey, opts);

            // Create an approval workflow for example
            var approvalWorkflow = new ApprovalWorkflow();
            approvalWorkflow.setName("Example Create Approval Workflow");
            approvalWorkflow.setApprovalMode("automatic");
            ApprovalWorkflowCreateResponse createResp = client.approvalWorkflows().create(approvalWorkflow);
            approvalWorkflow = createResp.getApprovalWorkflow();
            
            System.out.println("Successfully created approval workflow.");
            System.out.printf("\tID: %s\n", approvalWorkflow.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}
