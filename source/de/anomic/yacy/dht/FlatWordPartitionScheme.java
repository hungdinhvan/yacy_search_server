// FlatWordPartitionScheme.java 
// ------------------------------
// part of YaCy
// (C) 2009 by Michael Peter Christen; mc@yacy.net
// first published on http://yacy.net
// Frankfurt, Germany, 28.01.2009
//
// $LastChangedDate: 2009-01-23 16:32:27 +0100 (Fr, 23 Jan 2009) $
// $LastChangedRevision: 5514 $
// $LastChangedBy: orbiter $
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

package de.anomic.yacy.dht;

import de.anomic.kelondro.order.Base64Order;
import de.anomic.yacy.yacySeed;

/**
 * A flat word partition scheme is a metric for words on the range of a distributed
 * hash table. The dht is reflected by a 0..Long.MAX_VALUE integer range, each word gets
 * a number on that range. To compute a number, the hash representation is used to compute
 * the hash position from the first 63 bits of the b64 hash string.
 */
public class FlatWordPartitionScheme implements PartitionScheme {

    public static final FlatWordPartitionScheme std = new FlatWordPartitionScheme();
    
    public FlatWordPartitionScheme() {
        // nothing to initialize
    }

    public int verticalPartitions() {
        return 1;
    }
    
    public long dhtPosition(byte[] wordHash, String urlHash) {
        // the urlHash has no relevance here
        // normalized to Long.MAX_VALUE
        return Base64Order.enhancedCoder.cardinal(wordHash);
    }

    public final long dhtDistance(final byte[] word, final String urlHash, final yacySeed peer) {
        return dhtDistance(word, urlHash, peer.hash.getBytes());
    }
    
    private final long dhtDistance(final byte[] from, final String urlHash, final byte[] to) {
        // the dht distance is a positive value between 0 and 1
        // if the distance is small, the word more probably belongs to the peer
        assert to != null;
        assert from != null;
        final long toPos = dhtPosition(to, null);
        final long fromPos = dhtPosition(from, urlHash);
        return dhtDistance(fromPos, toPos);
    }

    public long dhtPosition(byte[] wordHash, int verticalPosition) {
        return dhtPosition(wordHash, null);
    }

    public long[] dhtPositions(byte[] wordHash) {
        long[] l = new long[1];
        l[1] = dhtPosition(wordHash, null);
        return l;
    }

    public int verticalPosition(String urlHash) {
        return 0; // this is not a method stub, this is actually true for all FlatWordPartitionScheme
    }

    public final static long dhtDistance(final long fromPos, final long toPos) {
        return (toPos >= fromPos) ?
                toPos - fromPos :
                (Long.MAX_VALUE - fromPos) + toPos + 1;
    }
    
    public static byte[] positionToHash(final long l) {
        // transform the position of a peer position into a close peer hash
        String s = new String(Base64Order.enhancedCoder.uncardinal(l));
        while (s.length() < 12) s += "A";
        return s.getBytes();
    }

}
