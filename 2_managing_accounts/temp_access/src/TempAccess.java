
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
import java.util.concurrent.TimeUnit;

import com.strongdm.api.v1.*;

import java.util.Calendar;
import java.util.Date;

public class TempAccess {
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
            var client = new Client(apiAccessKey,apiSecretKey, opts);

            // Define a Postgres Datasource
            var postgres = new Postgres();
            postgres.setName("Example Postgres Datasource");
            postgres.setHostname("example.strongdm.com");
            postgres.setPort(5432);
            postgres.setUsername("example");
            postgres.setPassword("example");
            postgres.setDatabase("example");
            postgres.setPortOverride(19999);
            
            // Create the datasource
            var postgresResponse = client.resources()
                .withDeadlineAfter(30, TimeUnit.SECONDS)
                .create(postgres)
                .getResource();

            System.out.println("Successfully created Postgres datasource.");
            System.out.printf("\tID: %s\n", postgresResponse.getId());
            System.out.printf("\tName: %s\n", postgresResponse.getName());

            // Define a user
            var user = new User();
            user.setEmail("example@example.com");
            user.setFirstName("example");
            user.setLastName("example");
            
            // Create the user
            var userResponse = (User)client.accounts().create(user).getAccount();

            System.out.println("Successfully created user.");
            System.out.printf("\tID: %s\n", userResponse.getId());
            System.out.printf("\tEmail: %s\n", userResponse.getEmail());

            // Define an account grant
            Date now = new Date();
            Calendar validUntil = Calendar.getInstance();
            validUntil.setTime(now);
            validUntil.add(Calendar.HOUR_OF_DAY, 1);

            var grant = new AccountGrant();
            grant.setAccountId(userResponse.getId());
            grant.setResourceId(postgresResponse.getId());
            grant.setValidUntil(validUntil.getTime());

            // Create the grant
            var grantResponse = client.accountGrants().create(grant).getAccountGrant();

            System.out.println("Successfully created grant.");
            System.out.printf("\tID: %s\n", grantResponse.getId());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}