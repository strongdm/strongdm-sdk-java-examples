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
package samples;

import com.strongdm.api.v1.*;

public class RoleGrantMigration {
    public static void main(String[] args) throws Exception {
    	// Create the SDM Client
        Client client = new Client(
        // Load the SDM API keys from the environment.
        // If these values are not set in your environment,
        // please follow the documentation here:
        // https://www.strongdm.com/docs/api/api-keys/
            System.getenv("SDM_API_ACCESS_KEY"),
            System.getenv("SDM_API_SECRET_KEY")
        );

        // The RoleGrants API has been deprecated in favor of Access Rules. When
        // using Access Rules, the best practice is to grant Resources access
        // based on type and tags. If it is _necessary_ to grant access to
        // specific Resources in the same way as Role Grants did, you can use
        // Resource IDs directly in Access Rules as shown in the following
        // examples.

        createRoleGrantViaAccessRulesExample(client);
        deleteRoleGrantViaAccessRulesExample(client);
        listRoleGrantsViaAccessRulesExample(client);
    }

	// Example: Create a Role grant via Access Rules
    private static void createRoleGrantViaAccessRulesExample(Client client) {
        // Create example resources
        String resourceId1 = createExampleResource(client);
        String resourceId2 = createExampleResource(client);
        String roleId = createExampleRole(client, new java.util.ArrayList<AccessRule>());

        // Get the Role
        Role role = client.roles().get(roleId).getRole();

        // Append the ID to an existing static Access Rule
        if (role.getAccessRules().size() > 1 || (role.getAccessRules().size() == 1 && role.getAccessRules().get(0).getIds().size() == 0)) {
            throw new RuntimeException("unexpected access rules in role");
        }
        if (role.getAccessRules().size() == 0) {
            AccessRule rule = new AccessRule();
            rule.setIds(new java.util.ArrayList<String>());
            role.setAccessRules(java.util.List.of(rule));
        }
        role.getAccessRules().get(0).getIds().add(resourceId2);

        // Update the Role
        client.roles().update(role);
    }

	// Example: Delete Role grant via Access Rules
    private static void deleteRoleGrantViaAccessRulesExample(Client client) {
        // Create example Resources
        String resourceId1 = createExampleResource(client);
        String resourceId2 = createExampleResource(client);
        AccessRule rule = new AccessRule();
        rule.setIds(java.util.List.of(resourceId1, resourceId2));
        String roleId = createExampleRole(client, java.util.List.of(rule));

        // Get the Role
        Role role = client.roles().get(roleId).getRole();

        if (role.getAccessRules().size() != 1 || role.getAccessRules().get(0).getIds().size() == 0) {
            throw new RuntimeException("unexpected access rules in role");
        }

        // Remove the ID of the second Resource
        java.util.List<String> ids = role.getAccessRules().get(0).getIds();
        ids.remove(resourceId2);
        if (ids.size() == 0) {
            role.setAccessRules(new java.util.ArrayList<AccessRule>());
        }

        // Update the Role
        client.roles().update(role);
    }

	// Example: List Role grants via Access Rules
    private static void listRoleGrantsViaAccessRulesExample(Client client) {
        // Create example Resources
        String resourceId = createExampleResource(client);
        AccessRule rule = new AccessRule();
        rule.setIds(java.util.List.of(resourceId));
        String roleId = createExampleRole(client, java.util.List.of(rule));

        // Get the Role
        Role role = client.roles().get(roleId).getRole();

        // role.AccessRules contains each AccessRule associated with the Role
        for (String id : role.getAccessRules().get(0).getIds()) {
            System.out.println(id);
        }
    }

    // Example: Create a Role with empty Access Rules and return the ID
    private static String createExampleRole(Client client, java.util.List<AccessRule> ar) {
        Role role = new Role();
        role.setName("exampleRole-" + Integer.toString(new java.util.Random().nextInt(10000000)));
        role.setAccessRules(ar);
        return client.roles().create(role).getRole().getId();
    }

    // Example: Create a sample Resource and return the ID
    private static String createExampleResource(Client client) {
        Redis redis = new Redis();
        redis.setName("exampleResource-" + Integer.toString(new java.util.Random().nextInt(10000000)));
        redis.setHostname("example.com");
        redis.setPort(6379);
        redis.setPortOverride(new java.util.Random().nextInt(20000) + 3000);
        return client.resources().create(redis).getResource().getId();
    }
}