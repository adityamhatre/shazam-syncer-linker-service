package com.adityamhatre.linker.service;

import com.adityamhatre.linker.kafka.KafkaProducer;
import com.adityamhatre.linker.search_engine.SearchEngine;
import com.adityamhatre.linker.search_engine.SearchEngines;
import dto.SongDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class LinkSearchService {
	private final KafkaProducer kafkaProducer;
	private final DBService dbService;


	LinkSearchService(KafkaProducer kafkaProducer, DBService dbService) {
		this.kafkaProducer = kafkaProducer;
		this.dbService = dbService;
	}

	public void searchForSong(SongDTO songDTO) {
		Map<String, Object> responseMap = this.dbService.doesSongLinkExists(songDTO.getShazamSongId());
		if ((boolean) responseMap.getOrDefault("link_exists", false)) {
			log.info("Link already saved in database for song {}", songDTO.getSongName());
			songDTO.setLinkFound(true);
			songDTO.setLink((String) responseMap.get("link"));
			this.kafkaProducer.produceOnFoundLink(songDTO);
			return;
		}

		for (SearchEngine searchEngine : SearchEngines.searchEngines) {
			String link = searchEngine.searchLinkForSong(songDTO.getSongName());
			if (link != null) {
				log.info("Link found for song {}", songDTO.getSongName());
				songDTO.setLinkFound(true);
				songDTO.setLink(link);
				this.kafkaProducer.produceOnFoundLink(songDTO);
				return;
			}
		}
		log.error("Cannot find any link for {}", songDTO.getSongName());
	}
}
