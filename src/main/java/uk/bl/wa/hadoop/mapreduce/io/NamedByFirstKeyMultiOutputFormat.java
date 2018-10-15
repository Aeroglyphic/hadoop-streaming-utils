/**
 * 
 */
package uk.bl.wa.hadoop.mapreduce.io;

import java.util.Arrays;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.lib.MultipleTextOutputFormat;
import org.apache.hadoop.util.UTF8ByteArrayUtils;

/**
 * 
 * Based on:
 * https://stackoverflow.com/questions/18541503/multiple-output-files-for-hadoop-streaming-with-python-mapper#18562328
 * 
 * @author Andrew Jackson <Andrew.Jackson@bl.uk>
 *
 */
public class NamedByFirstKeyMultiOutputFormat
        extends MultipleTextOutputFormat<Text, Text> {

    private byte[] separator = "\t".getBytes();

    /**
     * Use the first part of the key as part of the path for the final output
     * file.
     */
    @Override
    protected String generateFileNameForKeyValue(Text key, Text value,
            String leaf) {
        byte[] keyBytes = key.getBytes();
        int pos = UTF8ByteArrayUtils.findBytes(keyBytes, 0, keyBytes.length,
                separator);
        if( pos == -1 ) {
            return new Path(key.toString(), leaf).toString();
        } else {
            byte[] filename = Arrays.copyOfRange(keyBytes, 0, pos);
            return new Path(filename.toString(), leaf).toString();
        }
    }

    /**
     * We discard the key as per your requirement
     */
    @Override
    protected Text generateActualKey(Text key, Text value) {
        byte[] keyBytes = key.getBytes();
        int pos = UTF8ByteArrayUtils.findBytes(keyBytes, 0, keyBytes.length,
                separator);
        if (pos == -1) {
            return null;
        } else {
            byte[] newKey = Arrays.copyOfRange(keyBytes,
                    pos + separator.length, keyBytes.length);
            return new Text(newKey);
        }
    }
}