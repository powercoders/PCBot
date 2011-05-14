package org.rsbot.script.web;

import org.rsbot.script.Script;
import org.rsbot.script.methods.MethodContext;
import org.rsbot.script.methods.MethodProvider;
import org.rsbot.script.wrappers.RSPath;
import org.rsbot.script.wrappers.RSTile;

import java.util.Map;

public class RouteStep extends MethodProvider {
    private final Type type;
    private RSTile[] path = null;
    private RSPath rspath = null;
    private Teleport teleport = null;

    public static enum Type {
        PATH, TELEPORT
    }

    public RouteStep(final MethodContext ctx, final Object step) {
        super(ctx);
        if (step instanceof Teleport) {
            this.type = Type.TELEPORT;
            this.teleport = (Teleport) step;
        } else if (step instanceof RSTile[]) {
            this.type = Type.PATH;
            this.path = (RSTile[]) step;
        } else if (step instanceof RSTile) {
            this.type = Type.PATH;
            this.path = new RSTile[]{(RSTile) step};
        } else {
            throw new IllegalArgumentException("Step is of an invalid type!");
        }
    }

    public boolean execute() {
        switch (type) {
            case PATH:
                if (rspath == null && path != null) {
                    rspath = methods.walking.newTilePath(path);
                }
                while (scriptActive()) {
                    if (rspath == null || !rspath.isValid() || !rspath.traverse() || methods.calc.distanceTo(rspath.getEnd()) < 5) {
                        break;
                    }
                    sleep(random(50, 150));
                }
                return scriptActive() && methods.calc.distanceTo(rspath.getEnd()) < 5;
            case TELEPORT:
                return teleport != null && teleport.perform();
            default:
                return false;
        }
    }

    public Teleport getTeleport() {
        return teleport;
    }

    public RSTile[] getPath() {
        return path;
    }

    private boolean scriptActive() {
        Map<Integer, Script> scriptsMap = methods.bot.getScriptHandler().getRunningScripts();
        Script[] scripts = scriptsMap.values().toArray(new Script[scriptsMap.values().size()]);
        for (final Script script : scripts) {
            if (script.getBot().equals(methods.bot)) {
                if (!script.isActive()) {
                    return false;
                }
            }
        }
        return true;
    }

}
