package net.teamfruit.sushida.player;

import net.teamfruit.sushida.Sushida;
import net.teamfruit.sushida.logic.TypingLogic;
import net.teamfruit.sushida.player.state.IState;
import net.teamfruit.sushida.player.state.TitleState;
import net.teamfruit.sushida.util.Timer;
import org.bukkit.NamespacedKey;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class StateContainer {
    public final PlayerData data;
    public final NamespacedKey bossKey;
    private IState state = new TitleState();
    public Timer timer;
    public int score;
    public int inputCursor;
    public TypingLogic typingLogic = new TypingLogic();
    public int bgmCount = 100;

    public StateContainer(PlayerData data) {
        this.data = data;
        this.bossKey = new NamespacedKey(Sushida.plugin, "bossbar." + data.player.getName());

        timer = new Timer();
        timer.pause();
        timer.reset();
    }

    public IState getState() {
        return state;
    }

    public IState apply(BiFunction<IState, StateContainer, IState> func) {
        IState state = func.apply(this.state, this);
        if (state != null) {
            this.state.onExit(this);
            this.state = state;
            this.state.onEnter(this);
        }
        return this.state;
    }

    public static BiFunction<IState, StateContainer, IState> exclude(Class<?> stateClass, BiFunction<IState, StateContainer, IState> func) {
        return (state, container) -> {
            if (state.getClass().equals(stateClass))
                return null;
            return func.apply(state, container);
        };
    }

    public static <T extends IState> BiFunction<IState, StateContainer, IState> ifClass(Class<T> stateClass, BiFunction<T, StateContainer, IState> func) {
        return (state, container) -> {
            if (stateClass.isInstance(state)) {
                @SuppressWarnings("unchecked")
                T t = (T) state;
                return func.apply(t, container);
            }
            return null;
        };
    }

    public static BiFunction<IState, StateContainer, IState> supply(Supplier<IState> func) {
        return (state, container) -> func.get();
    }
}
