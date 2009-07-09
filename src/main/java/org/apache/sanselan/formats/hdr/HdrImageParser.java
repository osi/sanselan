package org.apache.sanselan.formats.hdr;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BandedSampleModel;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferDouble;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.sanselan.ImageFormat;
import org.apache.sanselan.ImageInfo;
import org.apache.sanselan.ImageParser;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.common.BinaryConstants;
import org.apache.sanselan.common.IImageMetadata;
import org.apache.sanselan.common.byteSources.ByteSource;

/**
 * Parser for Radiance HDR images
 *
 * @author <a href="mailto:peter@electrotank.com">peter royal</a>
 */
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
        HdrInfo info = new HdrInfo( byteSource );

        try {
            return info.getMetadata();
        } finally {
            info.close();
        }
    }

    public ImageInfo getImageInfo( ByteSource byteSource, Map params ) throws ImageReadException, IOException {
        HdrInfo info = new HdrInfo( byteSource );

        try {
            return new ImageInfo( getName(),
                                  32, // todo may be 64 if double?
                                  new ArrayList(),
                                  ImageFormat.IMAGE_FORMAT_HDR,
                                  getName(),
                                  info.getHeight(),
                                  "image/vnd.radiance",
                                  1,
                                  -1,
                                  -1,
                                  -1,
                                  -1,
                                  info.getWidth(),
                                  false,
                                  false,
                                  false,
                                  ImageInfo.COLOR_TYPE_RGB,
                                  "Adaptive RLE" );
        } finally {
            info.close();
        }
    }

    public BufferedImage getBufferedImage( ByteSource byteSource, Map params ) throws ImageReadException, IOException {
        HdrInfo info = new HdrInfo( byteSource );

        try {
            // It is necessary to create our own BufferedImage here as the
            // org.apache.sanselan.common.IBufferedImageFactory interface does not expose this complexity
            DataBuffer buffer = new DataBufferDouble( info.getPixelData(), info.getWidth() * info.getHeight() );

            return new BufferedImage(
                new ComponentColorModel( ColorSpace.getInstance( ColorSpace.CS_sRGB ),
                                         false,
                                         false,
                                         Transparency.OPAQUE,
                                         buffer.getDataType() ),
                Raster.createWritableRaster( new BandedSampleModel( buffer.getDataType(),
                                                                    info.getWidth(),
                                                                    info.getHeight(),
                                                                    3 ),
                                             buffer,
                                             new Point() ),
                false,
                null );
        } finally {
            info.close();
        }
    }

    public Dimension getImageSize( ByteSource byteSource, Map params ) throws ImageReadException, IOException {
        HdrInfo info = new HdrInfo( byteSource );

        try {
            return new Dimension( info.getWidth(), info.getHeight() );
        } finally {
            info.close();
        }
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
