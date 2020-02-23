package com.adityamhatre.linker.service;

import dto.UserDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@Component
public class DBService {
	@Value("${database-service:http://database-service:8080}")
	private String databaseServiceURL;

	private DB db;

	@PostConstruct
	void init() {
		this.db = new Retrofit.Builder()
				.baseUrl(databaseServiceURL)
				.addConverterFactory(JacksonConverterFactory.create())
				.build()
				.create(DB.class);
	}

	Map<String, Object> doesSongLinkExists(String songShazamId) {
		Call<Map<String, Object>> call = db.doesSongLinkExists(songShazamId);
		try {
			Response<Map<String, Object>> response = call.execute();
			if (response.isSuccessful()) {
				Map<String, Object> result = response.body();
				if (result != null) {
					return result;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Collections.emptyMap();
	}
}

interface DB {
	@GET("/api//songs/{songShazamId}/link")
	Call<Map<String, Object>> doesSongLinkExists(@Path("songShazamId") String songShazamId);
}

