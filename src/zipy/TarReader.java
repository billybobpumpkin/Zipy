package zipy;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

/**
 * A class used to read .tar files, using the Apache Commons library.
 * <p>
 * @author Abraham Yelifari
 * @version 1.0
 * @since 2025-09-27 1:13
 */
public class TarReader implements IReader
{
	private final String outputPath;
	private final String tarFilePath;
	
	public TarReader(String tarFilePath, String outputPath)
	{
		this.tarFilePath = tarFilePath;
		this.outputPath = outputPath;
	}
	
	public TarReader(File tarFile, File output)
	{
		this.tarFilePath = tarFile.getAbsolutePath();
		this.outputPath = output.getAbsolutePath();
	}

	@Override
	public boolean extractFiles()
	{
		File outputDir = new File(outputPath);
		
		if(!outputDir.exists())
			outputDir.mkdir();
		
		try (TarArchiveInputStream tais = new TarArchiveInputStream(
											new BufferedInputStream(
												new FileInputStream(new File(tarFilePath))))){
			TarArchiveEntry entry;
			while((entry = tais.getNextEntry()) != null)
			{
				if(!tais.canReadEntryData(entry))
					continue;
				
				File outputFile = new File(outputDir, entry.getName());
				
				try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = tais.read(buffer)) != -1)
                        fos.write(buffer, 0, bytesRead);
                }
			}
			
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}

	@Override
	public boolean isPasswordProtected()
	{
		return false;
	}
	
	/**
	 * A static method used to create a .tar file, as there is no need to have it localized since
	 * the local variables are meant to be used in extraction.
	 * @param fileToTar the file(s) to archive into a .tar file.
	 * @return {@link File} the archive created.
	 * @throws FileNotFoundException, IOException
	 */
	public static File makeArchive(File filesToTar) throws FileNotFoundException, IOException
	{
		String fileName = filesToTar.getName();
		String name = fileName.substring(0, (fileName.contains(".") ? fileName.lastIndexOf('.') : fileName.length()));
		File tarFile = new File(name);
		
		try(FileOutputStream fos = new FileOutputStream(tarFile);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
					TarArchiveOutputStream taos = new TarArchiveOutputStream(bos);) {
			
			TarArchiveEntry entry = new TarArchiveEntry(filesToTar);
			taos.putArchiveEntry(entry);
			
			try(FileInputStream fis = new FileInputStream(filesToTar)) {
				byte[] buffer = new byte[1024];
				int bytesRead = 0;
				
				while ((bytesRead = fis.read(buffer)) != -1)
                    taos.write(buffer, 0, bytesRead);
			}
			taos.closeArchiveEntry();
			
			return tarFile;
		}
	}
	
	
	/**
	 * A static method used to create a .tar file, as there is no need to have it localized since
	 * the local variables are meant to be used in extraction
	 * @param fileToTar the file(s) to archive into a .tar file.
	 * @param tarFile the file where the archive will be created.
	 * @throws FileNotFoundException, IOException
	 */
	public static void makeArchive(File filesToTar, File tarFile) throws FileNotFoundException, IOException
	{		
		try(FileOutputStream fos = new FileOutputStream(tarFile);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
					TarArchiveOutputStream taos = new TarArchiveOutputStream(bos);) {
			
			TarArchiveEntry entry = new TarArchiveEntry(filesToTar);
			taos.putArchiveEntry(entry);
			
			try(FileInputStream fis = new FileInputStream(filesToTar)) {
				byte[] buffer = new byte[1024];
				int bytesRead = 0;
				
				while ((bytesRead = fis.read(buffer)) != -1)
                    taos.write(buffer, 0, bytesRead);
			}
			taos.closeArchiveEntry();
		}
	}

	@Override
	public List<String> listEntries()
	{
		List<String> entries = new ArrayList<>();
		try(TarArchiveInputStream tais = new TarArchiveInputStream(new FileInputStream(new File(tarFilePath)))) {
			TarArchiveEntry entry;
			while((entry = tais.getNextEntry()) != null)
			{
				entries.add(entry.getName());
				entry = tais.getNextEntry();
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		return entries;
	}
	
	@Override
	public int getNumberOfItemsInArchive()
	{
		return this.listEntries().size();
	}
}