package com.luckyaf.kommonmaster

data class ZhihuDaily(
        val date: String,
        val stories: List<Story>,
        val top_stories: List<TopStory>
)