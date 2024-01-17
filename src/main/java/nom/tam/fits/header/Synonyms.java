package nom.tam.fits.header;

import java.util.Arrays;

import nom.tam.fits.header.extra.NOAOExt;
import nom.tam.fits.header.extra.SBFitsExt;
import nom.tam.fits.header.extra.STScIExt;

/*
 * #%L
 * nom.tam FITS library
 * %%
 * Copyright (C) 1996 - 2024 nom-tam-fits
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

/**
 * This enum wil try to list synonyms inside or over different dictionaries. So please use always the highest level
 * keyword you can find.
 *
 * @author Richard van Nieuwenhoven
 */
@SuppressWarnings("deprecation")
public enum Synonyms {

    /** EQUINOX is now preferred over the old EPOCH */
    EQUINOX(Standard.EQUINOX, Standard.EPOCH),

    /** TIMESYS appears in multiple conventions */
    TIMESYS(NOAOExt.TIMESYS, STScIExt.TIMESYS),

    /** RADESYS is now preferred over the old RADECSYS */
    RADESYS(Standard.RADESYS, Standard.RADECSYS),

    /** RESTFRQ is now preferred over the old RESTFREQ */
    RESTFRQ(Standard.RESTFRQ, WCS.RESTFREQ),

    /**
     * Equivalent keywords for column coordinate transformation matrix (PC convention). The shorter form may be required
     * for column indices &gt;99 with alternate coordinate systems.
     * 
     * @since 1.19
     */
    TPn_na(WCS.TPn_na, WCS.TPCn_na),

    /**
     * Equivalent keywords for column coordinate transformation matrix (CD convention). The shorter form may be required
     * for column indices &gt;99 with alternate coordinate systems.
     * 
     * @since 1.19
     */
    TCn_na(WCS.TCn_na, WCS.TCDn_na),

    /**
     * Equivalent keywords for column parameter names. The shorter form may be required for column indices &gt;99 with
     * alternate coordinate systems.
     * 
     * @since 1.19
     */
    TSn_na(WCS.TVn_na, WCS.TPSn_na),

    /**
     * Equivalent keywords for column parameter values. The shorter form may be required for column indices &gt;99 with
     * alternate coordinate systems.
     * 
     * @since 1.19
     */
    TVn_na(WCS.TVn_na, WCS.TPVn_na),

    /**
     * Equivalent keywords for column coordinate transformation matrix (PC convention). The shorter form may be required
     * for column indices &gt;99 with alternate coordinate systems.
     * 
     * @since 1.19
     */
    WCSna(WCS.WCSna, WCS.TWCSna),

    /**
     * Equivalent keywords for array column axis string parameters. The shorter form may be required for column indices
     * &gt;99 with alternate coordinate systems.
     * 
     * @since 1.19
     */
    nSn_na(WCS.nSn_na, WCS.nPSn_na),

    /**
     * Equivalent keywords for array column axis parameter values. The shorter form may be required for column indices
     * &gt;99 with alternate coordinate systems.
     * 
     * @since 1.19
     */
    nVn_na(WCS.nVn_na, WCS.nPVn_na),

    /**
     * EXTNAME and HDUNAME are synonymous, but EXTNAME is part of the standard since 1.19
     */
    EXTNAME(Standard.EXTNAME, DataDescription.HDUNAME),

    /**
     * EXTVER and HDUVER are synonymous, but EXTVER is part of the standard since 1.19
     */
    EXTVER(Standard.EXTVER, DataDescription.HDUVER),

    /**
     * EXTLEVEL and HDULEVEL are synonymous, but EXTLEVEL is part of the standard since 1.19
     */
    EXTLEVEL(Standard.EXTLEVEL, DataDescription.HDULEVEL),

    /**
     * [s] Non-standard exposure time conventions.
     * 
     * @see DateTime#XPOSURE
     */
    EXPOSURE(ObservationDurationDescription.EXPOSURE, ObservationDurationDescription.EXPTIME,
            ObservationDurationDescription.ONTIME),

    /**
     * Variants for recording the start time of observation in HH:MM:SS[.s...] format
     */
    TSTART(DateTime.TSTART, ObservationDurationDescription.TIME_OBS),

    /**
     * Variants for recording the ending time of observation in HH:MM:SS[.s...] format
     */
    TSTOP(DateTime.TSTOP, ObservationDurationDescription.TIME_END),

    /** DARKTIME appears in multiple conventions */
    DARKTIME(NOAOExt.DARKTIME, SBFitsExt.DARKTIME);

    private final IFitsHeader primaryKeyword;

    private final IFitsHeader[] synonyms;

    Synonyms(IFitsHeader primaryKeyword, IFitsHeader... synonyms) {
        this.primaryKeyword = primaryKeyword;
        this.synonyms = synonyms;
    }

    /**
     * Returns the list of equivalent FITS header keywords as an array
     * 
     * @return an array containing the list of equivalent (interchangeable) FITS header keywords
     */
    public IFitsHeader[] getSynonyms() {
        return Arrays.copyOf(synonyms, synonyms.length);
    }

    /**
     * Returns the primary or preferred FITS header keyword to use to provide this information in a FITS header.
     * 
     * @return the primary (or preferred) FITS header keyword form to use
     */
    public IFitsHeader primaryKeyword() {
        return primaryKeyword;
    }

    /**
     * Returns the primary or preferred FITS header keyword to prefer for the given header entry to provide this
     * information in a FITS header.
     * 
     * @param  key the standard or conventional header keyword.
     * 
     * @return        the primary (or preferred) FITS header keyword form to use
     * 
     * @see           #primaryKeyword(String)
     * @see           #primaryKeyword()
     */
    public static IFitsHeader primaryKeyword(IFitsHeader key) {
        for (Synonyms synonym : values()) {
            for (IFitsHeader synHeader : synonym.synonyms) {
                if (synHeader.equals(key)) {
                    return synonym.primaryKeyword();
                }
            }
        }
        return key;
    }

    /**
     * Returns the primary or preferred FITS header keyword to prefer for the given header entry to provide this
     * information in a FITS header.
     * 
     * @param  header the string FITS header keyword
     * 
     * @return        the primary (or preferred) FITS header keyword form to use
     * 
     * @see           #primaryKeyword(IFitsHeader)
     */
    public static String primaryKeyword(String header) {
        for (Synonyms synonym : values()) {
            for (IFitsHeader synHeader : synonym.synonyms) {
                if (synHeader.key().equals(header)) {
                    return synonym.primaryKeyword().key();
                }
            }
        }
        return header;
    }
}
