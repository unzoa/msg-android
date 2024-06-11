package io.crim.android.ouicore.utils;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;

public class MediaFileUtil {
    private static String sFileExtensions;
    // Audio
    private static final int FILE_TYPE_MP3 = 1;
    private static final int FILE_TYPE_M4A = 2;
    private static final int FILE_TYPE_WAV = 3;
    private static final int FILE_TYPE_AMR = 4;
    private static final int FILE_TYPE_AWB = 5;
    private static final int FILE_TYPE_WMA = 6;
    private static final int FILE_TYPE_OGG = 7;
    private static final int FIRST_AUDIO_FILE_TYPE = FILE_TYPE_MP3;
    private static final int LAST_AUDIO_FILE_TYPE = FILE_TYPE_OGG;

    // Video
    private static final int FILE_TYPE_MP4 = 21;
    private static final int FILE_TYPE_M4V = 22;
    private static final int FILE_TYPE_3GPP = 23;
    private static final int FILE_TYPE_3GPP2 = 24;
    private static final int FILE_TYPE_WMV = 25;
    private static final int FIRST_VIDEO_FILE_TYPE = FILE_TYPE_MP4;
    private static final int LAST_VIDEO_FILE_TYPE = FILE_TYPE_WMV;

    // Image
    private static final int FILE_TYPE_JPEG = 31;
    private static final int FILE_TYPE_GIF = 32;
    private static final int FILE_TYPE_PNG = 33;
    private static final int FILE_TYPE_BMP = 34;
    private static final int FILE_TYPE_WBMP = 35;
    private static final int FILE_TYPE_WEBP = 36;
    private static final int FIRST_IMAGE_FILE_TYPE = FILE_TYPE_JPEG;
    private static final int LAST_IMAGE_FILE_TYPE = FILE_TYPE_WEBP;

    public static class MediaFileType {

        int fileType;
        public String mimeType;

        MediaFileType(int fileType, String mimeType) {
            this.fileType = fileType;
            this.mimeType = mimeType;
        }
    }

    private static HashMap<String, MediaFileType> sFileTypeMap = new HashMap<>();

    static void addFileType(String extension, int fileType, String mimeType) {
        sFileTypeMap.put(extension, new MediaFileType(fileType, mimeType));
    }

    static {
        addFileType("MP3", FILE_TYPE_MP3, "audio/mpeg");
        addFileType("M4A", FILE_TYPE_M4A, "audio/mp4");
        addFileType("WAV", FILE_TYPE_WAV, "audio/x-wav");
        addFileType("AMR", FILE_TYPE_AMR, "audio/amr");
        addFileType("AWB", FILE_TYPE_AWB, "audio/amr-wb");
        addFileType("WMA", FILE_TYPE_WMA, "audio/x-ms-wma");
        addFileType("OGG", FILE_TYPE_OGG, "application/ogg");

        addFileType("MP4", FILE_TYPE_MP4, "video/mp4");
        addFileType("M4V", FILE_TYPE_M4V, "video/mp4");
        addFileType("3GP", FILE_TYPE_3GPP, "video/3gpp");
        addFileType("3GPP", FILE_TYPE_3GPP, "video/3gpp");
        addFileType("3G2", FILE_TYPE_3GPP2, "video/3gpp2");
        addFileType("3GPP2", FILE_TYPE_3GPP2, "video/3gpp2");
        addFileType("WMV", FILE_TYPE_WMV, "video/x-ms-wmv");
        addFileType("RMVB", FILE_TYPE_WMV, "application/vnd.rn-realmedia-vbr");

        addFileType("JPG", FILE_TYPE_JPEG, "image/jpeg");
        addFileType("JPEG", FILE_TYPE_JPEG, "image/jpeg");
        addFileType("GIF", FILE_TYPE_GIF, "image/gif");
        addFileType("PNG", FILE_TYPE_PNG, "image/png");
        addFileType("BMP", FILE_TYPE_BMP, "image/x-ms-bmp");
        addFileType("WBMP", FILE_TYPE_WBMP, "image/vnd.wap.wbmp");
        addFileType("WEBP", FILE_TYPE_WEBP, "image/webp");


        // compute file extensions list for native Media Scanner
        StringBuilder builder = new StringBuilder();
        Iterator<String> iterator = sFileTypeMap.keySet().iterator();

        while (iterator.hasNext()) {
            if (builder.length() > 0) {
                builder.append(',');
            }
            builder.append(iterator.next());
        }
        sFileExtensions = builder.toString();
    }

    private static final String UNKNOWN_STRING = "<unknown>";

    private static boolean isAudioFileType(int fileType) {
        return ((fileType >= FIRST_AUDIO_FILE_TYPE && fileType <= LAST_AUDIO_FILE_TYPE));
    }

    private static boolean isVideoFileType(int fileType) {
        return (fileType >= FIRST_VIDEO_FILE_TYPE && fileType <= LAST_VIDEO_FILE_TYPE);
    }

    private static boolean isImageFileType(int fileType) {
        return (fileType >= FIRST_IMAGE_FILE_TYPE && fileType <= LAST_IMAGE_FILE_TYPE);
    }

    public static MediaFileType getFileType(String path) {
        int lastDot = path.lastIndexOf(".");
        if (lastDot < 0) return null;
        return sFileTypeMap.get(path.substring(lastDot + 1).toUpperCase());
    }


    public static boolean isVideoType(String path) {
        MediaFileType type = getFileType(path);
        if (null != type) {
            return isVideoFileType(type.fileType);
        }
        return false;
    }

    public static boolean isAudioType(String path) {
        MediaFileType type = getFileType(path);
        if (null != type) {
            return isAudioFileType(type.fileType);
        }
        return false;
    }

    public static boolean isImageType(String path) {
        MediaFileType type = getFileType(path);
        if (null != type) {
            return isImageFileType(type.fileType);
        }
        return false;
    }

    /// Checks if string is an powerpoint file.
    public static  boolean isPPT(String filePath) {
        final String ext = filePath.toLowerCase();

        return ext.endsWith(".ppt") || ext.endsWith(".pptx");
    }

    /// Checks if string is an word file.
    public static  boolean isWord(String filePath) {
        final String ext = filePath.toLowerCase();

        return ext.endsWith(".doc") || ext.endsWith(".docx");
    }

    /// Checks if string is an excel file.
    public static  boolean isExcel(String filePath) {
        final String ext = filePath.toLowerCase();

        return ext.endsWith(".xls") || ext.endsWith(".xlsx");
    }

    /// Checks if string is an apk file.
    public static  boolean isAPK(String filePath) {
        return filePath.toLowerCase().endsWith(".apk");
    }

    /// Checks if string is an pdf file.
    public static  boolean isPDF(String filePath) {
        return filePath.toLowerCase().endsWith(".pdf");
    }

    /// Checks if string is an txt file.
    public static  boolean isTxt(String filePath) {
        return filePath.toLowerCase().endsWith(".txt");
    }

    /// Checks if string is an chm file.
    public static  boolean isChm(String filePath) {
        return filePath.toLowerCase().endsWith(".chm");
    }

    /// Checks if string is a vector file.
    public static  boolean isVector(String filePath) {
        return filePath.toLowerCase().endsWith(".svg");
    }

    /// Checks if string is an html file.
    public static  boolean isHTML(String filePath) {
        return filePath.toLowerCase().endsWith(".html");
    }
    public static  boolean isZIP(String filePath) {
        String ext=filePath.toLowerCase();
        return ext.endsWith(".zip")
            ||ext.endsWith(".rar")
            ||ext.endsWith(".z")
            ||ext.endsWith(".arj");
    }

    /**
     * 获取 视频 或 音频 时长
     *
     * @param path 视频 或 音频 文件路径
     * @return 时长 毫秒值
     */
    public static long getDuration(String path) {
        android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();
        long duration = 0;
        try {
            if (path != null) {
                mmr.setDataSource(path);
            }
            String time = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            duration = Long.parseLong(time);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            mmr.release();
        } catch (IOException ignored) {}
        return duration;
    }

    public static String saveBitmap(Bitmap bitmap, String dir) {
        return saveBitmap(bitmap, dir, true);
    }

    public static String saveBitmap(Bitmap bitmap, String dir, boolean isRecycle) {
        try {
            File dirFile = new File(dir);
            if (!dirFile.exists()) {              //如果不存在，那就建立这个文件夹
                dirFile.mkdirs();
            }
            String random = UUID.randomUUID().toString();
            File file = new File(dir, random + ".jpg");
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            if (isRecycle)
                bitmap.recycle();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
