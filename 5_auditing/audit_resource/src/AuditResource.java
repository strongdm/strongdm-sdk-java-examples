// Copyright 2023 StrongDM Inc
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
import com.strongdm.api.*;
import java.util.Date;

public class AuditResource {
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

            Date start = new Date();
            /* *
            Set up some audit records to read
            * */
            System.out.println("Creating, renaming, and deleting a test resource...");

            Redis redis = new Redis();
            redis.setName("example-redis");
            redis.setHostname("example-redis");
            redis.setUsername("example-username");
            ResourceCreateResponse resp = client.resources().create(redis);

            String resourceId = resp.getResource().getId();

            Thread.sleep(1000);
            Date createdAt = new Date();

            resp.getResource().setName("example-redis-renamed");
            client.resources().update(resp.getResource());

            Thread.sleep(1000);
            Date renamedAt = new Date();

            client.resources().delete(resourceId);

            Thread.sleep(1000);
            Date deletedAt = new Date();

            // Audit records may take a few seconds to be processed.
            Thread.sleep(3000);

            try {
                client.snapshotAt(start).resources().get(resourceId);
            } catch (NotFoundException e){
                System.out.printf("Attempting to retrieve resource before creation (%s) returned Not Found\n", start);
            }

            ResourceGetResponse getResp = client.snapshotAt(createdAt).resources().get(resourceId);
            System.out.printf("Resource name after creation (%s): %s\n", createdAt, getResp.getResource().getName()); // example-redis

            getResp = client.snapshotAt(renamedAt).resources().get(resourceId);
            System.out.printf("Resource name after rename (%s): %s\n", createdAt, getResp.getResource().getName()); // example-redis-renamed

            try {
                client.snapshotAt(deletedAt).resources().get(resourceId);
            } catch (NotFoundException e){
                System.out.printf("Attempting to retrieve resource after deletion (%s) returned Not Found\n", start);
            }

            System.out.println("Full history of the resource:");
            Iterable<ResourceHistory> history = client.resourcesHistory().list("id:?", resourceId);
            for (ResourceHistory h : history) {
                ActivityGetResponse activity = client.activities().get(h.getActivityId());
                System.out.println(activity.getActivity().getDescription()); // created, updated, deleted resource; in order
            }
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }
}
