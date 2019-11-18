package org.oilmod.spi.dependencies;

import org.oilmod.util.LamdbaCastUtils;

import java.util.function.Consumer;

public interface IDependency<Dependency> /*extends Cloneable*/ {
    Consumer<Dependency> getDependencyConsumer();
    Class<Dependency> getDependencyClass();
    boolean checkType(Object candidate);
    default Dependency cast(Object candidate) {
        return LamdbaCastUtils.cast(candidate);
    }
    boolean checkCandidate(Dependency candidate);

    /**
     * Should return true the first time it was called
     * @param candidate
     * @return
     */
    boolean accept(Dependency candidate);

    /*IDependency<Dependency> clone();
    default IDependency cloneIfNeeded() {
        if (getNode()==null) return this;
        IDependency result = clone();
        result.setNode(null);
        return result;
    }*/

}
