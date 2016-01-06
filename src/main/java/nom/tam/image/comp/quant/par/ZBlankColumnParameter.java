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

import nom.tam.fits.header.Compression;
import nom.tam.image.comp.par.CompressColumnParameter;
import nom.tam.image.comp.quant.QuantizeOption;

public final class ZBlankColumnParameter extends CompressColumnParameter<int[], QuantizeOption> {

    public ZBlankColumnParameter(QuantizeOption quantizeOption) {
        super(Compression.ZBLANK_COLUMN, quantizeOption, int[].class);
    }

    private boolean equals(Integer i1, Integer i2) {
        if (i1 == null) {
            return i2 == null;
        }
        return i1.equals(i2);
    }

    @Override
    public void getValueFromColumn(int index) {
        if (this.column != null) {
            getOption().setBNull(this.column[index]);
        } else if (getOption().getOriginal() != null) {
            getOption().setBNull(getOption().getOriginal().getBNull());
        }
    }

    @Override
    public void setValueInColumn(int index) {
        if (getOption().getOriginal() != null && !equals(getOption().getBNull(), getOption().getOriginal().getBNull())) {
            initializedColumn()[index] = getOption().getBNull();
        }
    }
}
