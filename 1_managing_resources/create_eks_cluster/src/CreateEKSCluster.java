
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

public class CreateEKSCluster {
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
            var opts = new ClientOptions();
            var client = new Client(apiAccessKey,apiSecretKey, opts);

            // Define an EKS cluster
            var certificateAuthority = "-----BEGIN CERTIFICATE-----\n" +
            "MIICpjCCAY4CCQCYJT6s+JVzSTANBgkqhkiG9w0BAQsFADAVMRMwEQYDVQQDDApr\n" +
            "dWJlcm5ldGVzMB4XDTIwMDcxNTE0MjgzN1oXDTIxMDcxNTE0MjgzN1owFTETMBEG\n" +
            "A1UEAwwKa3ViZXJuZXRlczCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEB\n" +
            "AJtI1pfbqy65FihJ6SadnrdDw6IjGJo7icoxcDR9Tn0Ljz7a7CO4VgpDfs/X4ljG\n" +
            "LkGTqDqLXZ61+lssfaUwMFA61McthTZfd6rYLBcxWFmaVqvUL0tguTrrUPuegHXv\n" +
            "IBs827JSH43BXqLgvZCaWYb5PtD+CI9F9bOBm+M+BUufrdS6gUkTqipZdgC8sl8E\n" +
            "SvixPjKPRu4EnBE/cPEMvYzkSpjixs87WKGPR0FM+6SQVr6o14Fs3QNlcElBAi27\n" +
            "U7XL+an/Fj0osEZGDhJ1u/TmmWlW7RopE1YS8gpVxBzQkBmeUU05a9l1f4L8j45E\n" +
            "TFuF5daWkNLZFO08u1GxnlsCAwEAATANBgkqhkiG9w0BAQsFAAOCAQEANyPDqSUZ\n" +
            "nLiOVGg4OWPmXJy3tk7+Mb6j/xOFFKoKfrXJVUB1F5IDMD673ozkhKyNcqfFOEeZ\n" +
            "+E3WC2/CxxwkJfEUrtij8qWMnafvDnaPan86jNkZsz9zvxphqdeA0hsYZF5tPLWT\n" +
            "Sk8uIHuRA36mYhzCrXQ7dhLn4mC147LRcZ73CTi4j4bNyGtCYgYE+Ta1pcrREIHp\n" +
            "PMiZH+tzwXAWeVKh3foHTjeXKAgXhg3Lbqxn6Uq3cejraUMi9b489KKPOlcaQ7wX\n" +
            "FPkubmy3vrhgJySlrfBDtCgFDwSosLniZU479S3oZBsKgPgLe3ELzAw1vLcuIgmd\n" +
            "JrXnKV7Z4r9uWg==\n" +
            "-----END CERTIFICATE-----\n";

            var cluster = new AmazonEKS();
            cluster.setName("Example EKS Cluster");
            cluster.setEndpoint("https://A1ADBDD0AE833267869C6ED0476D6B41.gr7.us-east-2.eks.amazonaws.com");
            cluster.setAccessKey("AKIAIOSFODNN7EXAMPLE");
            cluster.setSecretAccessKey("wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY");
            cluster.setRegion("us-east-1");
            cluster.setClusterName("example");
            cluster.setRoleArn("arn:aws:iam::000000000000:role/RoleName");
            cluster.setHealthcheckNamespace("default");
            cluster.setCertificateAuthority(certificateAuthority);
            
            // Create the cluster
            var response = client.resources()
                .withDeadlineAfter(30, TimeUnit.SECONDS)
                .create(cluster);

            System.out.println("Successfully created EKS cluster.");
            System.out.printf("    ID: %s\n", response.getResource().getId());
            System.out.printf("  Name: %s\n", response.getResource().getName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        
    }
}