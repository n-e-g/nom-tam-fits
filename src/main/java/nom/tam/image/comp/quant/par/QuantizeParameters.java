package nom.tam.image.comp.quant.par;

/*
 * #%L
 * nom.tam FITS library
 * %%
 * Copyright (C) 1996 - 2016 nom-tam-fits
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

import nom.tam.image.comp.ICompressColumnParameter;
import nom.tam.image.comp.ICompressHeaderParameter;
import nom.tam.image.comp.ICompressOption;
import nom.tam.image.comp.ICompressParameters;
import nom.tam.image.comp.par.CompressionParameters;
import nom.tam.image.comp.quant.QuantizeOption;

public class QuantizeParameters extends CompressionParameters {

    private final ZQuantizParameter quantz;

    private final ZBlankParameter blank;

    private final ZBlankColumnParameter blankColumn;

    private final ZZeroColumnParameter zero;

    private final ZScaleColumnParameter scale;

    public QuantizeParameters(QuantizeOption option) {
        this.quantz = new ZQuantizParameter(option);
        this.blank = new ZBlankParameter(option);
        this.blankColumn = new ZBlankColumnParameter(option);
        this.zero = new ZZeroColumnParameter(option);
        this.scale = new ZScaleColumnParameter(option);
    }

    @Override
    protected ICompressColumnParameter[] columnParameters() {
        return new ICompressColumnParameter[]{
            this.blankColumn,
            this.zero,
            this.scale
        };
    }

    @Override
    public ICompressParameters copy(ICompressOption original) {
        return copyColumnDetails(new QuantizeParameters((QuantizeOption) original));
    }

    protected ICompressParameters copyColumnDetails(QuantizeParameters quantizeParameters) {
        quantizeParameters.blankColumn.column(this.blankColumn);
        quantizeParameters.zero.column(this.zero);
        quantizeParameters.scale.column(this.scale);
        return quantizeParameters;
    }

    @Override
    protected ICompressHeaderParameter[] headerParameters() {
        if (this.blank.isActive()) {
            return new ICompressHeaderParameter[]{
                this.quantz,
                this.blank
            };
        } else {
            return new ICompressHeaderParameter[]{
                this.quantz
            };
        }
    }
}
