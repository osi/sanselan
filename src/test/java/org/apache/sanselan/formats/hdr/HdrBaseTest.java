package org.apache.sanselan.formats.hdr;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.sanselan.ImageFormat;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.SanselanTest;

public class HdrBaseTest extends SanselanTest {
    private static boolean isHdr( File file ) throws IOException,
                                                     ImageReadException
    {
        ImageFormat format = Sanselan.guessFormat( file );
        return format == ImageFormat.IMAGE_FORMAT_HDR;
    }

    private static final ImageFilter IMAGE_FILTER = new ImageFilter() {
        public boolean accept( File file ) throws IOException, ImageReadException {
            return isHdr( file );
        }
    };

    protected List getHdrImages() throws IOException, ImageReadException {
        return getTestImages( IMAGE_FILTER );
    }
}
