package zipy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IOutCreateArchive7z;
import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItem7z;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;

/**
 * A class used to read .7z files, using the Apache Commons library, as well as
 * the SevenZipJBinding library. Don't ask why I used 2 instead of 1.
 * <p>
 * @author Abraham Yelifari
 * @version 1.0
 * @since 2025-09-27 1:13
 */
public class SevenZReader implements IReader
{
	private final String outputPath;
	private final String zipFilePath;
	
	public SevenZReader(String zipFilePath, String outputPath)
	{
		this.zipFilePath = zipFilePath;
		this.outputPath = outputPath;
	}
	
	public SevenZReader(File zipFile, File output)
	{
		this.zipFilePath = zipFile.getAbsolutePath();
		this.outputPath = output.getAbsolutePath();
	}
	
	@Override
	public boolean extractFiles() throws IOException
	{
		File outputDir = new File(outputPath);
		
		if(!outputDir.exists())
			outputDir.mkdir();
		
		try (SeekableByteChannel channel = Files.newByteChannel(Paths.get(zipFilePath));
			SevenZFile sevenZFile = SevenZFile.builder().setSeekableByteChannel(channel)
														.get()) {
			SevenZArchiveEntry entry;
			while((entry = sevenZFile.getNextEntry()) != null)
			{
				if (!entry.isDirectory())
				{
					File outFile = new File(outputDir, entry.getName());
					
					try(FileOutputStream out = new FileOutputStream(outFile)) {
						byte[] stuff = new byte[(int)entry.getSize()];
						sevenZFile.read(stuff, 0, stuff.length);
						out.write(stuff);
					} catch (IOException e) {
						e.printStackTrace();
						Zipy.deleteDirectory(outputDir);
						return false;
					}
				}
			}
			
			return true;
		}
	}

	@Override
	public boolean isPasswordProtected() throws IOException
	{
		try (SeekableByteChannel channel = Files.newByteChannel(new File(zipFilePath).toPath());
				SevenZFile sevenZFile = SevenZFile.builder().setSeekableByteChannel(channel)
															.get()) {
            //If this doesn't throw an exception then it is not password protected
			sevenZFile.getNextEntry();
            return false;
        } catch (IOException e) {
            // If an IOException occurs, especially one related to
            // "Cannot read encrypted files without a password",
            // then the archive is password-protected.
            if (e.getMessage() != null && e.getMessage().contains("Cannot read encrypted files without a password"))
                return true;
            else
            	throw new IOException("Fatal error occured.");
        }
	}

	@Override
	public List<String> listEntries() throws IOException
	{
		List<String> entries = new ArrayList<>();
		
		try (SeekableByteChannel channel = Files.newByteChannel(new File(zipFilePath).toPath());
				SevenZFile sevenZFile = SevenZFile.builder().setSeekableByteChannel(channel)
															.get()) {
            SevenZArchiveEntry entry;
            while((entry = sevenZFile.getNextEntry()) != null)
            	entries.add(entry.getName());
        }
		
		return entries;
	}
	
	/*
	 * Taken directly from the SevenZipJBinding wikipedia!!
	 */
	@Override
	public int getNumberOfItemsInArchive() throws IOException
	{
		IInArchive archive;
	    RandomAccessFile randomAccessFile;

	    randomAccessFile = new RandomAccessFile(zipFilePath, "r");

	    archive = SevenZip.openInArchive(ArchiveFormat.ZIP, // null - autodetect
	            new RandomAccessFileInStream(randomAccessFile));

	    int numberOfItems = archive.getNumberOfItems();

	    archive.close();
	    randomAccessFile.close();
	    
	    return numberOfItems;
	}
	
	
	/**
	 * A static method used to create a .7z file, as there is no need to have it localized since
	 * the local variables are meant to be used in extraction
	 * @param fileToZip the file(s) to zip.
	 * @param compressionLevel the compression level used to zip.  0 - Copy mode (no compression)
	 * 1 - Fastest 3 - Fast 5 - Normal 7 - Maximum 9 - Ultra Note,
	 * that the meaning of compression level can differ through out different archive formats.
	 * @return {@link File} the archive created.
	 * @throws IOException
	 */
	public static File makeArchive(File fileToZip, int compressionLevel) throws IOException
	{
		String fileName = fileToZip.getName();
		String name = fileName.substring(0, (fileName.contains(".") ? fileName.lastIndexOf('.') : fileName.length()));
		
		//The new archive we are creating
		File outFile = new File(name);
		
		IOutCreateArchive7z outArchive7z = SevenZip.openOutArchive7z();
		ISequentialOutStream stream = new RandomAccessFileOutStream(new RandomAccessFile(outFile, "rw"));
		
		List<File> filesToCompress = new ArrayList<File>();
		List<String> paths = new ArrayList<String>();
		
		collectFiles(fileToZip, outFile, filesToCompress, paths);
		
		/*
		 * Set compression level: 0 - Copy mode (no compression)
		 * 1 - Fastest 3 - Fast 5 - Normal 7 - Maximum 9 - Ultra Note,
		 * that the meaning of compression level can differ through out different archive formats.
		 */
		outArchive7z.setLevel(compressionLevel);
		
		/*
		 * Creating a new archive using the out stream to put data into the new 7z file.
		 * We then create a callback interface so we can specify what to do per entry.
		 * We make sure to put the in stream as out file(s) to compress.
		 */
		outArchive7z.createArchive(stream, compressionLevel, new IOutCreateCallback<IOutItem7z>() {
			@Override
			public void setCompleted(long arg0) throws SevenZipException {}
			@Override
			public void setTotal(long arg0) throws SevenZipException {}

			@Override
			public IOutItem7z getItemInformation(int index, OutItemFactory<IOutItem7z> factory)
					throws SevenZipException
			{
				File ftc = filesToCompress.get(index);
				String path = paths.get(index);
				IOutItem7z outItem = factory.createOutItem();
				
				if (ftc.isDirectory())
				{
			        outItem.setDataSize(0l);
			        outItem.setPropertyIsDir(true);
			    }
				else
				{
			        outItem.setDataSize(ftc.length());
			    }
				
				outItem.setPropertyPath(path);
				outItem.setPropertyLastModificationTime(new Date(ftc.lastModified()));
				return outItem;
			}

			@Override
			public ISequentialInStream getStream(int index) throws SevenZipException
			{
				File file = filesToCompress.get(index);
			    if (file.isDirectory())
			    	return null;
			    
			    try {
			        return new RandomAccessFileInStream(new RandomAccessFile(file, "r"));
			    } catch (FileNotFoundException e) {
			        e.printStackTrace();
			        return null;
			    }
			}

			@Override
			public void setOperationResult(boolean operationResultOk) throws SevenZipException
			{
				if (!operationResultOk)
                    throw new SevenZipException("Compression failed for the current item.");
			}
		
		});
		
		outArchive7z.close();
		
		return outFile;
	}
	
	/**
	 * A static method used to create a .7z file, as there is no need to have it localized since
	 * the local variables are meant to be used in extraction.
	 * @param fileToZip the file(s) to zip.
	 * @param zippedFile the file where the archive will be created.
	 * @param compressionLevel the compression level used to zip.  0 - Copy mode (no compression)
	 * 1 - Fastest 3 - Fast 5 - Normal 7 - Maximum 9 - Ultra Note,
	 * that the meaning of compression level can differ through out different archive formats.
	 * @throws IOException
	 */
	public static void makeArchive(File fileToZip, File zippedFile, int compressionLevel) throws IOException
	{
		File outFile = zippedFile;
		
		if (outFile == null)
		{
			String fileName = fileToZip.getName();
			String name = fileName.substring(0, (fileName.contains(".") ? fileName.lastIndexOf('.') : fileName.length()));
			
			//The new archive we are creating
			outFile = new File(name);
		}
		
		IOutCreateArchive7z outArchive7z = SevenZip.openOutArchive7z();
		ISequentialOutStream stream = new RandomAccessFileOutStream(new RandomAccessFile(outFile, "rw"));
		
		List<File> filesToCompress = new ArrayList<File>();
		List<String> paths = new ArrayList<String>();
		
		collectFiles(fileToZip, outFile, filesToCompress, paths);
		
		/*
		 * Set compression level: 0 - Copy mode (no compression)
		 * 1 - Fastest 3 - Fast 5 - Normal 7 - Maximum 9 - Ultra Note,
		 * that the meaning of compression level can differ through out different archive formats.
		 */
		outArchive7z.setLevel(compressionLevel);
		
		/*
		 * Creating a new archive using the out stream to put data into the new 7z file.
		 * We then create a callback interface so we can specify what to do per entry.
		 * We make sure to put the in stream as out file(s) to compress.
		 */
		outArchive7z.createArchive(stream, compressionLevel, new IOutCreateCallback<IOutItem7z>() {
			@Override
			public void setCompleted(long arg0) throws SevenZipException {}
			@Override
			public void setTotal(long arg0) throws SevenZipException {}

			@Override
			public IOutItem7z getItemInformation(int index, OutItemFactory<IOutItem7z> factory)
					throws SevenZipException
			{
				File ftc = filesToCompress.get(index);
				String path = paths.get(index);
				IOutItem7z outItem = factory.createOutItem();
				
				if (ftc.isDirectory())
				{
			        outItem.setDataSize(0l);
			        outItem.setPropertyIsDir(true);
			    }
				else
				{
			        outItem.setDataSize(ftc.length());
			    }
				
				outItem.setPropertyPath(path);
				outItem.setPropertyLastModificationTime(new Date(ftc.lastModified()));
				return outItem;
			}

			@Override
			public ISequentialInStream getStream(int index) throws SevenZipException
			{
				File file = filesToCompress.get(index);
			    if (file.isDirectory())
			    	return null;
			    
			    try {
			        return new RandomAccessFileInStream(new RandomAccessFile(file, "r"));
			    } catch (FileNotFoundException e) {
			        e.printStackTrace();
			        return null;
			    }
			}

			@Override
			public void setOperationResult(boolean operationResultOk) throws SevenZipException
			{
				if (!operationResultOk)
                    throw new SevenZipException("Compression failed for the current item.");
			}
		
		});
		
		outArchive7z.close();
	}
	
	
	private static void collectFiles(File rootDir, File current, List<File> files, List<String> paths)
	{
        File[] children = current.listFiles();

        if (children != null)
        {
            for (File file : children)
            {
                files.add(file);
                
                String relativePath = rootDir.toURI().relativize(file.toURI()).getPath();
                relativePath = relativePath.replace("\\", "/");

                if (file.isDirectory() && !relativePath.endsWith("/"))
                	relativePath += "/";

                paths.add(relativePath);

                if (file.isDirectory())
                    collectFiles(rootDir, file, files, paths);
            }
        }
    }

}