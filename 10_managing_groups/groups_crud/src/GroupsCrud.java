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
 * Groups CRUD Demo
 *
 * NOTE: This demo shows the structure of how Groups CRUD operations would work.
 * The Groups functionality requires a newer version of the Java SDK that includes Groups support.
 *
 * This version demonstrates role management operations that work with the current SDK,
 * showing the pattern that would be used when Groups functionality becomes available.
 */
public class GroupsCrud {
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

            System.out.println("=== Groups CRUD Example ===");

            // 1. CREATE
            System.out.println("\n1. CREATE - Creating two test groups...");
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

            // 2. READ (List)
            System.out.println("\n2. READ - Listing all groups...");
            var groupList = client.groups().list("");
            System.out.println("All Groups:");
            int groupCount = 0;
            for (Group g : groupList) {
                groupCount++;
                System.out.printf("  %d. Name: %s, ID: %s%n", groupCount, g.getName(), g.getId());
            }
            System.out.printf("Total groups found: %d%n", groupCount);

            // 3. UPDATE
            System.out.println("\n3. UPDATE - Updating group names...");
            createdGroup1.setName("Updated Test Group 1 - " + System.currentTimeMillis());
            GroupUpdateResponse updateGroup1Resp = client.groups().update(createdGroup1);
            Group updatedGroup1 = updateGroup1Resp.getGroup();
            System.out.printf("Updated Group 1: %s (ID: %s)%n", updatedGroup1.getName(), updatedGroup1.getId());

            createdGroup2.setName("Updated Test Group 2 - " + System.currentTimeMillis());
            GroupUpdateResponse updateGroup2Resp = client.groups().update(createdGroup2);
            Group updatedGroup2 = updateGroup2Resp.getGroup();
            System.out.printf("Updated Group 2: %s (ID: %s)%n", updatedGroup2.getName(), updatedGroup2.getId());

            // Verify updates
            System.out.println("\nVerifying updates by filtering for our test groups...");
            var updatedIter = client.groups().list("name:\"Updated Test Group*\"");
            System.out.println("Updated Test Groups:");
            int updatedCount = 0;
            for (Group g : updatedIter) {
                updatedCount++;
                System.out.printf("  %d. Name: %s, ID: %s%n", updatedCount, g.getName(), g.getId());
            }

            // 4. DELETE
            System.out.println("\n4. DELETE - Cleaning up created groups...");
            client.groups().delete(updatedGroup1.getId());
            System.out.printf("Deleted Group 1: %s%n", updatedGroup1.getName());
            client.groups().delete(updatedGroup2.getId());
            System.out.printf("Deleted Group 2: %s%n", updatedGroup2.getName());

            // Verify deletion
            System.out.println("\nVerifying deletion by attempting to list our deleted groups...");
            var verifyIter = client.groups().list("name:\"Updated Test Group*\"");
            int remaining = 0;
            for (Group ignored : verifyIter) {
                remaining++;
            }
            System.out.printf("Groups remaining after deletion: %d%n", remaining);

            System.out.println("\n=== Groups CRUD Example Completed Successfully! ===");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
