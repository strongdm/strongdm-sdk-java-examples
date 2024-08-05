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

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import com.strongdm.api.*;


public class FindPolicy {
    private static Policy newPolicy(String name, String description, String policy) {
        var p = new Policy();
        p.setName(name);
        p.setDescription(description);
        p.setPolicy(policy);
        return p;
    }

    public static List<Policy> EXAMPLE_POLICIES = new ArrayList<>(Arrays.asList(
    newPolicy(
        "default-permit-policy",
        "a default permit policy",
        "permit (principal, action, resource);"
    ),
    newPolicy(
        "permit-sql-select-policy",
        "a permit sql select policy",
        "permit (principal, action == SQL::Action::\"select\", resource == Postgres::Database::\"*\");"
    ),
    newPolicy(
        "default-forbid-policy",
        "a default forbid policy",
        "forbid (principal, action, resource);"
    ),
    newPolicy(
        "forbid-connect-policy",
        "a forbid connect policy",
        "forbid (principal, action == StrongDM::Action::\"connect\", resource);"
    ),
    newPolicy(
        "forbid-sql-delete-policy",
        "a forbid delete policy on all resources",
        "forbid (principal, action == SQL::Action::\"delete\", resource == Postgres::Database::\"*\");"
      )
));

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

        var policiesToCleanup = new ArrayList<Policy>();


        try {
            // Create the SDM Client
            var opts = new ClientOptions();
            var client = new Client(apiAccessKey, apiSecretKey, opts);

            // Create our example policies
            for (Policy p : EXAMPLE_POLICIES) {
                var resp = client.policies().create(p);
                policiesToCleanup.add(resp.getPolicy());
            }

            System.out.println("Finding all Policies with a name containing 'sql'");
            var policies = client.policies().list("name:*sql*");
            for (Policy p : policies) {
                System.out.printf("\tID: %s\tName:%s\n", p.getId(), p.getName());
            }

            System.out.println("Finding all Policies that forbid");
            policies = client.policies().list("policy:forbid*");
            for (Policy p : policies) {
                System.out.printf("\tID: %s\tName:%s\n", p.getId(), p.getName());
            }

            // Try to cleanup the policies we created.
            for (Policy p : policiesToCleanup) {
                client.policies().delete(p.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
