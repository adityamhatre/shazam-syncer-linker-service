package com.adityamhatre.linker.service;

import com.adityamhatre.linker.kafka.KafkaProducer;
import com.adityamhatre.linker.search_engine_plugins.MyFreeMp3;
import dto.SongDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;

@SpringBootTest
@ActiveProfiles("test")
class LinkSearchServiceTest {
	@Autowired DBService dbService;
	private LinkSearchService cut;

	@BeforeEach
	private void init() {
		KafkaProducer kafkaProducer = Mockito.mock(KafkaProducer.class);
		new MyFreeMp3();
		cut = new LinkSearchService(kafkaProducer, dbService);
	}


	@Test
	void searchSong() {
		String songs[] = {"Cleopatra - The Lumineers"};

		cut.searchForSong(SongDTO.builder().shazamSongId("65629131").build());
		/*Arrays.stream(songs).forEach(song -> {
			System.out.print(String.format("\nSong: %s\tLink: ", song));
			cut.searchForSong(SongDTO.builder().shazamSongId("65629131").build());
		});*/

	}

}