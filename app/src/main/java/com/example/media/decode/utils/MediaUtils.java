package com.example.media.decode.utils;

import android.media.MediaExtractor;
import android.media.MediaFormat;

import java.io.IOException;

public class MediaUtils {

    /**
     *
     * @param videoPath
     * @param mini "video/" 或者 "audio/"
     * @return
     * @throws IOException
     */
    public static MediaFormat getMediaFormat(String videoPath, String mini) throws IOException {
        MediaExtractor mediaExtractor = new MediaExtractor();
        mediaExtractor.setDataSource(videoPath);
        int numTracks = mediaExtractor.getTrackCount();
        for (int index = 0; index < numTracks; index++) {
            MediaFormat format = mediaExtractor.getTrackFormat(index);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if(mime.startsWith(mini)){
                mediaExtractor.selectTrack(index);
                return mediaExtractor.getTrackFormat(index);
            }
        }
        return null;
    }

}
