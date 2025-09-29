package zipy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * A static helper class used when only needing to create and extract files from
 * archives. No need to instantiate the class as all methods are static.
 * <p>
 * Author: Abraham Yelifari  
 * Version: 1.0  
 * Since: 2025-09-27 1:13  
 */
public class Zipy
{
	/**
	 * A static method used to extract the zipped file to the specified output file. The output file
	 * doesn't need to be created as it will create the file on the disk itself. Any previous files at
	 * the specified location will be overwritten if necessary.
	 * @param zipPath the path in which the file is stored.
	 * @param extractToPath the path where the extracted files should be extracted to.
	 * @return {@code true} if the extraction was successful, {@code false} if it failed.
	 */
	public static boolean extractZipTo(Path zipPath, Path extractToPath)
	{
		return Zipy.extractZipTo(zipPath.toFile(), extractToPath.toFile());
	}

	/**
	 * A static method used to extract the zipped file to the specified output file. The output file
	 * doesn't need to be created as it will create the file on the disk itself. Any previous files at
	 * the specified location will be overwritten if necessary.
	 * @param zipPath the path in which the file is stored.
	 * @param extractToPath the path where the extracted files should be extracted to.
	 * @return {@code true} if the extraction was successful, {@code false} if it failed.
	 */
	public static boolean extractZipTo(String zipPath, String extractToPath)
	{
		return Zipy.extractZipTo(new File(zipPath), new File(extractToPath));
	}

	/**
	 * A static method used to extract the zipped file to the specified output file. The output file
	 * doesn't need to be created as it will create the file on the disk itself. Any previous files at
	 * the specified location will be overwritten if necessary.
	 * @param zip the zipped file to extract.
	 * @param extractTo the location to extract the files to.
	 * @return {@code true} if the extraction was successful, {@code false} if it failed.
	 */
	public static boolean extractZipTo(File zip, File extractTo)
	{
		ZipReader zipr = new ZipReader(zip, extractTo);
		return zipr.extractFiles();
	}

	/**
	 * A static method used to create a ZIP archive from the specified file or folder. 
	 * The output archive will be written to the location specified by {@code zipFile}.
	 * @param zipFile the file to save the archive as.
	 * @param contentsToZip the file or directory to compress into the archive.
	 */
	public static void makeZipArchive(File zipFile, File contentsToZip)
	{
		try {
			ZipReader.makeArchive(zipFile, contentsToZip);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * A static method used to extract a 7z archive to the specified output directory. The output directory
	 * doesn't need to be created as it will create the folder on the disk itself. Any previous files at
	 * the specified location will be overwritten if necessary.
	 * @param zip the path in which the archive is stored.
	 * @param extractTo the path where the extracted files should be extracted to.
	 * @return {@code true} if the extraction was successful, {@code false} if it failed.
	 */
	public static boolean extract7zTo(Path zip, Path extractTo)
	{
		return Zipy.extractZipTo(zip.toFile(), extractTo.toFile());
	}

	/**
	 * A static method used to extract a 7z archive to the specified output directory. The output directory
	 * doesn't need to be created as it will create the folder on the disk itself. Any previous files at
	 * the specified location will be overwritten if necessary.
	 * @param sevenZipPath the path where the archive is stored.
	 * @param extractToPath the path where the extracted files should be extracted to.
	 * @return {@code true} if the extraction was successful, {@code false} if it failed.
	 */
	public static boolean extract7zTo(String sevenZipPath, String extractToPath)
	{
		return Zipy.extractZipTo(new File(sevenZipPath), new File(extractToPath));
	}

	/**
	 * A static method used to extract a 7z archive to the specified output directory. The output directory
	 * doesn't need to be created as it will create the folder on the disk itself. Any previous files at
	 * the specified location will be overwritten if necessary.
	 * @param sevenZip the archive file to extract.
	 * @param extractTo the location to extract the files to.
	 * @return {@code true} if the extraction was successful, {@code false} if it failed.
	 */
	public static boolean extract7zTo(File sevenZip, File extractTo)
	{
		SevenZReader reader = new SevenZReader(sevenZip, extractTo);
		return reader.extractFiles();
	}

	/**
	 * A static method used to create a 7z archive from the specified file or folder.
	 * The output archive will be written to the location specified by {@code sevenZipFile}.
	 * @param sevenZipFile the file to save the archive as.
	 * @param contentsTo7z the file or directory to compress into the archive.
	 * @param compressionLevel the level of compression to apply (usually between 0–9).
	 */
	public static void make7zArchive(File sevenZipFile, File contentsTo7z, int compressionLevel)
	{
		try {
			SevenZReader.makeArchive(sevenZipFile, contentsTo7z, compressionLevel);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * A static method used to extract a TAR archive to the specified output directory. The output directory
	 * doesn't need to be created as it will create the folder on the disk itself. Any previous files at
	 * the specified location will be overwritten if necessary.
	 * @param tarPath the path in which the archive is stored.
	 * @param extractToPath the path where the extracted files should be extracted to.
	 * @return {@code true} if the extraction was successful, {@code false} if it failed.
	 */
	public static boolean extractTarTo(Path tarPath, Path extractToPath)
	{
		return Zipy.extractZipTo(tarPath.toFile(), extractToPath.toFile());
	}

	/**
	 * A static method used to extract a TAR archive to the specified output directory. The output directory
	 * doesn't need to be created as it will create the folder on the disk itself. Any previous files at
	 * the specified location will be overwritten if necessary.
	 * @param tarPath the path where the archive is stored.
	 * @param extractToPath the path where the extracted files should be extracted to.
	 * @return {@code true} if the extraction was successful, {@code false} if it failed.
	 */
	public static boolean extractTarTo(String tarPath, String extractToPath)
	{
		return Zipy.extractZipTo(new File(tarPath), new File(extractToPath));
	}

	/**
	 * A static method used to extract a TAR archive to the specified output directory. The output directory
	 * doesn't need to be created as it will create the folder on the disk itself. Any previous files at
	 * the specified location will be overwritten if necessary.
	 * @param tar the archive file to extract.
	 * @param extractTo the location to extract the files to.
	 * @return {@code true} if the extraction was successful, {@code false} if it failed.
	 */
	public static boolean extractTarTo(File tar, File extractTo)
	{
		TarReader reader = new TarReader(tar, extractTo);
		return reader.extractFiles();
	}

	/**
	 * A static method used to create a TAR archive from the specified file or folder.
	 * The output archive will be written to the location specified by {@code tarFile}.
	 * @param tarFile the file to save the archive as.
	 * @param contentsToTar the file or directory to compress into the archive.
	 */
	public static void makeTarArchive(File tarFile, File contentsToTar)
	{
		try {
			TarReader.makeArchive(tarFile, contentsToTar);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * A static method used to extract a RAR archive to the specified output directory. The output directory
	 * doesn't need to be created as it will create the folder on the disk itself. Any previous files at
	 * the specified location will be overwritten if necessary.
	 * @param rarPath the path in which the archive is stored.
	 * @param extractToPath the path where the extracted files should be extracted to.
	 * @return {@code true} if the extraction was successful, {@code false} if it failed.
	 */
	public static boolean extractRarTo(Path rarPath, Path extractToPath)
	{
		return Zipy.extractZipTo(rarPath.toFile(), extractToPath.toFile());
	}

	/**
	 * A static method used to extract a RAR archive to the specified output directory. The output directory
	 * doesn't need to be created as it will create the folder on the disk itself. Any previous files at
	 * the specified location will be overwritten if necessary.
	 * @param rarPath the path where the archive is stored.
	 * @param extractToPath the path where the extracted files should be extracted to.
	 * @return {@code true} if the extraction was successful, {@code false} if it failed.
	 */
	public static boolean extractRarTo(String rarPath, String extractToPath)
	{
		return Zipy.extractZipTo(new File(rarPath), new File(extractToPath));
	}

	/**
	 * A static method used to extract a RAR archive to the specified output directory. The output directory
	 * doesn't need to be created as it will create the folder on the disk itself. Any previous files at
	 * the specified location will be overwritten if necessary.
	 * @param rar the archive file to extract.
	 * @param extractTo the location to extract the files to.
	 * @return {@code true} if the extraction was successful, {@code false} if it failed.
	 */
	public static boolean extractRarTo(File rar, File extractTo)
	{
		RarReader reader = new RarReader(rar, extractTo);
		return reader.extractFiles();
	}

	// Can't make a .rar archive.
}