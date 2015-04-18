package net.download;
/**
 * 该接口实时获取下载的文件大小
 * @author Administrator
 *
 */
public interface DownloadProgressListener {
	public void onDownloadSize(int size);
}
