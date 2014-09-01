package com.lynda.javatraining.filetree;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DecimalFormat;
import java.util.TreeMap;

public class MyFileVisitor extends SimpleFileVisitor<Path> {

	TreeMap<Path, PathInfo> dirCountSizeMap = new MyTreeMap<Path, PathInfo>();

	long currentDirFileCount = 0;
	long currentDirFileSize = 0;

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
			throws IOException {
		dirCountSizeMap.put(dir, new PathInfo());
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
			throws IOException {
		currentDirFileCount++;
		currentDirFileSize += attrs.size();
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc)
			throws IOException {
		System.err.println("Failed File Visit: " + file.toString()
				+ ", error: " + exc.toString());
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc)
			throws IOException {
		PathInfo info = dirCountSizeMap.get(dir);
		info.localFileCount = currentDirFileCount;
		info.localFileSize = currentDirFileSize;

		Path parent = dir;
		PathInfo parentInfo = null;

		// Add directory file count and size to parent node
		while (((parent = parent.getParent()) != null)
				&& ((parentInfo = dirCountSizeMap.get(parent)) != null)) {
			parentInfo.localAndSubFileCount += currentDirFileCount;
			parentInfo.localAndSubFileSize += currentDirFileSize;
			dirCountSizeMap.put(parent, parentInfo);
		}

		// Reset directory stats
		currentDirFileCount = 0;
		currentDirFileSize = 0;

		return FileVisitResult.CONTINUE;
	}

	public void dumpInfo() {
		// System.out.println(map.toString());
		Path target = Paths.get("files/filelisting.txt");
		Charset charset = Charset.forName("US-ASCII");
		System.out.println("Writing to: " + target.getFileName());
		try (BufferedWriter bw = Files
				.newBufferedWriter(
						target,
						charset,
						(Files.exists(target, LinkOption.NOFOLLOW_LINKS) ? StandardOpenOption.TRUNCATE_EXISTING
								: StandardOpenOption.CREATE_NEW))) {
			bw.write(dirCountSizeMap.toString());
			bw.newLine();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		System.out.println("DONE.");
	}

	static class sizeInfo {

		static final Double KB = 1024d;
		static final Double MB = KB * KB;
		static final Double GB = MB * KB;
		static final Double TB = GB * KB;
		static DecimalFormat df = new DecimalFormat("#.##");
		static DecimalFormat bf = new DecimalFormat("#");

		static String byteString(Long bytes) {
			return (bytes > KB ? (bytes > MB ? (bytes > GB ? (bytes > TB ? df
					.format(bytes / TB) + "TB" : df.format(bytes / GB) + "GB")
					: df.format(bytes / MB) + "MB") : df.format(bytes / KB)
					+ "KB") : bf.format(bytes) + "B");
		}
	}

	class PathInfo {

		long localFileCount = 0;
		long localAndSubFileCount = 0;
		long localFileSize = 0;
		long localAndSubFileSize = 0;

		public String toString() {
			return ("FileCount: " + localFileCount) + ", FileSize: "
					+ sizeInfo.byteString(localFileSize) + ", SubFileCount: "
					+ localAndSubFileCount + ", SubFileSize:"
					+ sizeInfo.byteString(localAndSubFileSize);
		}
	}

}
