package br.com.zup;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.stepfunctions.AWSStepFunctions;
import com.amazonaws.services.stepfunctions.AWSStepFunctionsClient;
import com.amazonaws.services.stepfunctions.AWSStepFunctionsClientBuilder;
import com.amazonaws.services.stepfunctions.model.*;
import org.json.JSONObject;

import java.util.List;

public class StepFunctionsSample {

    public static void main(String[] args) {
        listExecutions();
    }

    public static void execute() {
        ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
        try {
            credentialsProvider.getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException("Cannot load the credentials", e);
        }

        var awsStepFunctions = AWSStepFunctionsClient.builder().build();

        JSONObject sfnInput = new JSONObject();
        sfnInput.put("name", "Hello");

        StartExecutionRequest startExecutionRequest = new StartExecutionRequest()
                .withStateMachineArn("arn:aws:states:sa-east-1:849021383803:stateMachine:sayHelloAndGoodbye")
                .withInput(sfnInput.toString());
        StartExecutionResult result = awsStepFunctions.startExecution(startExecutionRequest);

        System.out.println(result.toString());
    }

    public static void listExecutions() {
        ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
        try {
            credentialsProvider.getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException("Cannot load the credentials", e);
        }

        Regions region = Regions.SA_EAST_1;
        AWSStepFunctions sfnClient = AWSStepFunctionsClientBuilder.standard()
                .withCredentials(credentialsProvider)
                .withRegion(region)
                .build();

        System.out.println("===========================================");
        System.out.println("Getting Started with Amazon Step Functions");
        System.out.println("===========================================\n");

        try {
            System.out.println("Listing state machines");
            ListStateMachinesResult listStateMachinesResult = sfnClient.
                    listStateMachines(new ListStateMachinesRequest());

            List<StateMachineListItem> stateMachines = listStateMachinesResult
                    .getStateMachines();

            System.out.println("State machines count: " + stateMachines.size());
            if (!stateMachines.isEmpty()) {
                stateMachines.forEach(sm -> {
                    System.out.println("\t- Name: " + sm.getName());
                    System.out.println("\t- Arn: " + sm.getStateMachineArn());


                    ListExecutionsRequest listRequest = new ListExecutionsRequest().withStateMachineArn(sm
                            .getStateMachineArn());
                    ListExecutionsResult listExecutionsResult = sfnClient.listExecutions(listRequest);
                    List<ExecutionListItem> executions = listExecutionsResult.getExecutions();

                    System.out.println("\t- Total: " + executions.size());
                    executions.forEach(ex -> {
                        System.out.println("\t\t-Start: " + ex.getStartDate());
                        System.out.println("\t\t-Stop: " + ex.getStopDate());
                        System.out.println("\t\t-Name: " + ex.getName());
                        System.out.println("\t\t-Status: " + ex.getStatus());
                        System.out.println();
                    });
                });
            }

        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means" +
                    " your request made it to Amazon Step Functions, but was" +
                    " rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means " +
                    "the client encountered a serious internal problem while " +
                    "trying to communicate with Step Functions, such as not " +
                    "being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    }
}
