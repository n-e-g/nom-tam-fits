package nom.tam.image.comp.gzip;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import nom.tam.image.comp.ITileCompressor;
import nom.tam.util.ArrayFuncs;
import nom.tam.util.ByteBufferInputStream;
import nom.tam.util.ByteBufferOutputStream;
import nom.tam.util.FitsIO;
import nom.tam.util.type.PrimitiveTypeEnum;

/*
 * #%L
 * nom.tam FITS library
 * %%
 * Copyright (C) 1996 - 2015 nom-tam-fits
 * %%
 * This is free and unencumbered software released into the public domain.
 * 
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 * 
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 * #L%
 */

public abstract class GZipCompress<T extends Buffer> implements ITileCompressor<T> {

    /**
     * Byte compress is a special case, the only one that does not extends
     * GZipCompress because it can write the buffer directly.
     */
    public static class ByteGZipCompress extends GZipCompress<ByteBuffer> {

        public ByteGZipCompress() {
            super(1);
            this.nioBuffer = ByteBuffer.wrap(this.buffer);
        }

        @Override
        protected void getPixel(ByteBuffer pixelData, byte[] pixelBytes) {
            this.nioBuffer.put(pixelData);
        }

        @Override
        protected void setPixel(ByteBuffer pixelData, byte[] pixelBytes) {
            pixelData.put(this.nioBuffer);
        }
    }

    public static class IntGZipCompress extends GZipCompress<IntBuffer> {

        protected static final int BYTE_SIZE_OF_INT = 4;

        public IntGZipCompress() {
            super(BYTE_SIZE_OF_INT);
            this.nioBuffer = ByteBuffer.wrap(this.buffer).asIntBuffer();
        }

        @Override
        protected void getPixel(IntBuffer pixelData, byte[] pixelBytes) {
            this.nioBuffer.put(pixelData);
        }

        @Override
        protected void setPixel(IntBuffer pixelData, byte[] pixelBytes) {
            pixelData.put(this.nioBuffer);
        }
    }

    public static class LongGZipCompress extends GZipCompress<LongBuffer> {

        protected static final int BYTE_SIZE_OF_LONG = 8;

        public LongGZipCompress() {
            super(BYTE_SIZE_OF_LONG);
            this.nioBuffer = ByteBuffer.wrap(this.buffer).asLongBuffer();
        }

        @Override
        protected void getPixel(LongBuffer pixelData, byte[] pixelBytes) {
            this.nioBuffer.put(pixelData);
        }

        @Override
        protected void setPixel(LongBuffer pixelData, byte[] pixelBytes) {
            pixelData.put(this.nioBuffer);
        }
    }

    public static class ShortGZipCompress extends GZipCompress<ShortBuffer> {

        protected static final int BYTE_SIZE_OF_SHORT = 2;

        public ShortGZipCompress() {
            super(BYTE_SIZE_OF_SHORT);
            this.nioBuffer = ByteBuffer.wrap(this.buffer).asShortBuffer();
        }

        @Override
        protected void getPixel(ShortBuffer pixelData, byte[] pixelBytes) {
            this.nioBuffer.put(pixelData);
        }

        @Override
        protected void setPixel(ShortBuffer pixelData, byte[] pixelBytes) {
            pixelData.put(this.nioBuffer);
        }
    }

    public static class FloatGZipCompress extends GZipCompress<FloatBuffer> {

        protected static final int BYTE_SIZE_OF_FLOAT = 4;

        public FloatGZipCompress() {
            super(BYTE_SIZE_OF_FLOAT);
            this.nioBuffer = ByteBuffer.wrap(this.buffer).asFloatBuffer();
        }

        @Override
        protected void getPixel(FloatBuffer pixelData, byte[] pixelBytes) {
            this.nioBuffer.put(pixelData);
        }

        @Override
        protected void setPixel(FloatBuffer pixelData, byte[] pixelBytes) {
            pixelData.put(this.nioBuffer);
        }
    }

    public static class DoubleGZipCompress extends GZipCompress<DoubleBuffer> {

        protected static final int BYTE_SIZE_OF_DOUBLE = 8;

        public DoubleGZipCompress() {
            super(BYTE_SIZE_OF_DOUBLE);
            this.nioBuffer = ByteBuffer.wrap(this.buffer).asDoubleBuffer();
        }

        @Override
        protected void getPixel(DoubleBuffer pixelData, byte[] pixelBytes) {
            this.nioBuffer.put(pixelData);
        }

        @Override
        protected void setPixel(DoubleBuffer pixelData, byte[] pixelBytes) {
            pixelData.put(this.nioBuffer);
        }
    }

    private final class TypeConversion {

        private final PrimitiveTypeEnum from;

        private final PrimitiveTypeEnum to;

        private final Buffer fromBuffer;

        private final Buffer toBuffer;

        private final Object fromArray;

        private final Object toArray;

        private TypeConversion(PrimitiveTypeEnum from) {
            this.from = from;
            this.to = PrimitiveTypeEnum.valueOf(GZipCompress.this.primitivSize * FitsIO.BITS_OF_1_BYTE);
            this.toBuffer = GZipCompress.this.nioBuffer;
            this.fromBuffer = from.asTypedBuffer(ByteBuffer.wrap(GZipCompress.this.buffer));
            this.fromArray = from.newArray(DEFAULT_GZIP_BUFFER_SIZE / from.size());
            this.toArray = this.to.newArray(DEFAULT_GZIP_BUFFER_SIZE / this.to.size());
        }

        int copy(int byteCount) {
            this.fromBuffer.rewind();
            this.toBuffer.rewind();
            this.from.getArray(this.fromBuffer, this.fromArray);
            ArrayFuncs.copyInto(this.fromArray, this.toArray);
            this.to.putArray(this.toBuffer, this.toArray);
            return byteCount * this.to.size() / this.from.size();
        }
    }

    private static final int DEFAULT_GZIP_BUFFER_SIZE = 65536;

    protected final int primitivSize;

    protected byte[] buffer = new byte[DEFAULT_GZIP_BUFFER_SIZE];

    protected T nioBuffer;

    private final byte[] sizeArray = new byte[PrimitiveTypeEnum.INT.size()];

    private final IntBuffer sizeBuffer = ByteBuffer.wrap(this.sizeArray).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer();

    public GZipCompress(int primitivSize) {
        this.primitivSize = primitivSize;
    }

    @Override
    public boolean compress(T pixelData, ByteBuffer compressed) {
        this.nioBuffer.rewind();
        int pixelDataLimit = pixelData.limit();
        try (GZIPOutputStream zip = createGZipOutputStream(pixelDataLimit, compressed)) {
            while (pixelData.hasRemaining()) {
                int count = Math.min(pixelData.remaining(), this.nioBuffer.capacity());
                pixelData.limit(pixelData.position() + count);
                getPixel(pixelData, null);
                zip.write(this.buffer, 0, this.nioBuffer.position() * this.primitivSize);
                this.nioBuffer.rewind();
                pixelData.limit(pixelDataLimit);
            }
        } catch (IOException e) {
            throw new IllegalStateException("could not gzip data", e);
        }
        compressed.limit(compressed.position());
        return true;
    }

    protected GZIPInputStream createGZipInputStream(ByteBuffer compressed) throws IOException {
        return new GZIPInputStream(new ByteBufferInputStream(compressed), Math.min(compressed.limit() * 2, DEFAULT_GZIP_BUFFER_SIZE));
    }

    protected GZIPOutputStream createGZipOutputStream(int length, ByteBuffer compressed) throws IOException {
        return new GZIPOutputStream(new ByteBufferOutputStream(compressed), Math.min(length * 2, DEFAULT_GZIP_BUFFER_SIZE));
    }

    @Override
    public void decompress(ByteBuffer compressed, T pixelData) {
        this.nioBuffer.rewind();
        TypeConversion calcPrimitivSize = getGZipBytePix(compressed, pixelData.limit());
        try (GZIPInputStream zip = createGZipInputStream(compressed)) {
            int count;
            while ((count = zip.read(this.buffer)) >= 0) {
                if (calcPrimitivSize != null) {
                    count = calcPrimitivSize.copy(count);
                }
                this.nioBuffer.position(0);
                this.nioBuffer.limit(count / this.primitivSize);
                setPixel(pixelData, null);
            }
        } catch (IOException e) {
            throw new IllegalStateException("could not un-gzip data", e);
        }
    }

    private TypeConversion getGZipBytePix(ByteBuffer compressed, int nrOfPrimitivElements) {
        if (compressed.limit() > FitsIO.BYTES_IN_INTEGER) {
            int oldPosition = compressed.position();
            try {
                compressed.position(compressed.limit() - this.sizeArray.length);
                compressed.get(this.sizeArray);
                int uncompressedSize = this.sizeBuffer.get(0);
                if (uncompressedSize > 0) {
                    compressed.position(oldPosition);
                    int nrOfPixelsInTile = nrOfPrimitivElements;
                    if (uncompressedSize % nrOfPixelsInTile == 0) {
                        int compressedPrimitivSize = uncompressedSize / nrOfPixelsInTile;
                        if (compressedPrimitivSize != this.primitivSize) {
                            return new TypeConversion(PrimitiveTypeEnum.valueOf(compressedPrimitivSize * FitsIO.BITS_OF_1_BYTE));
                        }
                    }
                }
            } finally {
                compressed.position(oldPosition);
            }
        }
        return null;
    }

    protected abstract void getPixel(T pixelData, byte[] pixelBytes);

    protected abstract void setPixel(T pixelData, byte[] pixelBytes);

}
