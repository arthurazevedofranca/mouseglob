/*******************************************************************************
 * Copyright (c) 2016 Daniel Coelho de Castro.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Daniel Coelho de Castro - initial API and implementation
 ******************************************************************************/
package dcc.inject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public final class InjectionUtils {

	private InjectionUtils() {
	}

	public static Set<Constructor<?>> getConstructors(Class<?> clazz) {
		Set<Constructor<?>> constructors = new HashSet<>();
		for (Constructor<?> constructor : clazz.getDeclaredConstructors())
			if (constructor.isAnnotationPresent(Inject.class))
				constructors.add(constructor);
		return constructors;
	}

	public static Set<Field> getFields(Class<?> clazz) {
		Set<Field> fields = new HashSet<>();
		for (Field field : clazz.getDeclaredFields())
			if (field.isAnnotationPresent(Inject.class))
				fields.add(field);
		return fields;
	}

	public static Set<Method> getMethods(Class<?> clazz) {
		Set<Method> methods = new HashSet<>();
		for (Method method : clazz.getDeclaredMethods())
			if (method.isAnnotationPresent(Inject.class))
				methods.add(method);
		return methods;
	}

	public static Set<Class<?>> getAllDependencies(Class<?> clazz) {
		Set<Class<?>> dependencies = new HashSet<>();

		for (Constructor<?> constructor : getConstructors(clazz))
			for (Class<?> dependency : constructor.getParameterTypes())
				dependencies.add(dependency);

		for (Field field : getFields(clazz))
			dependencies.add(field.getType());

		for (Method method : getMethods(clazz))
			for (Class<?> dependency : method.getParameterTypes())
				dependencies.add(dependency);

		return dependencies;
	}

	public static <T> Set<Class<? extends T>> getAllDependenciesOfType(
			Class<?> clazz, Class<T> type) {
		Set<Class<?>> dependencies = getAllDependencies(clazz);
		Set<Class<? extends T>> dependenciesOfType = new HashSet<>();
		for (Class<?> dependency : dependencies)
			if (type.isAssignableFrom(dependency))
				dependenciesOfType.add(dependency.asSubclass(type));
		return dependenciesOfType;
	}

        public static Set<Class<?>> getRequiredDependencies(Class<?> clazz) {
                Set<Class<?>> dependencies = new HashSet<>();

                dependencies.addAll(getRequiredConstructorDependencies(clazz));

                for (Field field : getFields(clazz))
                        if (!field.isAnnotationPresent(Optional.class))
                                dependencies.add(field.getType());

		for (Method method : getMethods(clazz)) {
			Class<?>[] types = method.getParameterTypes();
			Annotation[][] annotations = method.getParameterAnnotations();
			for (int i = 0; i < types.length; i++) {
				if (isRequiredParameter(types[i], annotations[i]))
					dependencies.add(types[i]);
			}
		}

                return dependencies;
        }

        public static Set<Class<?>> getRequiredConstructorDependencies(Class<?> clazz) {
                Set<Class<?>> dependencies = new HashSet<>();

                for (Constructor<?> constructor : getConstructors(clazz)) {
                        Class<?>[] types = constructor.getParameterTypes();
                        Annotation[][] annotations = constructor.getParameterAnnotations();
                        for (int i = 0; i < types.length; i++) {
                                if (isRequiredParameter(types[i], annotations[i]))
                                        dependencies.add(types[i]);
                        }
                }

                return dependencies;
        }

	private static boolean isRequiredParameter(Class<?> type,
			Annotation[] annotations) {
		for (int i = 0; i < annotations.length; i++)
			if (annotations[i] instanceof Optional)
				return false;
		return true;
	}

	public static <T> Set<Class<? extends T>> getRequiredDependenciesOfType(
			Class<?> clazz, Class<T> type) {
		Set<Class<?>> dependencies = getAllDependencies(clazz);
		Set<Class<? extends T>> dependenciesOfType = new HashSet<>();
		for (Class<?> dependency : dependencies)
			if (type.isAssignableFrom(dependency))
				dependenciesOfType.add(dependency.asSubclass(type));
		return dependenciesOfType;
	}

	public static @interface Optional {
	}

}
