package com.adityamhatre.linker.kafka;

import dto.SongDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaProducer {
	private final
	KafkaTemplate<String, SongDTO> kafkaProducer;

	public KafkaProducer(KafkaTemplate<String, SongDTO> kafkaProducer) {
		this.kafkaProducer = kafkaProducer;
	}

	public void produceOnFoundLink(SongDTO songDTO) {
		this.kafkaProducer.send(Topics.TopicConstants.ON_FOUND_SONG_LINK, songDTO);
	}
}
