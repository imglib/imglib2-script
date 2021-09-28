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

package net.imglib2.script;

import ij.ImagePlus;
import ij.io.FileSaver;
import io.scif.config.SCIFIOConfig;
import io.scif.config.SCIFIOConfig.ImgMode;
import io.scif.img.ImgIOException;
import io.scif.img.ImgOpener;
import net.imglib2.exception.ImgLibException;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.ImagePlusAdapter;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

/* TODO license? */

/**
 * A simple wrapper class that is supposed to contain only functions for scripting.
 * 
 * @version 1.0 2010-12-07
 * @see Img
 *
 * @author Johannes Schindelin
 * @author Albert Cardona
 */
public class ImgLib {
	/** @deprecated Use {@link io.scif.img.IO#openImg(String, ImgOptions)}. */
	public static<T extends RealType<T> & NativeType<T>> Img<T> open(final String pathOrURL) throws ImgIOException, IncompatibleTypeException {
		return (Img<T>) new ImgOpener().openImg(pathOrURL);
		// Old:
		//return wrap(IJ.openImage(pathOrURL));
	}

	/** @deprecated Use {@link io.scif.img.IO#openImg(String, ImgOptions)}. */
	@Deprecated
	public static<T extends RealType<T> & NativeType<T>> Img<T> openVirtual(final String pathOrUrl) throws ImgIOException, IncompatibleTypeException {
		return (Img<T>) new ImgOpener().openImg(pathOrUrl, new SCIFIOConfig().imgOpenerSetImgModes( ImgMode.CELL ));
	}

	/** Wrap an ImageJ's {@link ImagePlus} as an Imglib {@link Img} of the appropriate type.
	 * The data is not copied, but merely accessed with a PlanarArrayContainer.
	 * @see ImagePlusAdapter */
	public static<T extends RealType<T> & NativeType<T>> Img<T> wrap(final ImagePlus imp) {
		return ImagePlusAdapter.<T>wrap(imp);
	}

	/** Wrap an Imglib's {@link Img} as an ImageJ's {@link ImagePlus} of the appropriate type.
	 * The data is not copied, but accessed with a special-purpose VirtualStack subclass. 
	 * @throws ImgLibException */
	static public final <T extends RealType<T>> ImagePlus wrap(final Img<T> img) throws ImgLibException {
		//return new ImagePlusImgFactory<T>().create(img, img.firstElement().createVariable()).getImagePlus();
		return wrap(img, "");
	}
	
	/** Wrap an Imglib's {@link Img} as an ImageJ's {@link ImagePlus} of the appropriate type.
	 * The data is not copied, but accessed with a special-purpose VirtualStack subclass. 
	 * @throws ImgLibException */
	static public final <T extends RealType<T>> ImagePlus wrap(final Img<T> img, final String title) throws ImgLibException {
		//ImagePlus imp = new ImagePlusImgFactory<T>().create(img, img.firstElement().createVariable()).getImagePlus();
		//imp.setTitle(title);
		//return imp;
		return ImageJFunctions.wrap(img, title);
	}
	
	/** Wrap an Imglib's {@link Img} as an ImageJ's {@link ImagePlus} of the appropriate type.
	 * The data is not copied, but accessed with a special-purpose VirtualStack subclass. 
	 * @throws ImgLibException */
	static public final <T extends RealType<T>> ImagePlus show(final Img<T> img) throws ImgLibException {
		return show(img, "");
	}
	
	/** Wrap an Imglib's {@link Img} as an ImageJ's {@link ImagePlus} of the appropriate type.
	 * The data is not copied, but accessed with a special-purpose VirtualStack subclass. 
	 * @throws ImgLibException */
	static public final <T extends RealType<T>> ImagePlus show(final Img<T> img, final String title) throws ImgLibException {
		final ImagePlus imp = wrap(img, title);
		imp.show();
		return imp;
	}

	/** Save an image in the appropriate file format according to
	 * the filename extension specified in {@param path}. 
	 * @throws ImgLibException */
	public static final <T extends RealType<T> & NativeType<T>> boolean save(final Img<T> image, final String path) throws ImgLibException {
		final int dot = path.lastIndexOf('.');
		if (dot < 0 || path.length() - dot - 1 > 4)
			throw new RuntimeException("Could not infer file type from filename: " + path);
		return save(image, path.substring(dot + 1), path);
	}

	/** Save an image in the format specified by {@param fileType}, which can be any of:
	 *  "tif", "tiff", "zip", "gif", "jpg", "jpeg", "bmp", "pgm", "png", "raw".
	 *  
	 *  When saving as TIFF, if the image has more than 2 dimensions, it will be saved
	 *  as a stack. 
	 * @throws ImgLibException */
	public static<T extends RealType<T> & NativeType<T>> boolean save(final Img<T> image, String fileType, final String path) throws ImgLibException {
		// TODO: use LOCI for this
		final ImagePlus imp = wrap(image);
		final FileSaver saver = new FileSaver(imp);
		fileType = fileType.toLowerCase();
		if (fileType.equals("tif") || fileType.equals("tiff")) {
			if (image.numDimensions() > 2) {
				return saver.saveAsTiffStack(path);
			}
			return saver.saveAsTiff(path);
		} else if (fileType.equals("zip"))
			return saver.saveAsZip(path);
		else if (fileType.equals("gif"))
			return saver.saveAsGif(path);
		else if (fileType.equals("jpg") || fileType.equals("jpeg"))
			return saver.saveAsJpeg(path);
		else if (fileType.equals("bmp"))
			return saver.saveAsBmp(path);
		else if (fileType.equals("pgm"))
			return saver.saveAsPgm(path);
		else if (fileType.equals("png"))
			return saver.saveAsPng(path);
		else if (fileType.equals("raw"))
			return saver.saveAsRaw(path);
		else
			throw new RuntimeException("Unknown fileformat: " + fileType);
	}
}
