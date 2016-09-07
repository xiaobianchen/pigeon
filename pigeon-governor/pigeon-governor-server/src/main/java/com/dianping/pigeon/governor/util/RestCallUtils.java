package com.dianping.pigeon.governor.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

/**
 * 
 * @author chenchongze
 *
 */
public class RestCallUtils {

	private static final Logger logger = LogManager.getLogger();

	private static final ObjectMapper objectMapper = new ObjectMapper();

	public static <T> T postRestCall(String targetUrl, FormDataMultiPart formDataMultiPart, Class<T> responseType, Integer connectTimeout, Integer readTimeout) {
		T result = null;
		try {
			ClientConfig configuration = new ClientConfig();
			configuration.property(ClientProperties.CONNECT_TIMEOUT, connectTimeout);
			configuration.property(ClientProperties.READ_TIMEOUT, readTimeout);
			Client client = ClientBuilder.newClient(configuration).register(MultiPartFeature.class);//.register(JacksonFeature.class)
			WebTarget target = client.target(targetUrl);

			if(responseType == String.class) {
                return target.request().post(Entity.entity(formDataMultiPart, MediaType.MULTIPART_FORM_DATA), responseType);
            }

			String resStr = target.request().post(Entity.entity(formDataMultiPart, MediaType.MULTIPART_FORM_DATA), String.class);
			result = getBeanFromJson(resStr, responseType);
		} catch (Throwable t) {
			logger.error("Failed to POST: "+ targetUrl, t);
		}

		return result;
	}
	
	public static <T> T getRestCall(String targetUrl, Class<T> responseType){
    	return getRestCall(targetUrl, responseType, 1000, 1000);
	}
	
	public static <T> T getRestCall(String targetUrl, Class<T> responseType, Integer connectTimeout, Integer readTimeout){
		T result = null;

		try {
			ClientConfig configuration = new ClientConfig();
			configuration.property(ClientProperties.CONNECT_TIMEOUT, connectTimeout);
			configuration.property(ClientProperties.READ_TIMEOUT, readTimeout);
			Client client = ClientBuilder.newClient(configuration);
			WebTarget target = client.target(targetUrl);
			if(responseType == String.class) {
                return target.request().get(responseType);
            }
			String resStr = target.request().get(String.class);
			result = getBeanFromJson(resStr, responseType);
		} catch (Throwable t) {
			logger.error("Failed to GET: "+ targetUrl, t);
		}

		return result;
	}
	
	public static <T> T getBeanFromJson(String json, Class<T> responseType) throws Throwable {
    	return objectMapper.readValue(json, responseType);
	}
}
