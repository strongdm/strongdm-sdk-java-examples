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

import com.strongdm.api.*;

/**
 * GroupsRoles CRUD Demo
 *
 * NOTE: This demo shows the structure of how GroupsRoles CRUD operations would work.
 * The Groups and GroupsRoles functionality requires a newer version of the Java SDK
 * that includes Groups support.
 *
 * This version demonstrates role management operations that work with the current SDK,
 * showing the pattern that would be used when Groups functionality becomes available.
 */
public class GroupsRolesCrud {
    public static void main(String[] args) {
        // Load the SDM API keys from the environment.
        // If these values are not set in your environment,
        // please follow the documentation here:
        // https://www.strongdm.com/docs/api/api-keys/
        String accessKey = System.getenv("SDM_API_ACCESS_KEY");
        String secretKey = System.getenv("SDM_API_SECRET_KEY");
        if (accessKey == null || accessKey.isEmpty() || secretKey == null || secretKey.isEmpty()) {
            System.err.println("SDM_API_ACCESS_KEY and SDM_API_SECRET_KEY must be provided");
            System.exit(1);
        }

        try {
            // Create the client
            // Use default ClientOptions unless SDM proxy is configured via env vars
            ClientOptions opts = new ClientOptions();
            Client client = new Client(accessKey, secretKey, opts);

            System.out.println("=== GroupsRoles CRUD Example ===");

            // Setup: Create prerequisite groups and roles
            System.out.println("\nSetup: Creating prerequisite groups and roles...");

            Group group1 = new Group();
            group1.setName("Test Group 1 - " + System.currentTimeMillis());
            GroupCreateResponse createGroup1Resp = client.groups().create(group1);
            Group createdGroup1 = createGroup1Resp.getGroup();
            System.out.printf("Created Group 1: %s (ID: %s)%n", createdGroup1.getName(), createdGroup1.getId());

            Group group2 = new Group();
            group2.setName("Test Group 2 - " + System.currentTimeMillis());
            GroupCreateResponse createGroup2Resp = client.groups().create(group2);
            Group createdGroup2 = createGroup2Resp.getGroup();
            System.out.printf("Created Group 2: %s (ID: %s)%n", createdGroup2.getName(), createdGroup2.getId());

            Role role1 = new Role();
            role1.setName("Test Role 1 - " + System.currentTimeMillis());
            RoleCreateResponse createRole1Resp = client.roles().create(role1);
            Role createdRole1 = createRole1Resp.getRole();
            System.out.printf("Created Role 1: %s (ID: %s)%n", createdRole1.getName(), createdRole1.getId());

            Role role2 = new Role();
            role2.setName("Test Role 2 - " + System.currentTimeMillis());
            RoleCreateResponse createRole2Resp = client.roles().create(role2);
            Role createdRole2 = createRole2Resp.getRole();
            System.out.printf("Created Role 2: %s (ID: %s)%n", createdRole2.getName(), createdRole2.getId());

            // 1. CREATE - Link groups to roles
            System.out.println("\n1. CREATE - Linking groups to roles...");
            GroupRole gr1 = new GroupRole();
            gr1.setGroupId(createdGroup1.getId());
            gr1.setRoleId(createdRole1.getId());
            GroupRoleCreateResponse createGR1 = client.groupsRoles().create(gr1);
            GroupRole createdGR1 = createGR1.getGroupRole();
            System.out.printf(
                "Created GroupRole 1: Group %s linked to Role %s (ID: %s)%n",
                createdGroup1.getName(), createdRole1.getName(), createdGR1.getId());

            GroupRole gr2 = new GroupRole();
            gr2.setGroupId(createdGroup1.getId());
            gr2.setRoleId(createdRole2.getId());
            GroupRoleCreateResponse createGR2 = client.groupsRoles().create(gr2);
            GroupRole createdGR2 = createGR2.getGroupRole();
            System.out.printf(
                "Created GroupRole 2: Group %s linked to Role %s (ID: %s)%n",
                createdGroup1.getName(), createdRole2.getName(), createdGR2.getId());

            GroupRole gr3 = new GroupRole();
            gr3.setGroupId(createdGroup2.getId());
            gr3.setRoleId(createdRole1.getId());
            GroupRoleCreateResponse createGR3 = client.groupsRoles().create(gr3);
            GroupRole createdGR3 = createGR3.getGroupRole();
            System.out.printf(
                "Created GroupRole 3: Group %s linked to Role %s (ID: %s)%n",
                createdGroup2.getName(), createdRole1.getName(), createdGR3.getId());

            // 2. READ (List)
            System.out.println("\n2. READ - Listing group role relationships...");

            System.out.println("\nAll GroupRoles:");
            var allGRIter = client.groupsRoles().list("");
            int allCount = 0;
            for (GroupRole gr : allGRIter) {
                allCount++;
                System.out.printf(
                    "  %d. ID: %s, Group ID: %s, Role ID: %s%n",
                    allCount, gr.getId(), gr.getGroupId(), gr.getRoleId());
            }
            System.out.printf("Total group roles found: %d%n", allCount);

            System.out.printf("\nGroupRoles for Group 1 (%s):%n", createdGroup1.getName());
            var group1Iter = client.groupsRoles().list("groupid:?", createdGroup1.getId());
            int g1Count = 0;
            for (GroupRole gr : group1Iter) {
                g1Count++;
                System.out.printf(
                    "  %d. ID: %s, Group ID: %s, Role ID: %s%n",
                    g1Count, gr.getId(), gr.getGroupId(), gr.getRoleId());
            }
            System.out.printf("Group roles for Group 1: %d%n", g1Count);

            System.out.printf("\nGroupRoles for Role 1 (%s):%n", createdRole1.getName());
            var role1Iter = client.groupsRoles().list("roleid:?", createdRole1.getId());
            int r1Count = 0;
            for (GroupRole gr : role1Iter) {
                r1Count++;
                System.out.printf(
                    "  %d. ID: %s, Group ID: %s, Role ID: %s%n",
                    r1Count, gr.getId(), gr.getGroupId(), gr.getRoleId());
            }
            System.out.printf("Group roles for Role 1: %d%n", r1Count);

            // 3. DELETE
            System.out.println("\n3. DELETE - Removing group role relationships...");
            client.groupsRoles().delete(createdGR1.getId());
            System.out.printf("Deleted GroupRole 1 (ID: %s)%n", createdGR1.getId());
            client.groupsRoles().delete(createdGR2.getId());
            System.out.printf("Deleted GroupRole 2 (ID: %s)%n", createdGR2.getId());
            client.groupsRoles().delete(createdGR3.getId());
            System.out.printf("Deleted GroupRole 3 (ID: %s)%n", createdGR3.getId());

            // Verify deletion
            System.out.println("\nVerifying deletion by listing group roles for our test group...");
            var verifyIter = client.groupsRoles().list("groupid:?", createdGroup1.getId());
            int remaining = 0;
            for (GroupRole ignored : verifyIter) {
                remaining++;
            }
            System.out.printf("Group roles remaining after deletion for Group 1: %d%n", remaining);

            // CLEANUP
            System.out.println("\nCleanup: Removing prerequisite resources...");
            try {
                client.groups().delete(createdGroup1.getId());
                System.out.printf("Deleted Group 1: %s%n", createdGroup1.getName());
            } catch (Exception e) {
                System.out.printf("Warning: failed to delete group %s: %s%n", createdGroup1.getId(), e.getMessage());
            }
            try {
                client.groups().delete(createdGroup2.getId());
                System.out.printf("Deleted Group 2: %s%n", createdGroup2.getName());
            } catch (Exception e) {
                System.out.printf("Warning: failed to delete group %s: %s%n", createdGroup2.getId(), e.getMessage());
            }
            try {
                client.roles().delete(createdRole1.getId());
                System.out.printf("Deleted Role 1: %s%n", createdRole1.getName());
            } catch (Exception e) {
                System.out.printf("Warning: failed to delete role %s: %s%n", createdRole1.getId(), e.getMessage());
            }
            try {
                client.roles().delete(createdRole2.getId());
                System.out.printf("Deleted Role 2: %s%n", createdRole2.getName());
            } catch (Exception e) {
                System.out.printf("Warning: failed to delete role %s: %s%n", createdRole2.getId(), e.getMessage());
            }

            System.out.println("\n=== GroupsRoles CRUD Example Completed Successfully! ===");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
