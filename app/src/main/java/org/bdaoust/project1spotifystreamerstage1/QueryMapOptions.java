package org.bdaoust.project1spotifystreamerstage1;

import java.lang.annotation.Annotation;
import java.util.HashMap;

import retrofit.http.QueryMap;

public class QueryMapOptions extends HashMap<String,String> implements QueryMap{

    @Override
    public boolean encodeNames() {
        return false;
    }

    @Override
    public boolean encodeValues() {
        return false;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }
}
