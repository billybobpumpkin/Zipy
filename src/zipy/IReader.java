package zipy;

import java.util.List;


/**
 * An interface used to generalize archive extracting operations.
 * Each method should not throw any exceptions, as they should be handled inside
 * the implemented method.
 * <p>
 * @author Abraham Yelifari
 * @version 1.0
 * @since 2025-09-27 1:13
 */
public interface IReader
{
	/**
	 * Extracts the files to the outputPath specified during creation.
	 * @return {@code true} if it was successful in extracting the files to the
	 * 		   specified output path and {@code false} if it failed.
	 */
	public boolean extractFiles();
	
	/**
	 * Tries to open the file and determine if it is password protected.
	 * The exception is caught in the function.
	 * @return {@code true} if it is not password protected, else {@code false}
	 */
	public boolean isPasswordProtected();
	
	/**
	 * Tries to list all of the entries in the file, including directories.
	 * @return {@link List} a list of all of the found entries.
	 */
	public List<String> listEntries();
	
	/**
	 * Tries to read the file and determine the amount of entries in the archive.
	 * @return the amount of items found in the archive.
	 */
	public int getNumberOfItemsInArchive();
}