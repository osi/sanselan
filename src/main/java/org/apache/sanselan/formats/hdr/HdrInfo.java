package org.apache.sanselan.formats.hdr;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.common.BinaryConstants;
import org.apache.sanselan.common.BinaryFileFunctions;
import org.apache.sanselan.common.BinaryInputStream;
import org.apache.sanselan.common.IImageMetadata;
import org.apache.sanselan.common.ImageMetadata;
import org.apache.sanselan.common.byteSources.ByteSource;
import org.apache.sanselan.util.Debug;

class HdrInfo extends BinaryFileFunctions {
    private static final Pattern RESOLUTION_STRING = Pattern.compile( "-Y (\\d+) \\+X (\\d+)" );

    private final BinaryInputStream in;
    private ImageMetadata metadata;
    private int width = -1;
    private int height = -1;
    private static final byte[] TWO_TWO = new byte[]{ 0x2, 0x2 };

    HdrInfo( ByteSource byteSource ) throws IOException {
        this.in = new BinaryInputStream( byteSource.getInputStream(), BinaryConstants.BYTE_ORDER_BIG_ENDIAN );
    }

    IImageMetadata getMetadata() throws IOException, ImageReadException {
        if ( null == metadata ) {
            readMetadata();
        }

        return metadata;
    }

    int getWidth() throws IOException, ImageReadException {
        if ( -1 == width ) {
            readDimensions();
        }

        return width;
    }

    int getHeight() throws IOException, ImageReadException {
        if ( -1 == height ) {
            readDimensions();
        }

        return height;
    }

    void close() {
        try {
            in.close();
        } catch( IOException e ) {
            Debug.debug( e );
        }
    }

    private void readDimensions() throws IOException, ImageReadException {
        getMetadata(); // Ensure we've read past this

        InfoHeaderReader reader = new InfoHeaderReader( in );
        String resolution = reader.readLine();
        Matcher matcher = RESOLUTION_STRING.matcher( resolution );

        if ( !matcher.matches() ) {
            throw new ImageReadException(
                "Invalid HDR resolution string. Only \"-Y N +X M\" is supported. Found \"" + resolution + "\"" );
        }

        height = Integer.parseInt( matcher.group( 1 ) );
        width = Integer.parseInt( matcher.group( 2 ) );
    }

    private void readMetadata() throws IOException, ImageReadException {
        in.readAndVerifyBytes( HdrConstants.HEADER, "Not a valid HDR: Incorrect Header" );

        InfoHeaderReader reader = new InfoHeaderReader( in );

        if ( reader.readLine().length() != 0 ) {
            throw new ImageReadException( "Not a valid HDR: Incorrect Header" );
        }

        metadata = new ImageMetadata();

        String info = reader.readLine();

        while ( info.length() != 0 ) {
            int equals = info.indexOf( "=" );

            if ( equals > 0 ) {
                String variable = info.substring( 0, equals );
                String value = info.substring( equals + 1 );

                if ( "FORMAT".equals( value ) ) {
                    if ( !"32-bit_rle_rgbe".equals( value ) ) {
                        throw new ImageReadException(
                            "Only 32-bit_rle_rgbe images are supported, trying to read " + value );
                    }
                }

                metadata.add( variable, value );
            } else {
                metadata.add( "<command>", info );
            }

            info = reader.readLine();
        }
    }

    public double[][] getPixelData() throws IOException, ImageReadException {
        // Read into local variables to ensure that we have seeked into the file far enough
        int height = getHeight();
        int width = getWidth();

        if ( width >= 32768 ) {
            throw new ImageReadException( "Scan lines must be less than 32768 bytes long" );
        }

        byte[] scanLineBytes = convertShortToByteArray( width, BinaryConstants.BYTE_ORDER_BIG_ENDIAN );
        byte[] rgbe = new byte[width * 4];
        double[][] out = new double[3][width * height];

        for ( int i = 0; i < height; i++ ) {
            in.readAndVerifyBytes( TWO_TWO, "Scan line " + i + " expected to start with 0x2 0x2" );
            in.readAndVerifyBytes( scanLineBytes, "Scan line " + i + " length expected" );

            decompress( in, rgbe );

            for ( int channel = 0; channel < 3; channel++ ) {
                int channelOffset = channel * width;
                int eOffset = 3 * width;

                for ( int p = 0; p < width; p++ ) {
                    int mantissa = rgbe[p + eOffset] & 0xff;
                    int pos = p + i * width;
                    
                    if ( 0 == mantissa ) {
                        out[channel][pos] = 0;
                    } else {
                        double mult = Math.pow( 2, mantissa );
                        out[channel][pos] = ( ( rgbe[p + channelOffset] & 0xff ) + 0.5f ) * mult;
                    }
                }
            }
        }

        return out;
    }

    private static void decompress( InputStream in, byte[] out ) throws IOException {
        int position = 0;
        int total = out.length;

        while ( position < total ) {
            int n = in.read();

            if ( n > 128 ) {
                int value = in.read();

                for ( int i = 0; i < ( n & 0x7f ); i++ ) {
                    out[position++] = (byte) value;
                }
            } else {
                for ( int i = 0; i < n; i++ ) {
                    out[position++] = (byte) in.read();
                }
            }
        }
    }
}
