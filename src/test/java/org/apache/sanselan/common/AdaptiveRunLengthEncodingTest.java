/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.sanselan.common;

import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.SanselanTest;

/** @author <a href="mailto:proyal@apache.org">peter royal</a> */
public class AdaptiveRunLengthEncodingTest extends SanselanTest {

    public void testDecompress() throws ImageReadException {
        byte[] expected = toByteArray( new int[]{ 200, 200, 200, 200, 190, 189, 180, 180, 180, 180 } );
        byte[] input = toByteArray( new int[]{ 3, 200, -2, 190, 189, 3, 180 } );

        compareByteArrays( expected, new AdaptiveRunLengthEncoding().decompress( input, expected.length ) );
    }

    private static byte[] toByteArray( int[] values ) {
        byte[] bytes = new byte[values.length];

        for ( int i = 0; i < values.length; i++ ) {
            bytes[i] = (byte) values[i];
        }

        return bytes;
    }
}
