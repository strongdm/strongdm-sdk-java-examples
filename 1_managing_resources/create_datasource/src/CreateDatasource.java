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

public class CreateDatasource {
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
            // Set `PortOverride` to `-1` to auto-allocate an available port.
            postgres.setPortOverride(19999);
            // May be set to one of the ResourceIPAllocationMode constants to select between VNM,
            // loopback, or default allocation. If not set, will behave as if configured for
            // 'default'.
            // For more details on Virtual Networking Mode see documentation here:
            // https://docs.strongdm.com/admin/clients/client-networking/virtual-networking-mode
            postgres.setBindInterface(ResourceIPAllocationMode.LOOPBACK);
            postgres.setTags(java.util.Map.of(
            "env", "example"));

            // You can also specify an explicit loopback IP address to bind to if Loopback IP Ranges
            // are enabled as documented here:
            // https://docs.strongdm.com/admin/clients/client-networking/loopback-ip-ranges
            postgres.setBindInterface("127.0.0.2");

            // ...Or if your organization has Virtual Networking Mode enabled,
            // you may configure the resource's bind interface
            // to automatically get an IP allocated upon creation:
            postgres.setBindInterface(ResourceIPAllocationMode.VNM);

            // ...Or specify an explicit VNM IP address to bind to...
            postgres.setBindInterface("100.64.0.1");

            // Your organization can default either to 'loopback' or 'vnm', and
            // ResourceIPAllocationModeDefault will honor that.
            postgres.setBindInterface(ResourceIPAllocationMode.DEFAULT);
 
            // Create the Datasource
            var response = client.resources()
                .withDeadlineAfter(30, TimeUnit.SECONDS)
                .create(postgres);

            System.out.println("Successfully created Postgres datasource.");
            System.out.printf("\tID: %s\n", response.getResource().getId());
            System.out.printf("\tName: %s\n", response.getResource().getName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        
    }
}
