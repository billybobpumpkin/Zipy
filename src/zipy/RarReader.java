package zipy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;

/**
 * A class used to read .rar files, using the Junrar library.
 * <p>
 * @author Abraham Yelifari
 * @version 1.0
 * @since 2025-09-27 1:13
 */
public class RarReader implements IReader
{
	private final String outputPath;
	private final String rarFilePath;
	
	public RarReader(String rarFilePath, String outputPath)
	{
		this.rarFilePath = rarFilePath;
		this.outputPath = outputPath;
	}
	
	public RarReader(File rarFile, File output)
	{
		this.rarFilePath = rarFile.getAbsolutePath();
		this.outputPath = output.getAbsolutePath();
	}

	@Override
	public boolean extractFiles() throws IOException
	{
		File outputDir = new File(outputPath);
		
		if(!outputDir.exists())
			outputDir.mkdir();
		
		try(Archive archive = new Archive(new File(rarFilePath))) {
			if (archive.isEncrypted())
				return false;
			
			for(FileHeader fh : archive.getFileHeaders())
			{
				File outputFile = new File(outputDir, fh.getFileName());
                try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                	archive.extractFile(fh, fos);
                } catch(IOException e) {
                	Zipy.deleteDirectory(outputDir);
                	return false;
                }
			}
			
			return true;
		} catch (RarException e) {
			 throw new IOException(e);
		}
	}

	@Override
	public boolean isPasswordProtected() throws IOException
	{
		try (Archive archive = new Archive(new File(rarFilePath))) {
           return archive.isEncrypted();
        } catch (RarException e) {
        	 throw new IOException(e);
        }
	}

	@Override
	public List<String> listEntries() throws IOException
	{
		List<String> entries = new ArrayList<String>();
		
		try (Archive archive = new Archive(new File(rarFilePath))) {
			for(FileHeader fh : archive.getFileHeaders())
				entries.add(fh.getFileName());
        } catch (RarException e) {
        	 throw new IOException(e);
        }
		
		return entries;
	}

	@Override
	public int getNumberOfItemsInArchive() throws IOException
	{
		return this.listEntries().size();
	}
}