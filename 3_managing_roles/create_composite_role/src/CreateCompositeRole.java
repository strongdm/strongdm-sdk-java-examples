
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
import java.util.concurrent.TimeUnit;

import com.strongdm.api.v1.*;

public class CreateCompositeRole {
    public static void main(String[] args) {
        // Load the SDM API keys from the environment.
        // If these values are not set in your environment,
        // please follow the documentation here:
        // https://www.strongdm.com/docs/admin-guide/api-credentials/
        var apiAccessKey = System.getenv("SDM_API_ACCESS_KEY");
        var apiSecretKey = System.getenv("SDM_API_SECRET_KEY");
        if (apiAccessKey == null || apiSecretKey == null) {
            System.out.println("SDM_API_ACCESS_KEY and SDM_API_SECRET_KEY must be provided");
            return;
        }

        try {
            // Create the SDM Client
            var opts = new ClientOptions().withHostAndPort("api.strongdmdev.com", 443);
            var client = new Client(apiAccessKey, apiSecretKey, opts);

            // Define a composite role
            var composite = new Role();
            composite.setName("Example Composite Role");
            composite.setComposite(true);

            // Create the composite role
            var compositeResponse = client.roles().create(composite).getRole();

            System.out.println("Successfully created composite role.");
            System.out.printf("    ID: %s\n", compositeResponse.getId());
            System.out.printf("  Name: %s\n", compositeResponse.getName());

            // Define a role
            var role = new Role();
            role.setName("Example Role");

            // Create the role
            var roleResponse = client.roles().create(role).getRole();

            System.out.println("Successfully created role.");
            System.out.printf("    ID: %s\n", roleResponse.getId());
            System.out.printf("  Name: %s\n", roleResponse.getName());

            // Define the role attachment
            var attachment = new RoleAttachment();
            attachment.setCompositeRoleId(compositeResponse.getId());
            attachment.setAttachedRoleId(roleResponse.getId());
            
            // Create the role attachment
            var attachmentResponse = client.roleAttachments().create(attachment).getRoleAttachment();

            System.out.println("Successfully created attachment.");
            System.out.printf("    ID: %s\n", attachmentResponse.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}