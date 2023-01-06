package com.vauthenticator.cache


interface CacheContentConverter<O> {
    fun getObjectFromCacheContentFor(cacheContent: String): O
    fun loadableContentIntoCacheFor(source: O): String
}