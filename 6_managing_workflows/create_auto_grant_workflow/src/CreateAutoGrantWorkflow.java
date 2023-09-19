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

import com.strongdm.api.v1.*;

public class CreateManualGrantWorkflow {
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
            AccessRule accessRule = new AccessRule();
            Map<String, String> tags = new Hashmap<>();
            tags.put("env", "dev");
            accessRule.setTags(tags);

            // Create a Workflow
            Workflow workflow = new Workflow();
            workflow.setName("Example Create Auto Grant Worfklow");
            workflow.setDescription("Example Workflow Description");
            workflow.setAutoGrant(true);
            workflow.setEnabled(true);
            workflow.setAccessRule(accessRule);

            workflow = client.workflows().create(workflow).getWorkflow();

            System.out.println("Successfully created workflow.");
            System.out.printf("\tID: %s\n", workflow.getId());
            System.out.printf("\tName: %s\n", workflow.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}