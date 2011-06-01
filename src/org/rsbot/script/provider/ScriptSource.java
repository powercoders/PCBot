package org.rsbot.script.provider;

import org.rsbot.script.Script;
import org.rsbot.service.ServiceException;

import java.util.List;

/**
 */
public interface ScriptSource {

	List<ScriptDefinition> list();

	Script load(ScriptDefinition def) throws ServiceException;

}
