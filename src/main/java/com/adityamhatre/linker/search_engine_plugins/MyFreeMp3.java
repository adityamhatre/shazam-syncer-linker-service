package com.adityamhatre.linker.search_engine_plugins;

import com.adityamhatre.linker.search_engine.SearchEngine;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class MyFreeMp3 extends SearchEngine {
	@Value("${myfreemp3.link.regex:window\\.open\\('(?<myLink>.*)',.*);}")
	private String myPattern;

	private MyFreeMp3cInterface myFreeMp3cInterface;

	public MyFreeMp3() {
		final OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();

		final OkHttpClient builtClient = okHttpClientBuilder.addInterceptor(chain -> {
			Request request = chain.request();
			okhttp3.Response response = chain.proceed(request);
			if (response.code() == 200) {
				String responseBody = response.body() != null ? response.body().string() : null;
				if (responseBody != null) {
					responseBody = responseBody.substring(responseBody.indexOf("(") + 1, responseBody.length() - 2);
					return response.newBuilder().body(ResponseBody.create(null, responseBody)).build();
				}
			}
			return response;
		}).build();

		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(APIConfiguration.API_BASE_URL)
				.client(builtClient)
				.addConverterFactory(ScalarsConverterFactory.create())
				.build();
		myFreeMp3cInterface = retrofit.create(MyFreeMp3cInterface.class);
	}

	@Override
	public String searchLinkForSong(String songName) {
		int retry = 3;
		JsonObject songObject;
		do {
			songObject = performSearchOnSearchEngine(songName);
			if (songObject == null || !(songObject.has("owner_id") && songObject.has("id"))) {
				retry--;
			} else {
				break;
			}
		} while (retry >= 0);
		if (songObject == null) {
			return null;
		}
		String safeLinkRequestUrl = String.format("https://mysafeurls.com/api/get_song.php?id=%s:%s",
				getLink(songObject.get("owner_id").getAsInt()),
				getLink(songObject.get("id").getAsInt()));

		String link;
		retry = 3;
		do {
			link = getSafeLinkFromPHP(safeLinkRequestUrl);
			if (link == null) {
				retry--;
			} else {
				break;
			}
		} while (retry >= 0);

		return link;
	}


	private JsonObject performSearchOnSearchEngine(String songName) {
		String payload = String.format("q=%s&page=0", songName.replaceAll(" ", "+"));
		Map<String, String> headerMap = new HashMap<>();
		headerMap.put("authority", "myfreemp3c.com");
		headerMap.put("pragma", "no-cache");
		headerMap.put("cache-control", "no-cache");
		headerMap.put("origin", "https,//myfreemp3c.com");
		headerMap.put("x-requested-with", "XMLHttpRequest");
		headerMap.put("dnt", "1");
		headerMap.put("content-type", "application/x-www-form-urlencoded; charset=UTF-8");
		headerMap.put("sec-fetch-site", "same-origin");
		headerMap.put("sec-fetch-mode", "cors");
		headerMap.put("referer", "https,//myfreemp3c.com/");
		headerMap.put("accept-language", "en-US,en;q=0.9,hi;q=0.8,mr;q=0.7");
		headerMap.put("cookie", "__cfduid=dbbc905772a004b0895705f8f313d73b61577790088; musicLang=en");

		Call<String> call = myFreeMp3cInterface.getSongSearchResponse("jQuery213025636066715463635_1577790089580", payload, headerMap);
		try {
			Response<String> response = call.execute();
			if (!response.isSuccessful()) {
				return null;
			}
			String s = response.body();
			if (s == null) {
				log.error("no link found");
				return null;
			}
			JsonObject jsonResponse = JsonParser.parseString(s).getAsJsonObject();
			if (!jsonResponse.get("response").isJsonArray() || jsonResponse.get("response").getAsJsonArray().size() == 0) {
				log.error("no link found");
				return null;
			}
			jsonResponse.get("response").getAsJsonArray().remove(0);
			return jsonResponse.get("response").getAsJsonArray().get(0).getAsJsonObject();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String getLink(int t) {
		String[] u = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "M", "N", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "a", "b", "c", "d", "e", "f", "g", "h", "j", "k", "m", "n", "p", "q", "r", "s", "t", "u", "v", "x", "y", "z", "1", "2", "3"};
		int length = u.length;
		StringBuilder e = new StringBuilder();
		if (t == 0) {
			return e.append(u[0]).toString();
		}
		if (t < 0) {
			t *= -1;
			e.append("-");
		}
		do {
			int val = t % length;
			t = t / length;
			e.append(u[val]);
		} while (t > 0);
		return e.toString();

	}


	private String getSafeLinkFromPHP(String safeLinkRequestUrl) {
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(safeLinkRequestUrl);
		if (myPattern == null) {
			myPattern = "window\\.open\\('(?<myLink>.*)',.*\\);";
		}
		try {
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			Pattern pattern = Pattern.compile(myPattern);
			Matcher matcher = pattern.matcher(EntityUtils.toString(entity));

			if (matcher.find()) {
				return matcher.group("myLink");
			} else {
				log.error("dafaq ?");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;

	}


	interface APIConfiguration {
		String API_BASE_URL = "https://myfreemp3c.com/";
	}

	interface MyFreeMp3cInterface {
		@POST("api/search.php")
		Call<String> getSongSearchResponse(@Query("callback") String jQueryNumber, @Body String payload, @HeaderMap Map<String, String> headerMap);
	}

}
