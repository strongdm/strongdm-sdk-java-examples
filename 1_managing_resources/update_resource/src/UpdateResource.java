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

import com.strongdm.api.*;

public class UpdateResource {
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
            postgres.setTags(java.util.Map.of(
            "env", "example"));
            
            // Create the Datasource
            var createResponse = client.resources()
                .withDeadlineAfter(30, TimeUnit.SECONDS)
                .create(postgres);

            System.out.println("Successfully created Postgres datasource.");
            System.out.printf("\tID: %s\n", createResponse.getResource().getId());
            System.out.printf("\tName: %s\n", createResponse.getResource().getName());

            // Load the Datasource to update
            var getResponse = client.resources()
                .withDeadlineAfter(30, TimeUnit.SECONDS)
                .get(createResponse.getResource().getId());
            var updatedPostgresDatasource = getResponse.getResource();

            // Update the fields to change
            updatedPostgresDatasource.setName("Example Name Updated");

            // If your organization has Virtual Networking Mode enabled,
            // you can automatically allocate an IP to that resource via the 
            // ResourceIPAllocationMode.VNM constant...
            updatedPostgresDatasource.setBindInterface(ResourceIPAllocationMode.VNM);

            // ...Or fallback to whatever the default behavior is for your organization...
            updatedPostgresDatasource.setBindInterface(ResourceIPAllocationMode.DEFAULT);

            // ...Or if there is a specific IP to bind to, you can specify it directly.
            // For more details on Virtual Networking Mode see documentation here:
            // https://docs.strongdm.com/admin/clients/client-networking/virtual-networking-mode
            updatedPostgresDatasource.setBindInterface("127.0.0.1");

            var postgresResource = (Postgres)updatedPostgresDatasource;
            // Update `PortOverride` to `-1` to auto-allocate a different available port.
            postgresResource.setPortOverride(-1);

            // Update the Datasource
            var updateResponse = client.resources()
                .withDeadlineAfter(30, TimeUnit.SECONDS)
                .update(updatedPostgresDatasource);

            var updatedResource = updateResponse.getResource();
            var newPortOverride = ((Postgres)updatedResource).getPortOverride();

            System.out.println("Successfully updated Postgres datasource.");
            System.out.printf("\tID: %s\n", updatedResource.getId());
            System.out.printf("\tName: %s\n", updatedResource.getName());
          	System.out.printf("\tBindInterface: %s\n", updatedResource.getBindInterface());
            System.out.printf("\tPortOverride: %s\n", newPortOverride);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
