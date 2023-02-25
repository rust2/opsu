package kww.useless.interfaces;

import fluddokt.opsu.fake.GameContainer;

/** Should indicate that implementing classes are inited outside of a constructor */
public interface IInitable {
    default void init() {}

    default void init(GameContainer container) {}
}
