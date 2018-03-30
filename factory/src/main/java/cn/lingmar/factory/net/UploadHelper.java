package cn.lingmar.factory.net;

import android.text.format.DateFormat;
import android.util.Log;

import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;

import java.io.File;
import java.util.Date;

import cn.lingmar.factory.Factory;
import cn.lingmar.utils.HashUtil;

/**
 * 上传工具类，用于上传任意文件到阿里OSS存储
 */
public class UploadHelper {
    private static final String TAG = UploadHelper.class.getSimpleName();
    private static final String ENDPOINT = "http://oss-cn-hangzhou.aliyuncs.com";
    private static final String BUCKET_NAME = "itolker-new";

    private static OSS getClient() {
        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(
                "LTAIamqXgjiweTGz",
                "pnJc5eEdkUqH8rJLIgfg3tfzZuuIxr");
        return new OSSClient(Factory.app(), ENDPOINT, credentialProvider);
    }

    /**
     * 上传方法，上传成功则返回一个路径
     *
     * @param objKey 服务器上独立的Key（文件名）
     * @param path   需要上传的文件路径
     * @return
     */
    private static String upload(String objKey, String path) {
        // 构造上传请求
        PutObjectRequest request = new PutObjectRequest(
                BUCKET_NAME,
                objKey,
                path);

        try {
            OSS client = getClient();
            // 开始同步上传
            PutObjectResult putObjectResult = client.putObject(request);
            // 得到外网可访问的地址
            String url = client.presignPublicObjectURL(BUCKET_NAME, objKey);
            Log.d(TAG, String.format("PublicObjectURL: %s", url));

            return url;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 上传普通文件
     *
     * @param path 本地地址
     * @return 服务器地址
     */
    public static String uploadImage(String path) {
        String key = getImageObjKey(path);
        return upload(key, path);
    }

    /**
     * 上传头像
     *
     * @param path 本地地址
     * @return 服务器地址
     */
    public static String uploadPortrait(String path) {
        String key = getPortraitObjKey(path);
        return upload(key, path);
    }

    /**
     * 上传音频
     *
     * @param path 本地地址
     * @return 服务器地址
     */
    public static String uploadAudio(String path) {
        String key = getAudioObjKey(path);
        return upload(key, path);
    }

    /**
     * 分月存储，放置一个文件夹中的文件过多
     * @return
     */
    private static String getDateString() {
        return DateFormat.format("yyyyMM", new Date()).toString();
    }

    private static String getImageObjKey(String path) {
        String fileMd5 = HashUtil.getMD5String(new File(path));
        String dateString = getDateString();
        return String.format("image/%s/%s.jpg", dateString, fileMd5);
    }

    private static String getPortraitObjKey(String path) {
        String fileMd5 = HashUtil.getMD5String(new File(path));
//        String fileMd5 = HashUtil.getMD5String(path);


        String dateString = getDateString();
        return String.format("portrait/%s/%s.jpg", dateString, fileMd5);
    }

    private static String getAudioObjKey(String path) {
        String fileMd5 = HashUtil.getMD5String(new File(path));
        String dateString = getDateString();
        return String.format("audio/%s/%s.mp3", dateString, fileMd5);
    }
}
