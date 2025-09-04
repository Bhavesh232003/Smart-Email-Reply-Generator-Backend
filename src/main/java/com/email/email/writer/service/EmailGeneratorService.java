package com.email.email.writer.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.email.email.writer.model.EmailRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class EmailGeneratorService {
	
	private final WebClient webclient;
	private final DataMaskingService dataMaskingService; // Already injected, which is great

	@Value("${gemini.api.url}")
	private String geminiApiUrl;

	@Value("${gemini.api.key}")
	private String geminiApiKey;
	
	public EmailGeneratorService(WebClient.Builder webclientBuilder, DataMaskingService dataMaskingService) {
		this.webclient = webclientBuilder.build();
		this.dataMaskingService = dataMaskingService;
	}
	
	public String generateEmailReply(EmailRequest emailRequest) {
		// 1. MASK the original email content first
        MaskedData maskedData = dataMaskingService.mask(emailRequest.getContent());
        //System.out.println(maskedData);
		// 2. Build the prompt using the MASKED content
		String prompt = buildPrompt(maskedData.maskedText(), emailRequest.getTone());
        
		// Craft a request body
		Map<String,Object> requestBody=Map.of(
				"contents",new Object[] {
						Map.of("parts", new Object[] {
								Map.of("text", prompt)
						})
				}
		);

		// 3. Do request and get a response as before
		String rawApiResponse = webclient.post()
				.uri(geminiApiUrl + geminiApiKey)
				.header("Content-Type","application/json")
				.bodyValue(requestBody)
				.retrieve()
				.bodyToMono(String.class)
				.block();
		
		// 4. Extract the text content from the API's JSON response
        String llmResponseText = extractResponseContent(rawApiResponse);

        // 5. UNMASK the response from the LLM before returning it to the user
        String finalResponse = dataMaskingService.unmask(llmResponseText, maskedData.replacements());

		// Return the final, unmasked response
		return finalResponse;
        //return llmResponseText;
	}

	private String extractResponseContent(String response) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.readTree(response);
			return rootNode.path("candidates")
					.get(0).path("content")
					.path("parts").get(0)
					.path("text").asText();
		}
		catch(Exception e) {
			return "Error Processing Request " + e.getMessage();
		}
	}

    // I've modified this method to take the content and tone directly
    // This makes the logic cleaner and more reusable.
	private String buildPrompt(String content, String tone) {
		StringBuilder prompt = new StringBuilder();
		prompt.append("Generate a professional email reply for the following email content. Please don't generate a subject line.");
		if(tone != null && !tone.isEmpty()) {
			prompt.append("Use a ").append(tone).append(" tone.");
		}
		prompt.append("\n Original email: \n").append(content);
		return prompt.toString();
	}
}