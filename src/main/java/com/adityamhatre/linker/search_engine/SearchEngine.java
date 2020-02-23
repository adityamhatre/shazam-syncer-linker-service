package com.adityamhatre.linker.search_engine;

public abstract class SearchEngine {
	abstract public String searchLinkForSong(String songName);

	public SearchEngine() {
		SearchEngines.searchEngines.add(this);
	}
}

