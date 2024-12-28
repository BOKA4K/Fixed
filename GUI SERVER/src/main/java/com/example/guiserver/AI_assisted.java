package com.example.guiserver;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
public class AI_assisted {

    public String getRecommendedTechnician(String problemDescription, HashMap<String, String> techniciansData) throws Exception {
        String promptMessage = createPromptMessage(problemDescription, techniciansData);
        String jsonInputString = createJsonRequestBody(promptMessage);
        String apiUrl = "http://localhost:11434/v1/chat/completions";

        String response = sendPostRequest(apiUrl, jsonInputString);

        if (response != null) {
            return parseResponse(response);
        } else {
            throw new Exception("Error in response");
        }
    }

    // Method to create the prompt message to send to the API
    private String createPromptMessage(String problemDescription, HashMap<String, String> techniciansData) {
        StringBuilder techniciansString = new StringBuilder("Technicians:\n");
        for (Map.Entry<String, String> entry : techniciansData.entrySet()) {
            techniciansString.append(" email("+entry.getKey()).append(")  skills(").append(entry.getValue()).append("). \n ----- ");
        }

        return problemDescription + "\n\n" + techniciansString.toString() + "\n\n" +
                "Based on the above, which technician is best suited for the problem described?-- super important only return the email and oly one email.";
    }

    // Method to create the JSON request body
    private String createJsonRequestBody(String promptMessage) {
        String escapedPrompt = promptMessage.replace("\"", "\\\"").replace("\n", "\\n");
        System.out.println(escapedPrompt);
        return "{\n" +
                "  \"model\": \"llama3.2:1b\",\n" +
                "  \"messages\": [\n" +
                "    {\"role\": \"user\", \"content\": \"" + escapedPrompt + "\"}\n" +
                "  ]\n" +
                "}";
    }

    // Method to send a POST request to the API
    private String sendPostRequest(String apiUrl, String jsonInputString) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set up the connection
            // Write the input JSON string to the request body
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get the response code
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // Read the response
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return readResponse(connection);
            } else {
                handleErrorResponse(connection);
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to read the response from the API
    private String readResponse(HttpURLConnection connection) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            return response.toString();
        }
    }

    // Method to handle error response from the API
    private void handleErrorResponse(HttpURLConnection connection) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "utf-8"))) {
            String inputLine;
            StringBuilder errorResponse = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                errorResponse.append(inputLine);
            }
            System.out.println("Error Response: " + errorResponse.toString());
        }
    }

    // Method to parse the response JSON and extract the assistant's recommendation
    private String parseResponse(String response) {
        JSONObject jsonResponse = new JSONObject(response);
        return jsonResponse
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");
    }
}
