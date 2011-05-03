package org.rsbot.security;

import org.rsbot.Application;
import org.rsbot.gui.BotGUI;
import org.rsbot.script.Script;
import org.rsbot.service.ScriptDeliveryNetwork;

import java.io.FileDescriptor;
import java.net.InetAddress;
import java.security.Permission;

/**
 * @author Paris
 */
public class RestrictedSecurityManager extends SecurityManager {
	private String getCallingClass() {
		final String prefix = Application.class.getPackage().getName() + ".";
		for (StackTraceElement s : Thread.currentThread().getStackTrace()) {
			final String name = s.getClassName();
			if (name.startsWith(prefix) && !name.equals(RestrictedSecurityManager.class.getName())) {
				return name;
			}
		}
		return "";
	}

	private boolean isCallerScript() {
		final String name = getCallingClass();
		if (name.isEmpty()) {
			return false;
		}
		return name.startsWith(Script.class.getName());
	}

	public void checkAccept(String host, int port) {
		throw new SecurityException();
	}

	public void checkConnect(String host, int port) {
		if (port == -1 || port == 80 || port == 443) {
			super.checkConnect(host, port);
		} else {
			throw new SecurityException();
		}
	}

	public void checkConnect(String host, int port, Object context) {
		throw new SecurityException();
	}

	public void checkCreateClassLoader() {
		super.checkCreateClassLoader();
	}

	public void checkDelete(String file) {
		if (isCallerScript()) {
			throw new SecurityException();
		} else {
			super.checkDelete(file);
		}
	}

	public void checkExec(String cmd) {
		final String calling = getCallingClass();
		if (calling.equals(ScriptDeliveryNetwork.class.getName()) || calling.equals(BotGUI.class.getName())) {
			super.checkExec(cmd);
		} else {
			throw new SecurityException();
		}
	}

	public void checkExit(int status) {
		final String calling = getCallingClass();
		if (calling.equals(BotGUI.class.getName())) {
			super.checkExit(status);
		} else {
			throw new SecurityException();
		}
	}

	public void checkLink(String lib) {
		super.checkLink(lib);
	}

	public void checkListen(int port) {
		throw new SecurityException();
	}

	public void checkMemberAccess(Class<?> clazz, int which) {
		super.checkMemberAccess(clazz, which);
	}

	public void checkMulticast(InetAddress maddr) {
		throw new SecurityException();
	}

	public void checkMulticast(InetAddress maddr, byte ttl) {
		throw new SecurityException();
	}

	public void checkPackageAccess(String pkg) {
		super.checkPackageAccess(pkg);
	}

	public void checkPackageDefinition(String pkg) {
		super.checkPackageDefinition(pkg);
	}

	public void checkPermission(Permission perm) {
		//super.checkPermission(perm);
	}

	public void checkPermission(Permission perm, Object context) {
		//super.checkPermission(perm, context);
	}

	public void checkPrintJobAccess() {
		throw new SecurityException();
	}

	public void checkPropertiesAccess() {
		super.checkPropertiesAccess();
	}

	public void checkPropertyAccess(String key) {
		super.checkPropertyAccess(key);
	}

	public void checkRead(FileDescriptor fd) {
		if (isCallerScript()) {
			throw new SecurityException();
		}
		super.checkRead(fd);
	}

	public void checkRead(String file) {
		super.checkRead(file);
	}

	public void checkRead(String file, Object context) {
		if (isCallerScript()) {
			throw new SecurityException();
		}
		super.checkRead(file, context);
	}

	public void checkSecurityAccess(String target) {
		super.checkSecurityAccess(target);
	}

	public void checkSetFactory() {
		super.checkSetFactory();
	}

	public void checkSystemClipboardAccess() {
		throw new SecurityException();
	}

	public boolean checkTopLevelWindow(Object window) {
		return super.checkTopLevelWindow(window);
	}

	public void checkWrite(FileDescriptor fd) {
		if (isCallerScript()) {
			throw new SecurityException();
		}
		super.checkWrite(fd);
	}

	public void checkWrite(String file) {
		if (isCallerScript()) {
			throw new SecurityException();
		}
		super.checkWrite(file);
	}
}
