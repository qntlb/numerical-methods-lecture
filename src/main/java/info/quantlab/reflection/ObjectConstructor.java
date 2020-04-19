/*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christian-fries.de.
 *
 * Created on 12.04.2020
 */
package info.quantlab.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ObjectConstructor<T> {
	
	public static <T> T create(Class<?> theClass, Class<T> theInterface, Object argument) {
		if(!theInterface.isAssignableFrom(theClass)) {
			System.out.println("Your class does not implement the interface " + theInterface.getName());
			throw new IllegalArgumentException("Your class does not implement the interface " + theInterface.getName());
		}

		Constructor<?> vectorConstructor;
		try {
			vectorConstructor = theClass.getConstructor(argument.getClass());
		}
		catch(Exception e) {
			System.out.println("Your class does not have a constructor that takes an argument of type " + argument.getClass().getCanonicalName());
			throw new IllegalArgumentException("Your class does not have a constructor that takes an argument of type " + argument.getClass().getCanonicalName());
		}
		
		T vector;
		try {
			vector = (T) vectorConstructor.newInstance(argument);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			System.out.println("Could not create an object of your class. The constructor failed.");
			throw new IllegalArgumentException("Could not create an object of your class. The constructor failed.", e);
		}

		return vector;
	}
}
