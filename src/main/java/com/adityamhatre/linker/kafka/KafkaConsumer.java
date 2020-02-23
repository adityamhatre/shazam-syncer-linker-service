package com.adityamhatre.linker.kafka;

import com.adityamhatre.linker.service.LinkSearchService;
import dto.SongDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.adityamhatre.linker.kafka.Topics.TopicConstants.ON_RECEIVE_NEW_SONG;

@Component
@Slf4j
public class KafkaConsumer {

	private final LinkSearchService linkSearchService;

	public KafkaConsumer(LinkSearchService linkSearchService) {
		this.linkSearchService = linkSearchService;
	}

	@KafkaListener(topics = ON_RECEIVE_NEW_SONG, groupId = "linker", containerFactory = "kafkaListenerContainerFactory", concurrency = "5")
	void writeSong(ConsumerRecord<String, SongDTO> record) {
		log.info("Got new value on \"{}\" channel", ON_RECEIVE_NEW_SONG);
		SongDTO songDTO = record.value();
		linkSearchService.searchForSong(songDTO);
	}

}
