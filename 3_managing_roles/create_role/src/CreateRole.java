// Copyright 2020 StrongDM Inc
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

import com.strongdm.api.v1.*;

public class App {
    public static void main(String[] args) throws Exception {
    	// Create the SDM Client
        Client client = new Client(
        // Load the SDM API keys from the environment.
        // If these values are not set in your environment,
        // please follow the documentation here:
        // https://www.strongdm.com/docs/admin-guide/api-credentials/
            System.getenv("SDM_API_ACCESS_KEY"),
            System.getenv("SDM_API_SECRET_KEY")
        );

        // Define a resource (e.g., Redis)
        Redis redis = new Redis();
        redis.setName("accessRuleTest");
        redis.setHostname("example.com");
        redis.setPort(6379);
        redis.setPortOverride(2001);
        redis.setTags(java.util.Map.of(
            "env", "staging"
        ));
        redis = (Redis)client.resources().create(redis).getResource();

        // Create a Role with an initial Access Rule
        Role role = new Role();
        role.setName("accessRuleTest");
        AccessRule rule1 = new AccessRule();
        rule1.setIds(java.util.List.of(redis.getId()));
        role.setAccessRules(java.util.List.of(rule1));
        role = client.roles().create(role).getRole();

        // Update the Role's Access Rules
        AccessRule rule2 = new AccessRule();
        rule2.setType("postgres");
        AccessRule rule3 = new AccessRule();
        rule3.setTags(java.util.Map.of(
            "env", "staging"
        ));
        role.setAccessRules(java.util.List.of(rule2, rule3));
        role = client.roles().update(role).getRole();

        // The Role Grants API has been deprecated in favor of Access Rules. When
        // using Access Rules, the best practice is to grant Resources access
        // based on type and tags. If it is _necessary_ to grant access to
        // specific resources in the same way as Role Grants did, you can use
        // resource IDs directly in Access Rules as shown in the following
        // examples.

        createRoleGrantViaAccessRulesExample(client);
        deleteRoleGrantViaAccessRulesExample(client);
        listRoleGrantsViaAccessRulesExample(client);
    }

    // Example: Create a Role with empty Access Rules and return the ID
    private static String createExampleRole(Client client, java.util.List<AccessRule> ar) {
        Role role = new Role();
        role.setName("exampleRole-" + Integer.toString(new java.util.Random().nextInt(10000000)));
        role.setAccessRules(ar);
        return client.roles().create(role).getRole().getId();
    }

    // Example: Create a sample resource and return the ID
    private static String createExampleResource(Client client) {
        Redis redis = new Redis();
        redis.setName("exampleResource-" + Integer.toString(new java.util.Random().nextInt(10000000)));
        redis.setHostname("example.com");
        redis.setPort(6379);
        redis.setPortOverride(new java.util.Random().nextInt(20000) + 3000);
        return client.resources().create(redis).getResource().getId();
    }
}