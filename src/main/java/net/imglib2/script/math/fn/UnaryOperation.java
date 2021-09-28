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

package net.imglib2.script.math.fn;

import java.util.Collection;

import net.imglib2.IterableRealInterval;
import net.imglib2.RealCursor;
import net.imglib2.type.numeric.RealType;

/* An abstract class to facilitate implementing a function that takes one argument.
 * Subclasses must call one of the three constructors: for an {@link Image}, an {@link IFunction},
 * or for a {@link Number}.
 * 
 * Here is an example. Suppose you want a function that adds 42:
 * 
 <code>
 import net.imglib2.scripting.math.fn.IFunction;
 import net.imglib2.scripting.math.fn.UnaryOperation;
 import net.imglib2.image.Image;
 
 public class Add42 extends UnaryOperation {
     public Add42(Image<? extends RealType<?>> img) {
         super(img);
     }
     public Add42(IFunction fn) {
         super(fn);
     }
     public Add42(Number val) {
         super(val);
     }
     
     public final double eval() {
         return a() + 42;
     }
 }
 </code>
 *
 * The new Add42 function created above will interact with any other of the math functions,
 * or with any other class implementing IFunction.
 *
 */
public abstract class UnaryOperation extends FloatImageOperation
{
	private final IFunction a;

	public <R extends RealType<R>> UnaryOperation(final IterableRealInterval<R> img) {
		this.a = new ImageFunction<R>(img);
	}

	public UnaryOperation(final IFunction fn) {
		this.a = fn;
	}

	public UnaryOperation(final Number val) {
		this.a = new NumberFunction(val);
	}

	@Override
	public final void findCursors(final Collection<RealCursor<?>> cursors) {
		a.findCursors(cursors);
	}

	/** Call a().eval() to obtain the result as a double of the computation encapsulated by the {@field a}. 
	 *  @returns the IFunction {@field a} */
	public final IFunction a() { return a; }
	
	@Override
	public IFunction duplicate() throws Exception
	{
		return getClass().getConstructor(IFunction.class).newInstance(a.duplicate());
	}
	
	@Override
	public void findImgs(final Collection<IterableRealInterval<?>> iris)
	{
		a.findImgs(iris);
	}
}
