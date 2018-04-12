package com.example.francisfeng.helloweather.Databases;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by francisfeng on 06/12/2017.
 */

public class CopyDatabase {

    public static void packDataBase(Context context){

        // 数据库路径
        String DB_PATH = "/data/data/com.example.francisfeng.helloweather/databases/";
        String DB_NAME = "hello_weather.db";

        if (!(new File(DB_PATH + DB_NAME)).exists()) {
            // 如 SQLite 数据库文件不存在，再检查一下 database 目录是否存在
            System.out.println("again");
            File f = new File(DB_PATH);
            // 如国 database 目录不存在，新建该目录
            if (!f.exists()) {
                f.mkdir();
            }
        try {
            // 得到 assets 目录下我们实现准备好的 SQLite 数据库作为输入流
            InputStream is = context.getAssets().open(DB_NAME);
            // 输出流,在指定路径下生成db文件
            OutputStream os = new FileOutputStream(DB_PATH + DB_NAME);
            // 文件写入
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }

            // 关闭文件流
            os.flush();
            os.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    }
}
