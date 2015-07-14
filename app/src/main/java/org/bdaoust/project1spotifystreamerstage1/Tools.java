package org.bdaoust.project1spotifystreamerstage1;

import java.util.List;

import kaaes.spotify.webapi.android.models.Image;

public class Tools {

    public static Image findPreferedSizeImage(List<Image> imageList, int preferedSize){
        Image image;
        for(int i=0; i< imageList.size(); i++){
            if(imageList.get(i).width == preferedSize && imageList.get(i).height == preferedSize){
                image = imageList.get(i);
                return image;
            }
        }
        if(imageList.size() > 0) {
            return imageList.get(imageList.size() - 1);
        }
        else {
            return null;
        }
    }
}
