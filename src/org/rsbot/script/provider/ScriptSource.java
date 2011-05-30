package org.rsbot.script.provider;

import java.util.List;

import org.rsbot.script.Script;
import org.rsbot.service.ServiceException;

/**
 * @author Jacmob
 */
public interface ScriptSource {

	List<ScriptDefinition> list();

	Script load(ScriptDefinition def) throws ServiceException;

}
