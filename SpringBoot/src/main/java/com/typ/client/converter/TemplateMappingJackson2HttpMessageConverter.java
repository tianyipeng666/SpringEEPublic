package com.typ.client.converter;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;
import java.util.List;

public class TemplateMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {
    public TemplateMappingJackson2HttpMessageConverter(){
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.valueOf("text/json;charset=UTF-8"));
        setSupportedMediaTypes(mediaTypes);
    }
}