package org.apache.sanselan.formats.hdr;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.sanselan.ImageInfo;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.ImageWriteException;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.common.IImageMetadata;
import org.apache.sanselan.util.Debug;

public class HdrReadTest extends HdrBaseTest {
    public void test() throws IOException, ImageReadException,
                              ImageWriteException
    {
        Debug.debug( "start" );

        List images = getHdrImages();

        for ( int i = 0; i < images.size(); i++ ) {
            if ( i % 10 == 0 ) {
                Debug.purgeMemory();
            }

            File imageFile = (File) images.get( i );
            Debug.debug( "imageFile", imageFile );

            IImageMetadata metadata = Sanselan.getMetadata( imageFile );
			assertNotNull(metadata);

            ImageInfo imageInfo = Sanselan.getImageInfo( imageFile );
            assertNotNull( imageInfo );

            BufferedImage image = Sanselan.getBufferedImage( imageFile );
            assertNotNull( image );
        }
    }
}
