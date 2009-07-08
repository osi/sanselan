package org.apache.sanselan.formats.hdr;

import java.util.Map;
import java.io.IOException;
import java.io.File;
import java.awt.image.BufferedImage;
import java.awt.*;

import org.apache.sanselan.ImageFormat;
import org.apache.sanselan.ImageParser;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.ImageInfo;
import org.apache.sanselan.common.BinaryConstants;
import org.apache.sanselan.common.IImageMetadata;
import org.apache.sanselan.common.byteSources.ByteSource;

/** @author <a href="mailto:peter@electrotank.com">peter royal</a> */
public class HdrImageParser extends ImageParser {

    public HdrImageParser() {
        setByteOrder( BinaryConstants.BYTE_ORDER_BIG_ENDIAN );
    }

    public String getName() {
        return "Radiance HDR";
    }

    public String getDefaultExtension() {
        return ".hdr";
    }

    protected String[] getAcceptedExtensions() {
        return new String[]{ ".hdr", ".pic" };
    }

    protected ImageFormat[] getAcceptedTypes() {
        return new ImageFormat[]{ ImageFormat.IMAGE_FORMAT_HDR };
    }

    public IImageMetadata getMetadata( ByteSource byteSource, Map params ) throws ImageReadException, IOException {
        throw new UnsupportedOperationException();
    }

    public ImageInfo getImageInfo( ByteSource byteSource, Map params ) throws ImageReadException, IOException {
        throw new UnsupportedOperationException();
    }

    public BufferedImage getBufferedImage( ByteSource byteSource, Map params ) throws ImageReadException, IOException {
        throw new UnsupportedOperationException();
    }

    public Dimension getImageSize( ByteSource byteSource, Map params ) throws ImageReadException, IOException {
        throw new UnsupportedOperationException();
    }

    public byte[] getICCProfileBytes( ByteSource byteSource, Map params ) throws ImageReadException, IOException {
        return null;
    }

    public boolean embedICCProfile( File src, File dst, byte[] profile ) {
        return false;
    }

    public String getXmpXml( ByteSource byteSource, Map params ) throws ImageReadException, IOException {
        return null;
    }
}
