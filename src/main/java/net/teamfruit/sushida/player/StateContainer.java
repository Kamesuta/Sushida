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
    public final NamespacedKey progressKey;

    private IState state = new TitleState();
    public TypingLogic typingLogic;

    public int titleBgmCount = 100;
    public int bgmCount = 100;
    public int ranking = -1;
    public boolean rankingUpdated = false;

    public Timer timer;
    public Timer realTimer;
    public Timer sushiTimer;

    public int scoreCombo;
    public int inputCursor;

    public int moneyCount;
    public int clearCount;
    public int missCount;
    public int typeCount;

    public StateContainer(PlayerData data) {
        this.data = data;
        this.typingLogic = new TypingLogic(data.getGroup());
        this.bossKey = new NamespacedKey(Sushida.plugin, "bossbar." + data.player.getName());
        this.progressKey = new NamespacedKey(Sushida.plugin, "progressbar." + data.player.getName());

        timer = new Timer();
        timer.pause();
        timer.reset();

        realTimer = new Timer();
        realTimer.pause();
        realTimer.reset();

        sushiTimer = new Timer();
        sushiTimer.pause();
        sushiTimer.reset();
    }

    public IState getState() {
        return state;
    }

    public IState apply(BiFunction<IState, StateContainer, IState> func) {
        IState state = func.apply(this.state, this);
        return apply(state);
    }

    private IState apply(IState state) {
        if (state != null) {
            this.state.onExit(this);
            this.state = state;
            IState newState = this.state.onEnter(this);
            if (newState != null)
                return apply(newState);
        }
        return this.state;
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
