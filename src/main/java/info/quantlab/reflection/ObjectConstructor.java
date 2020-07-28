/*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christian-fries.de.
 *
 * Created on 12.04.2020
 */
package info.quantlab.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

public class ObjectConstructor<T> {

	public static <T> T create(Class<?> theClass, Class<T> theInterface) {
		if(!theInterface.isAssignableFrom(theClass)) {
			System.out.println("Your class does not implement the interface " + theInterface.getName());
			throw new IllegalArgumentException("Your class does not implement the interface " + theInterface.getName());
		}

		Constructor<?> vectorConstructor;
		try {
			vectorConstructor = theClass.getConstructor();
		}
		catch(Exception e) {
			System.out.println("Your class does not have a constructor that takes no argument.");
			throw new IllegalArgumentException("Your class does not have a constructor that takes no argument.");
		}

		T vector;
		try {
			vector = (T) vectorConstructor.newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			System.out.println("Your class does not have a constructor that takes no argument.");
			throw new IllegalArgumentException("Your class does not have a constructor that takes no argument.");
		}

		return vector;
	}

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

	public static <T> T create(Class<?> theClass, Class<T> theInterface, List<Class<?>> argumentTypes, List<Object> arguments) {
		if(!theInterface.isAssignableFrom(theClass)) {
			System.out.println("Your class does not implement the interface " + theInterface.getName());
			throw new IllegalArgumentException("Your class does not implement the interface " + theInterface.getName());
		}

		Constructor<?> vectorConstructor;
		try {
			vectorConstructor = theClass.getConstructor(argumentTypes.toArray(new Class<?>[argumentTypes.size()]));
		}
		catch(Exception e) {
			throw new IllegalArgumentException("Your class does not have a constructor that takes an argument of type " + Arrays.deepToString(argumentTypes.toArray()));
		}

		T vector;
		try {
			vector = (T) vectorConstructor.newInstance(arguments.toArray(new Object[arguments.size()]));
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new IllegalArgumentException("Could not create an object of your class. The constructor failed.", e);
		}

		return vector;
	}
}
