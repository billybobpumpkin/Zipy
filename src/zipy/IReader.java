package zipy;

import java.io.IOException;
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
	 * @throws IOException when a fatal error occurs.
	 * @return {@code true} if it was successful in extracting the files to the
	 * 		   specified output path and {@code false} if it failed.
	 */
	public boolean extractFiles() throws IOException;
	
	/**
	 * Tries to open the file and determine if it is password protected.
	 * @throws IOException when a fatal error occurs.
	 * @return {@code true} if it is not password protected, else {@code false}
	 */
	public boolean isPasswordProtected() throws IOException;
	
	/**
	 * Tries to list all of the entries in the file, including directories.
	 * @throws IOException when a fatal error occurs.
	 * @return {@link List} a list of all of the found entries.
	 */
	public List<String> listEntries() throws IOException;
	
	/**
	 * Tries to read the file and determine the amount of entries in the archive.
	 * @throws IOException when a fatal error occurs.
	 * @return the amount of items found in the archive.
	 */
	public int getNumberOfItemsInArchive() throws IOException;
}