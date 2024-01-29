package com.eaton.platform.integration.endeca.util;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.integration.endeca.constants.EndecaConstants;
import org.apache.commons.lang3.StringUtils;

/**
 * Converter for converting a given size in bytes to human-readable String in the form of 10MB, etc...
 */
public class FileSizeToHumanReadableStringConverter {
    /**
     * Converts from 1024 to 1MB
     * @param size to convert
     * @return the converted string
     */
    public String convert(String size ) {
        if((size!=null) && (!size.equals(StringUtils.EMPTY))){
            // get the file size in Bytes unit
            long fileSize = Long.parseLong(size);
            long sizeOfFile = (long) (fileSize / Math.pow(EndecaConstants.TEN, EndecaConstants.SIX));
            String spaceUnit;
            // set the file size unit
            if (sizeOfFile < 1) {
                sizeOfFile = (long) (fileSize / Math.pow(EndecaConstants.TEN, EndecaConstants.THREE));
                spaceUnit = CommonConstants.KB;
                if (sizeOfFile < 1) {
                    sizeOfFile = fileSize;
                    spaceUnit = CommonConstants.B;
                }
            } else {
                spaceUnit = CommonConstants.MB;
            }
            size = sizeOfFile+ spaceUnit;
        }
        return size;
    }
}
