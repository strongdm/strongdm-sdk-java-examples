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
 * AccountsGroups CRUD Demo
 *
 * NOTE: This demo shows the structure of how AccountsGroups CRUD operations would work.
 * The Groups and AccountsGroups functionality requires a newer version of the Java SDK
 * that includes Groups support.
 *
 * This version demonstrates account management operations that work with the current SDK,
 * showing the pattern that would be used when Groups functionality becomes available.
 */
public class AccountsGroupsCrud {
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

            System.out.println("=== AccountsGroups CRUD Example ===");

            // Setup: Create prerequisite resources
            System.out.println("\nSetup: Creating prerequisite account and groups...");

            // Create test account
            User account = new User();
            account.setFirstName("Test");
            account.setLastName("User");
            account.setEmail("test-user-" + System.currentTimeMillis() + "@example.com");
            account.setPermissionLevel(PermissionLevel.USER);

            AccountCreateResponse createAccountResp = client.accounts().create(account);
            Account createdAccount = createAccountResp.getAccount();
            String accountID = createdAccount.getId();
            System.out.printf("Created Account: %s (ID: %s)%n", account.getEmail(), accountID);

            // Create test groups
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

            // 1. CREATE - link account to groups
            System.out.println("\n1. CREATE - Linking account to groups...");
            AccountGroup ag1 = new AccountGroup();
            ag1.setAccountId(accountID);
            ag1.setGroupId(createdGroup1.getId());
            AccountGroupCreateResponse createAG1 = client.accountsGroups().create(ag1);
            AccountGroup createdAG1 = createAG1.getAccountGroup();
            System.out.printf(
                "Created AccountGroup 1: Account %s linked to Group %s (ID: %s)%n",
                accountID, createdGroup1.getName(), createdAG1.getId());

            AccountGroup ag2 = new AccountGroup();
            ag2.setAccountId(accountID);
            ag2.setGroupId(createdGroup2.getId());
            AccountGroupCreateResponse createAG2 = client.accountsGroups().create(ag2);
            AccountGroup createdAG2 = createAG2.getAccountGroup();
            System.out.printf(
                "Created AccountGroup 2: Account %s linked to Group %s (ID: %s)%n",
                accountID, createdGroup2.getName(), createdAG2.getId());

            // 2. READ (List)
            System.out.println("\n2. READ - Listing account group relationships...");

            System.out.println("\nAll AccountGroups:");
            var allAGIter = client.accountsGroups().list("");
            int allAGCount = 0;
            for (AccountGroup ag : allAGIter) {
                allAGCount++;
                System.out.printf(
                    "  %d. ID: %s, Account ID: %s, Group ID: %s%n",
                    allAGCount, ag.getId(), ag.getAccountId(), ag.getGroupId());
            }
            System.out.printf("Total account groups found: %d%n", allAGCount);

            System.out.println("\nAccountGroups for our test account:");
            var forAccountIter = client.accountsGroups().list("accountid:?", accountID);
            int accCount = 0;
            for (AccountGroup ag : forAccountIter) {
                accCount++;
                System.out.printf(
                    "  %d. ID: %s, Account ID: %s, Group ID: %s%n",
                    accCount, ag.getId(), ag.getAccountId(), ag.getGroupId());
            }
            System.out.printf("Account groups for test account: %d%n", accCount);

            // 3. DELETE links
            System.out.println("\n3. DELETE - Removing account group relationships...");
            client.accountsGroups().delete(createdAG1.getId());
            System.out.printf("Deleted AccountGroup 1 (ID: %s)%n", createdAG1.getId());
            client.accountsGroups().delete(createdAG2.getId());
            System.out.printf("Deleted AccountGroup 2 (ID: %s)%n", createdAG2.getId());

            // Verify deletion
            System.out.println("\nVerifying deletion by listing account groups for our test account...");
            var verifyIter = client.accountsGroups().list("accountid:?", accountID);
            int remaining = 0;
            for (AccountGroup ignored : verifyIter) {
                remaining++;
            }
            System.out.printf("Account groups remaining after deletion: %d%n", remaining);

            // CLEANUP
            System.out.println("\nCleanup: Removing prerequisite resources...");
            try {
                client.accounts().delete(accountID);
                System.out.printf("Deleted Account: %s%n", account.getEmail());
            } catch (Exception e) {
                System.out.printf("Warning: failed to delete account %s: %s%n", accountID, e.getMessage());
            }
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

            System.out.println("\n=== AccountsGroups CRUD Example Completed Successfully! ===");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
