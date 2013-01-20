/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package home.assetracker.data.util;

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import org.jboss.logging.Logger;

/**
 *
 * @author kenmin
 */
public class FileUtils {

    private static final int FILENAME_LENGTH = 20;
    private static final Logger LOGGER = Logger.getLogger(FileUtils.class);

    /**
     * Writes a bytestream to file and returns the generated file name
     *
     * @param content
     * @param fileName
     * @param path
     * @return
     */
    public static String writeFile(byte[] content, String path) {
        RandomUtil randomUtil = new RandomUtil(FILENAME_LENGTH);
        String fileName = randomUtil.nextString();
        File file = new File(path + File.separator + fileName);
        while (file.exists()) {
            file = new File(path + File.separator + fileName);
        }
        try {
            Files.write(content, file);
            return fileName;
        } catch (IOException e) {
            LOGGER.error("Error writing file.", e);
            return "";
        }
    }

    /**
     * Deletes a file from specified path
     *
     * @param fileName
     * @param path
     * @return
     */
    public static boolean deleteFile(String fileName, String path) {
        boolean returnStatus = false;
        File file = new File(path + File.separator + fileName);
        try {
            returnStatus = file.delete();
        } catch (SecurityException e) {
            LOGGER.error("Error deleting file.", e);
        }
        return returnStatus;
    }
}
