/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2021 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package net.imglib2.script.algorithm;

import net.imglib2.algorithm.fft.FourierTransform;
import net.imglib2.algorithm.fft.InverseFourierTransform;
import net.imglib2.img.Img;
import net.imglib2.script.algorithm.fn.ImgProxy;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.complex.ComplexDoubleType;

/**
 * TODO
 *
 */
public class InverseFFT<T extends RealType<T>> extends ImgProxy<T>
{
	public InverseFFT(final FFT<T> fftImage) throws Exception {
		super(process(fftImage, fftImage.fft));
	}

	public InverseFFT(final Img<ComplexDoubleType> img, final FFT<T> fftImage) throws Exception {
		super(process(img, fftImage.fft));
	}

	static private final <T extends RealType<T>> Img<T> process(final Img<ComplexDoubleType> fftImage, final FourierTransform<T, ComplexDoubleType> fft) throws Exception {
		final InverseFourierTransform<T, ComplexDoubleType> ifft = new InverseFourierTransform<T, ComplexDoubleType>(fftImage, fft);
		if (!ifft.checkInput() || !ifft.process()) {
			throw new Exception("FFT: failed to process for image " + fftImage.getClass() + " -- " + ifft.getErrorMessage());
		}
		return ifft.getResult();
	}
}
