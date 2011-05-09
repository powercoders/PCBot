package org.rsbot.service;

import java.util.List;

import org.rsbot.script.Script;

/**
 * @author Jacmob
 */
public interface ScriptSource {

	List<ScriptDefinition> list();

	Script load(ScriptDefinition def) throws ServiceException;

}
