package zrock.application.engine.app.plugin.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

/**
 * 文件操作工具类
 * <p>
 * 使用nio以提高性能
 * 
 * @author HouKangxi
 *
 */
public class FileUtil {

	public static void writeToFile(InputStream dataIns, File target) throws IOException {
		final int BUFFER = 1024;
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(target));
		int count;
		byte data[] = new byte[BUFFER];
		while ((count = dataIns.read(data, 0, BUFFER)) != -1) {
			bos.write(data, 0, count);
		}
		bos.close(); 
	}

	/**
	 * 
	 * 复制文件
	 * 
	 * @param source
	 *            - 源文件
	 * 
	 * @param target
	 *            - 目标文件
	 * 
	 */
	public static void copyFile(File source, File target) {

		FileInputStream fi = null;
		FileOutputStream fo = null;

		FileChannel in = null;

		FileChannel out = null;

		try {
			fi = new FileInputStream(source);

			fo = new FileOutputStream(target);

			in = fi.getChannel();// 得到对应的文件通道

			out = fo.getChannel();// 得到对应的文件通道

			in.transferTo(0, in.size(), out);// 连接两个通道，并且从in通道读取，然后写入out通道

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fi != null) {
					fi.close();
				}

				if (in != null) {
					in.close();
				}

				if (fo != null) {
					fo.close();
				}

				if (out != null) {
					out.close();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
