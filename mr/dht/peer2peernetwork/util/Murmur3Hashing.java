package mr.dht.peer2peernetwork.util;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import java.math.BigInteger;

public class Murmur3Hashing {
    private static final long C1 	= 0x87c37b91114253d5L;
    private static final long C2 	= 0x4cf5ad432745937fL;

    private final long seed;

    private long h1;
    private long h2;
    private int length;

    private int partialPos;
    private long partialK1;
    private long partialK2;

    private boolean finished;
    private long finishedH1;
    private long finishedH2;

    public Murmur3Hashing() {
        seed 				= 0;
    }

    public Murmur3Hashing(int seed) {
        this.seed 			= seed & (0xffffffffL); // unsigned
        h1 				= seed;
        h2 				= seed;
    }

    public void update(int b) {
        finished 			= false;
        switch (partialPos) {
            case 0:
                partialK1 		= 0xff & b;
                break;
            case 1:
                partialK1 |= (0xff & b) << 8;
                break;
            case 2:
                partialK1 |= (0xff & b) << 16;
                break;
            case 3:
                partialK1 |= (0xffL & b) << 24;
                break;
            case 4:
                partialK1 |= (0xffL & b) << 32;
                break;
            case 5:
                partialK1 |= (0xffL & b) << 40;
                break;
            case 6:
                partialK1 |= (0xffL & b) << 48;
                break;
            case 7:
                partialK1 |= (0xffL & b) << 56;
                break;
            case 8:
                partialK2 		= 0xff & b;
                break;
            case 9:
                partialK2 |= (0xff & b) << 8;
                break;
            case 10:
                partialK2 |= (0xff & b) << 16;
                break;
            case 11:
                partialK2 |= (0xffL & b) << 24;
                break;
            case 12:
                partialK2 |= (0xffL & b) << 32;
                break;
            case 13:
                partialK2 |= (0xffL & b) << 40;
                break;
            case 14:
                partialK2 |= (0xffL & b) << 48;
                break;
            case 15:
                partialK2 |= (0xffL & b) << 56;
                break;
        }

        partialPos++;
        if (partialPos == 16) {
            applyKs(partialK1, partialK2);
            partialPos 			= 0;
        }
        length++;
    }

    public void update(byte[] b) {
        update(b, 0, b.length);
    }

    public void update(byte[] b, int off, int len) {
        finished 			= false;
        while (partialPos != 0 && len > 0) {
            update(b[off]);
            off++;
            len--;
        }

        int remainder 			= len & 0xF;
        int stop 			= off + len - remainder;
        for (int i = off; i < stop; i += 16) {
            long k1 			= getLongLE(b, i);
            long k2 			= getLongLE(b, i + 8);
            applyKs(k1, k2);
        }
        length 				+= stop - off;

        for (int i = 0; i < remainder; i++) {
            update(b[stop + i]);
        }
    }

    private long getLongLE(byte[] bytes, int index) {
        return (bytes[index] & 0xff) | ((bytes[index + 1] & 0xff) << 8) |
                ((bytes[index + 2] & 0xff) << 16) | ((bytes[index + 3] & 0xffL) << 24) |
                ((bytes[index + 4] & 0xffL) << 32) | ((bytes[index + 5] & 0xffL) << 40) |
                ((bytes[index + 6] & 0xffL) << 48) | (((long) bytes[index + 7]) << 56);
    }

    private void applyKs(long k1, long k2) {
        k1 				*= C1;
        k1 				= Long.rotateLeft(k1, 31);
        k1 				*= C2;
        h1 				^= k1;

        h1 				= Long.rotateLeft(h1, 27);
        h1 				+= h2;
        h1 				= h1 * 5 + 0x52dce729;

        k2 				*= C2;
        k2 				= Long.rotateLeft(k2, 33);
        k2 				*= C1;
        h2 				^= k2;

        h2 				= Long.rotateLeft(h2, 31);
        h2 				+= h1;
        h2 				= h2 * 5 + 0x38495ab5;
    }

    private void checkFinished() {
        if (!finished) {
            finished 			= true;
            finishedH1 			= h1;
            finishedH2 			= h2;
            if (partialPos > 0) {
                if (partialPos > 8) {
                    long k2 		= partialK2 * C2;
                    k2 = Long.rotateLeft(k2, 33);
                    k2 *= C1;
                    finishedH2 ^= k2;
                }
                long k1 = partialK1 * C1;
                k1 = Long.rotateLeft(k1, 31);
                k1 *= C2;
                finishedH1 ^= k1;
            }

            finishedH1 ^= length;
            finishedH2 ^= length;

            finishedH1 += finishedH2;
            finishedH2 += finishedH1;

            finishedH1 = fmix64(finishedH1);
            finishedH2 = fmix64(finishedH2);

            finishedH1 += finishedH2;
            finishedH2 += finishedH1;
        }
    }

    private long fmix64(long k) {
        k ^= k >>> 33;
        k *= 0xff51afd7ed558ccdL;
        k ^= k >>> 33;
        k *= 0xc4ceb9fe1a85ec53L;
        k ^= k >>> 33;
        return k;
    }

    public long getValue() {
        checkFinished();
        return finishedH1;
    }

    /** Returns the higher 64 bits of the 128 bit hash. */
    public long getValueHigh() {
        checkFinished();
        return finishedH2;
    }

    /** Positive value. */
    public BigInteger getValueBigInteger() {
        byte[] bytes = getValueBytesBigEndian();
        return new BigInteger(1, bytes);
    }

    /** Padded with leading 0s to ensure length of 32. */
    public String getValueHexString() {
        checkFinished();
        return getPaddedHexString(finishedH2) + getPaddedHexString(finishedH1);
    }

    private String getPaddedHexString(long value) {
        String string = Long.toHexString(value);
        while (string.length() < 16) {
            string = '0' + string;
        }
        return string;
    }

    public byte[] getValueBytesBigEndian() {
        checkFinished();
        byte[] bytes = new byte[16];
        for (int i = 0; i < 8; i++) {
            bytes[i] = (byte) ((finishedH2 >>> (56 - i * 8)) & 0xff);
        }
        for (int i = 0; i < 8; i++) {
            bytes[8 + i] = (byte) ((finishedH1 >>> (56 - i * 8)) & 0xff);
        }
        return bytes;
    }

    public byte[] getValueBytesLittleEndian() {
        checkFinished();
        byte[] bytes = new byte[16];
        for (int i = 0; i < 8; i++) {
            bytes[i] = (byte) ((finishedH1 >>> (i * 8)) & 0xff);
        }
        for (int i = 0; i < 8; i++) {
            bytes[8 + i] = (byte) ((finishedH2 >>> (i * 8)) & 0xff);
        }
        return bytes;
    }

    public void reset() {
        h1 = seed;
        h2 = seed;
        length = 0;
        partialPos = 0;
        finished = false;
    }

public static void main(String args[]) throws IOException {
	long s =0;
			long t1,t2;
/*	for( int i=0; i<1000; ++i){
		byte[] bytes = ByteStream.intToByteArray(UtilClass.getRandomNumber(0,(int)Math.pow(2, 32)));
		t1 = System.nanoTime();
		
		//hash64(bytes,bytes.length);
		t2 = System.nanoTime();
		s+=(t2-t1);
	}
	System.out.println("Avg: "+(s/1000.0));
*/		
	Murmur3Hashing h = new Murmur3Hashing();
	h.update(23);
	System.out.println(h.getValue());
/*	for( int i=0; i<1000; ++i){
		byte[] bytes = ByteStream.intToByteArray(UtilClass.getRandomNumber(0,(int)Math.pow(2, 32)));
		t1 = System.nanoTime();
		h.update(bytes);
		t2 = System.nanoTime();
		s+=(t2-t1);
	}

	System.out.println("Avg: "+(s/1000.0));
*/}
}
