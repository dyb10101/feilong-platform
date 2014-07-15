/*
 * Copyright (C) 2008 feilong (venusdrogon@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.feilong.framework.bind.parse;

import java.lang.reflect.Field;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.feilong.commons.core.lang.reflect.ConstructorUtil;
import com.feilong.commons.core.tools.json.JsonUtil;
import com.feilong.framework.bind.VarCommand;
import com.feilong.framework.bind.annotation.VarName;
import com.feilong.framework.bind.exception.BuildCommandException;

/**
 * The Class VarClassUtil.
 * 
 * @author <a href="mailto:venusdrogon@163.com">feilong</a>
 * @version 1.0.6 2014年5月10日 上午4:16:25
 * @since 1.0.6
 */
public class VarClassUtil{

	/** The Constant log. */
	private static final Logger	log	= LoggerFactory.getLogger(VarClassUtil.class);

	/**
	 * Builds the command.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param modelClass
	 *            the model class
	 * @param varNameAndValueMap
	 *            the var name and value map
	 * @return the t
	 * @throws BuildCommandException
	 *             the build command exception
	 */
	public static <T extends VarCommand> T buildCommand(Class<T> modelClass,Map<String, String> varNameAndValueMap)
			throws BuildCommandException{

		try{
			T t = ConstructorUtil.newInstance(modelClass);

			// 通过反射机制 省却一堆的 set
			// DokuQueryResult dokuQueryResult = new DokuQueryResult();

			Field[] fields = modelClass.getDeclaredFields();
			for (Field field : fields){
				if (field.isAnnotationPresent(VarName.class)){
					VarName varName = field.getAnnotation(VarName.class);
					if (log.isInfoEnabled()){
						String varNameName = varName.name();

						if (log.isDebugEnabled()){
							String fieldName = field.getName();
							log.debug("{}:{}", fieldName, varNameName);
						}

						String value = varNameAndValueMap.get(varNameName);
						field.setAccessible(true);

						// 将指定对象变量上此 Field 对象表示的字段设置为指定的新值。如果底层字段的类型为基本类型，则对新值进行自动解包
						field.set(t, value);
					}
				}
			}
			if (log.isInfoEnabled()){
				log.info("[{}]:{}", modelClass.getName(), JsonUtil.format(t));
			}
			return t;
		}catch (Exception e){
			e.printStackTrace();
			throw new BuildCommandException(e);
		}
	}
}
