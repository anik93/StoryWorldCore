package com.storyworld.repository.elastic;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.storyworld.domain.elastic.StoryContent;

public interface StoryContentRepository extends ElasticsearchRepository<StoryContent, String> {

}
