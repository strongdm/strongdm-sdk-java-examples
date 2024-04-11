
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
import java.util.concurrent.TimeUnit;
import java.time.Duration;

import com.strongdm.api.Client;
import com.strongdm.api.ClientOptions;
import com.strongdm.api.Token;

public class RotateToken {
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

            // Define a Token
            var token = new Token();
            token.setName("example-token-name");
            token.setDuration(Duration.ofHours(1));
            token.setPermissions(new List<String>("role:list"));
            token.setAccountType("api");
            
            // Create the Token
            var response = client.accounts().create(token);
            var tokenResponse = (Token)response.getAccount();

            System.out.println("Successfully created token.");
            System.out.printf("\tID: %s\n", tokenResponse.getId());
            System.out.printf("\tAccess Key: %s\n", response.getAccessKey());
            System.out.printf("\tSecret Key: %s\n", response.getSecretKey());

            // Find the Token by Name
            var listResponse = client.accounts().list("name:" + tokenResponse.getName());
            List<Account> accountsList = new List<>();
            for (Account account : listResponse) { 
                accountsList.add(account);
            }
            if (accountsList.size() != 1) {
                throw new IllegalStateException("only expected one token to be returned");
            }
            var oldToken = (Token)accountsList.get(0);

            // Update the old token name
            var tok = new Token();
            tok.setId(oldToken.getId());
            tok.setName(oldToken.getName() + "-deprecated");
            tok.setDuration(Duration.ofHours(1));
            tok.setPermissions(oldToken.getPermissions());
            tok.setAccountType(oldToken.getAccountType());

            var updateResponse = client.accounts().update(tok);
            var updatedOldToken = (Token)updateResponse.getAccount();
            System.out.println("Successfully updated old token name.");

            // Create New Token
            tok.setName(oldToken.getName());
            tok.setId(null);
            var createResponse = client.accounts().create(tok);
            var newToken = (Token)createResponse.getAccount();
            System.out.println("Successfully created new token.");

            // Delete the old token
            client.accounts().delete(updatedOldToken.getId());
            System.out.println("Successfully deleted old token.");


            System.out.println("Successfully rotated token.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        
    }
}