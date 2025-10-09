package io.zerows.platform.metadata;

import io.zerows.platform.constant.VClassPath;
import io.zerows.platform.constant.VString;
import io.zerows.platform.enums.Environment;
import io.zerows.support.base.UtBase;

/**
 * @author lang : 2023-06-12
 */
public class KPathAtom {
    private final String name;
    private final String path;

    private final String input;

    private final String output;

    private String identifier;

    private Environment environment = Environment.Production;

    private KPathAtom(final String name) {
        this.name = name;
        this.path = VClassPath.init.OOB + VString.SLASH + name;
        this.input = VClassPath.atom.of(name);
        this.output = VClassPath.atom.TARGET;
    }

    public static KPathAtom of(final String name) {
        return new KPathAtom(name);
    }

    public KPathAtom create(final String identifier) {
        final KPathAtom atom = of(this.name);
        atom.bind(this.environment);
        atom.bind(identifier);
        return atom;
    }

    public KPathAtom bind(final Environment environment) {
        this.environment = environment;
        return this;
    }

    public KPathAtom bind(final String identifier) {
        this.identifier = identifier;
        return this;
    }

    public String identifier() {
        return this.identifier;
    }

    public String path() {
        return this.path;
    }

    public String input() {
        return this.input;
    }

    public String output() {
        return UtBase.ioPath(this.output, this.environment);
    }

    public String ui(final String identifier) {
        return VClassPath.init.OOB + VString.SLASH + this.name + VString.SLASH + identifier + VString.SLASH;
    }

    public String atomUi() {
        return VClassPath.init.OOB + VString.SLASH + this.name + VString.SLASH + this.identifier + VString.SLASH;
    }
}
