package zipy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.FileHeader;

/**
 * A class used to read .zip files, using the standard Java Zip Library, as well as
 * the Zip4J library to create .zip files.
 * <p>
 * @author Abraham Yelifari
 * @version 1.0
 * @since 2025-09-27 1:13
 */
public class ZipReader implements IReader
{
	private final String outputPath;
	private final String zipFilePath;
	
	public ZipReader(String zipFilePath, String outputPath)
	{
		this.zipFilePath = zipFilePath;
		this.outputPath = outputPath;
	}
	
	public ZipReader(File zipFile, File output)
	{
		this.zipFilePath = zipFile.getAbsolutePath();
		this.outputPath = output.getAbsolutePath();
	}
	
	@Override
	public boolean extractFiles()
	{
		File outputFolder = new File(outputPath);
		ZipEntry zipEntry;
		
		if (!outputFolder.exists())
			outputFolder.mkdirs();
		
		try(ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {
			
			while((zipEntry = zis.getNextEntry()) != null)
			{
				
                if (zipEntry.isDirectory())
                	continue;
                
                File newFile = new File(outputFolder, new File(zipEntry.getName()).getName());

                if (!newFile.exists())
                {
                	try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zis.read(buffer)) > 0)
                            fos.write(buffer, 0, len);
                    }
                }
                zis.closeEntry();
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
		try(ZipFile zipped = new ZipFile(new File(zipFilePath))) {
			return zipped.isEncrypted();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public List<String> listEntries()
	{
		List<String> entries = new ArrayList<>();
		
		try(ZipFile zipped = new ZipFile(zipFilePath)) {
			for (FileHeader header : zipped.getFileHeaders())
				entries.add(header.getFileName());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return entries;
	}
	
	@Override
	public int getNumberOfItemsInArchive()
	{
		return this.listEntries().size();
	}
	
	
	/**
	 * A static method used to create a .zip file, as there is no need to have it localized since
	 * the local variables are meant to be used in extraction.
	 * @param fileToZip the file(s) to zip.
	 * @return {@link File} the created archive
	 * @throws IOException
	 */
	public static File makeArchive(File fileToZip) throws IOException
	{
		String fileName = fileToZip.getName();
		String name = fileName.substring(0, (fileName.contains(".") ? fileName.lastIndexOf('.') : fileName.length()));
		ZipFile zipped = new ZipFile(name);
		
		if (fileToZip.isDirectory())
			zipped.addFolder(fileToZip);
		else
			zipped.addFile(fileToZip);
		
		zipped.close();
		
		return zipped.getFile();
	}
	
	/**
	 * A static method used to create a .zip file, as there is no need to have it localized since
	 * the local variables are meant to be used in extraction.
	 * @param fileToZip the file(s) to zip.
	 * @param zippedFile the file where the archive will be created.
	 * @throws IOException
	 */
	public static void makeArchive(File fileToZip, File zippedFile) throws IOException
	{
		ZipFile zipped = new ZipFile(zippedFile);
		
		if (fileToZip.isDirectory())
			zipped.addFolder(fileToZip);
		else
			zipped.addFile(fileToZip);
		
		zipped.close();
	}
}