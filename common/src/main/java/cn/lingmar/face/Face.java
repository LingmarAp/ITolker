package cn.lingmar.face;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.Spannable;
import android.util.ArrayMap;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import cn.lingmar.utils.StreamUtil;

public class Face {
    private static final ArrayMap<String, Bean> FACE_MAP = new ArrayMap<>();
    private static List<FaceTab> FACE_TABS = null;

    private static void init(Context context) {
        if (FACE_TABS == null) {
            synchronized (Face.class) {
                if (FACE_TABS == null) {
                    ArrayList<FaceTab> faceTabs = new ArrayList<>();
                    FaceTab tab = initAssetsFace(context);
                    if (tab != null)
                        faceTabs.add(tab);

                    tab = initResourceFace(context);
                    if (tab != null)
                        faceTabs.add(tab);

                    for (FaceTab faceTab : faceTabs)
                        faceTab.copyToMap(FACE_MAP);

                    FACE_TABS = Collections.unmodifiableList(faceTabs);
                }
            }

        }
    }

    // 从zip包中解析表情
    private static FaceTab initAssetsFace(Context context) {
        String faceAsset = "face-t.zip";
        // data/data/包名/files/face/ft
        String faceCacheDir = String.format("%s/face/ft", context.getFilesDir());//.replace("/0", "");
        File faceFolder = new File(faceCacheDir);

        if (!faceFolder.exists()) {
            if (faceFolder.mkdirs()) {
                try {
                    InputStream inputStream = context.getAssets().open(faceAsset);
                    // save
                    File faceSource = new File(faceFolder, "source.zip");
                    StreamUtil.copy(inputStream, faceSource);

                    // unzip
                    unZipFile(faceSource, faceFolder);

                    // clear
                    StreamUtil.delete(faceSource.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        File infoFile = new File(faceCacheDir, "info.json");

        Gson gson = new Gson();
        JsonReader reader = null;
        try {
            reader = gson.newJsonReader(new FileReader(infoFile));
        } catch (Exception e) {
            e.printStackTrace();
        }

        FaceTab tab = gson.fromJson(reader, FaceTab.class);

        // 相对路径-->绝对路径
        for (Bean face : tab.faces) {
            face.preview = String.format("%s/%s", faceCacheDir, face.preview);
            face.source = String.format("%s/%s", faceCacheDir, face.source);
        }

        return tab;
    }

    // 解压文件到目录
    private static void unZipFile(File zipFile, File desDir) throws IOException {
        final String folderPath = desDir.getAbsolutePath();

        ZipFile zf = new ZipFile(zipFile);
        for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements(); ) {
            ZipEntry entry = (ZipEntry) entries.nextElement();

            // 过滤缓存文件
            String name = entry.getName();
            if(name.startsWith("."))
                continue;

            InputStream in = zf.getInputStream(entry);
            String str = folderPath + File.separator + name;
            // 防止名称错乱
            str = new String(str.getBytes("8859_1"), "GB2312");

            File desFile = new File(str);
            // 输出文件
            StreamUtil.copy(in, desFile);
        }
    }

    // 从drawable资源中加载数据并映射到对应的key
    private static FaceTab initResourceFace(Context context) {
        final ArrayList<Bean> faces = new ArrayList<>();
        final Resources resources = context.getResources();

        String packageName = context.getApplicationInfo().packageName;
        for (int i = 1; i <= 142; i++) {
            String key = String.format(Locale.ENGLISH, "fb%03d", i);
            String resStr = String.format(Locale.ENGLISH, "face_base_%03d", i);

            int resId = resources.getIdentifier(resStr, "drawable", packageName);
            if (resId == 0)
                continue;

            faces.add(new Bean(key, resId));
        }

        if (faces.size() == 0)
            return null;

        return new FaceTab(faces, "NAME", faces.get(0).preview);
    }

    // 获取所有的表情
    public static List<FaceTab> all(@NonNull Context context) {
        init(context);
        return FACE_TABS;
    }

    // 输入表情到editable
    public static List<FaceTab> inputFace(@NonNull Context context, final Editable editable,
                                          final Face.Bean bean, final int size) {
        return null;
    }

    // 从spannable解析表情并替换显示
    public static List<FaceTab> decode(@NonNull View target, final Spannable spannable,
                                       final int size) {
        return null;
    }

    /**
     * 每一个表情盘，含有很多表情
     */
    public static class FaceTab {
        public List<Bean> faces;
        public String name;
        // 预览图
        public Object preview;

        void copyToMap(ArrayMap<String, Bean> faceMap) {
            for (Bean face : faces) {
                faceMap.put(face.key, face);
            }
        }

        public FaceTab(List<Bean> faces, String name, Object preview) {
            this.faces = faces;
            this.name = name;
            this.preview = preview;
        }
    }

    /**
     * 每一个表情
     */
    public static class Bean {
        public String key;
        public Object source;
        public Object preview;
        public String desc;

        public Bean(String key, int preview) {
            this.key = key;
            this.preview = preview;
            this.source = preview;
        }
    }
}
